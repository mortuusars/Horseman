package io.github.mortuusars.horseman.world.summoning;

import com.google.common.base.Preconditions;
import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.world.HitchableHorse;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Summoning {
    protected final SummoningStorage storage;

    public Summoning(MinecraftServer server) {
        storage = server.overworld().getDataStorage().computeIfAbsent(SummoningStorage.TYPE);
    }

    public SummoningStorage getStorage() {
        return storage;
    }

    public boolean isBound(AbstractHorse horse) {
        return horse.getHorsemanBoundData() != null;
    }

    public @Nullable StoredBoundHorse getBoundHorse(UUID owner, ResourceKey<Instrument> instrument) {
        return getStorage().getBoundHorses().getOrDefault(owner, Collections.emptyMap()).get(instrument);
    }

    public @Nullable StoredBoundHorse getBoundHorse(Player owner, ResourceKey<Instrument> instrument) {
        return getBoundHorse(owner.getUUID(), instrument);
    }

    public Map<ResourceKey<Instrument>, StoredBoundHorse> getStoredHorsesOf(UUID owner) {
        return getStorage().getBoundHorses().computeIfAbsent(owner, id -> new HashMap<>());
    }

    public Map<ResourceKey<Instrument>, StoredBoundHorse> getStoredHorsesOf(Player owner) {
        return getStoredHorsesOf(owner.getUUID());
    }

    protected void addOrUpdateBoundHorse(AbstractHorse horse) {
        @Nullable BoundData data = horse.getHorsemanBoundData();
        if (data == null) {
            Horseman.LOGGER.warn("Tried to update a horse that does not have a bound data. Horse: {}", horse);
            return;
        }
        getStoredHorsesOf(data.owner()).put(data.instrument(), new StoredBoundHorse(horse));
        getStorage().setDirty();
    }

    // --

    public void bind(ServerLevel level, AbstractHorse horse, Player player, ResourceKey<Instrument> instrument) {
        unbindExistingHorse(level, player, instrument);
        horse.setHorsemanBoundData(new BoundData(player, instrument));
        addOrUpdateBoundHorse(horse);
    }

    public void unbindHorse(ServerLevel level, StoredBoundHorse boundHorse) {
        boundHorse.boundData().ifPresent(data -> {
            getStoredHorsesOf(data.owner()).remove(data.instrument());
        });
        if (tryFindLoadedHorse(level, boundHorse.uuid()) instanceof AbstractHorse loadedHorse) {
            loadedHorse.setHorsemanBoundData(null);
        } else {
            getStorage().getUnboundHorses().add(boundHorse.uuid());
        }
        getStorage().setDirty();
    }

    public void unbindExistingHorse(ServerLevel level, Player player, ResourceKey<Instrument> instrument) {
        @Nullable StoredBoundHorse boundHorse = getBoundHorse(player, instrument);
        if (boundHorse != null) {
            unbindHorse(level, boundHorse);
        }
    }

    // -- Calling

    public CallResult call(ServerPlayer player, ResourceKey<Instrument> instrument) {
        ServerLevel level = player.level();
        @Nullable StoredBoundHorse boundHorse = getBoundHorse(player, instrument);

        if (boundHorse == null) return CallResult.NO_BOUND_HORSE;
        if (boundHorse.isDead()) return CallResult.HORSE_IS_DEAD;

        @Nullable AbstractHorse existingHorse = tryFindLoadedHorse(level, boundHorse.uuid());
        if (existingHorse != null) {
            if (!isBound(existingHorse)) {
                return CallResult.ERROR_HORSE_IS_NOT_BOUND;
            }

            addOrUpdateBoundHorse(existingHorse); // Update before calling to have the latest state
            boundHorse = getBoundHorse(player, instrument); // Re-query updated state
            Preconditions.checkNotNull(boundHorse); // Should not be null as we check if the horse isBound above.
        }

        if (!dimensionsAreValid(player, boundHorse)) return CallResult.INVALID_DIMENSION;
        if (!isInRange(player, boundHorse)) return CallResult.TOO_FAR;

        if (existingHorse != null && canWalkInsteadOfResummoning(player, existingHorse)) {
            return walkToPlayer(player, existingHorse);
        }

        return summonHorse(player, boundHorse);
    }

    protected CallResult walkToPlayer(ServerPlayer player, AbstractHorse horse) {
        // Leashed horses would not react to calling. So we are unhitching to allow it to move.
        // (but only hitched, regular leashed will stay, to not drop leash far away from player)
        if (horse instanceof HitchableHorse hitchableHorse && HitchableHorse.isHitched(hitchableHorse)) {
            horse.dropLeash(true, false);
        }

        AttributeInstance followRangeAttribute = horse.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRangeAttribute != null) {
            followRangeAttribute.setBaseValue(Config.Server.HORSE_SUMMONING_MAX_WALKING_DISTANCE.get());
        }

        horse.getNavigation().moveTo(player, Config.Server.HORSE_SUMMONING_WALK_MOVEMENT_SPEED.get());
        addOrUpdateBoundHorse(horse);

        Horseman.CriteriaTriggers.HORSE_SUMMONED.get().trigger(player, horse);

        return CallResult.SUCCESS;
    }

    protected CallResult summonHorse(ServerPlayer player, @NotNull StoredBoundHorse boundHorse) {
        ServerLevel level = player.level();

        ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), boundHorse.tag());
        Optional<EntityType<?>> type = EntityType.by(input);
        if (type.isEmpty()) {
            Horseman.LOGGER.error("Failed to get the type from a stored boundHorse data. " +
                    "'id' probably wasn't saved properly. Tag '{}'.", boundHorse.tag());
            return CallResult.ERROR_ENTITY_NOT_CREATED;
        }

        @Nullable Entity entity = type.get().create(player.level(), EntitySpawnReason.MOB_SUMMONED);
        if (!(entity instanceof AbstractHorse newHorse)) {
            Horseman.LOGGER.error("Created entity isn't an AbstractHorse but {}. Something went wrong.", entity);
            return CallResult.ERROR_ENTITY_NOT_CREATED;
        }

        newHorse.load(input);
        newHorse.setUUID(UUID.randomUUID());
        newHorse.setPos(player.getX(), player.getY(), player.getZ());

        if (!hasSpaceFor(level, player, newHorse)) return CallResult.NO_SPACE;

        player.level().addFreshEntity(newHorse);

        removeOldBoundHorse(level, boundHorse);
        addOrUpdateBoundHorse(newHorse);

        Horseman.CriteriaTriggers.HORSE_SUMMONED.get().trigger(player, newHorse);

        return CallResult.SUCCESS;
    }

    // -- Conditions

    protected boolean dimensionsAreValid(Player player, StoredBoundHorse boundHorse) {
        return switch (Config.Server.HORSE_SUMMONING_DIMENSION_HANDLING.get()) {
            case ANY -> true;
            case SAME -> boundHorse.isInSameDimension(player);
            case WHITELIST -> {
                String playerDimension = player.level().dimension().location().toString();
                yield Config.Server.HORSE_SUMMONING_DIMENSIONS.get().stream()
                        .anyMatch(dimension -> dimension.equals(playerDimension));
            }
            case BLACKLIST -> {
                String playerDimension = player.level().dimension().location().toString();
                yield Config.Server.HORSE_SUMMONING_DIMENSIONS.get().stream()
                        .noneMatch(dimension -> dimension.equals(playerDimension));
            }
        };
    }

    protected boolean isInRange(Player player, StoredBoundHorse boundHorse) {
        int maxDistance = Config.Server.HORSE_SUMMONING_MAX_DISTANCE.get();
        if (maxDistance < 0) return true;
        int distance = (int) boundHorse.position().distanceTo(player.position());
        return distance <= maxDistance;
    }

    protected boolean canWalkInsteadOfResummoning(Player player, AbstractHorse horse) {
        return player.level().dimension().equals(horse.level().dimension())
                && player.distanceTo(horse) < Config.Server.HORSE_SUMMONING_MAX_WALKING_DISTANCE.get();
    }

    protected boolean hasSpaceFor(ServerLevel level, Player player, AbstractHorse horse) {
        StoredBoundHorse boundHorse = new StoredBoundHorse(horse);
        ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), boundHorse.tag());
        Optional<EntityType<?>> type = EntityType.by(input);
        if (type.isEmpty()) return false;

        @Nullable Entity entity = type.get().create(player.level(), EntitySpawnReason.MOB_SUMMONED);
        if (!(entity instanceof AbstractHorse newHorse)) return false;

        newHorse.load(input);
        newHorse.setUUID(UUID.randomUUID());
        newHorse.setPos(player.getX(), player.getY(), player.getZ());

        return !newHorse.isInWall();
    }

    // -- Events

    public boolean onHorseLoaded(ServerLevel level, AbstractHorse horse) {
        if (!isBound(horse)) return false;

        UUID entityUUID = horse.getUUID();

        if (getStorage().getHorsesToRemove().contains(entityUUID)) {
            getStorage().getHorsesToRemove().remove(entityUUID);
            getStorage().getUnboundHorses().remove(entityUUID);
            horse.ejectPassengers();
            return true; // Prevent loading
        }

        if (getStorage().getUnboundHorses().contains(entityUUID)) {
            horse.setHorsemanBoundData(null);
            getStorage().getUnboundHorses().remove(entityUUID);
        }

        getStorage().setDirty();

        return false;
    }

    // Also called when horse has died.
    public void onHorseUnloaded(ServerLevel level, AbstractHorse horse) {
        if (isBound(horse)) {
            addOrUpdateBoundHorse(horse);
            if (horse.isDeadOrDying() && horse.getCustomName() == null) {
                Horseman.LOGGER.info("Bound horse has died at [{}, {}, {}].", (int)horse.getX(), (int)horse.getY(), (int)horse.getZ());
            }
        }
    }

    // -- Util

    protected @Nullable AbstractHorse tryFindLoadedHorse(ServerLevel level, UUID entityUuid) {
        for (ServerLevel dimension : level.getServer().getAllLevels()) {
            if (dimension.getEntity(entityUuid) instanceof AbstractHorse horse) {
                return horse;
            }
        }
        return null;
    }

    protected void removeOldBoundHorse(ServerLevel level, @NotNull StoredBoundHorse boundHorse) {
        boolean removed = false;

        // Remove already loaded horse immediately:
        for (ServerLevel dimension : level.getServer().getAllLevels()) {
            @Nullable Entity existingHorse = dimension.getEntity(boundHorse.uuid());
            if (existingHorse != null) {
                existingHorse.discard();
                removed = true;
                break;
            }
        }

        if (!removed) {
            // Old entity will be removed when it tries to load:
            getStorage().getHorsesToRemove().add(boundHorse.uuid());
        }

        getStorage().setDirty();
    }
}
