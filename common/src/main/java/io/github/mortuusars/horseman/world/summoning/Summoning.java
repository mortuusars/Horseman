package io.github.mortuusars.horseman.world.summoning;

import com.google.common.base.Preconditions;
import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.world.HitchableHorse;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Summoning {
    protected final SummoningStorage storage;

    public Summoning(MinecraftServer server) {
        storage = server.getDataStorage().computeIfAbsent(SummoningStorage.TYPE);
    }

    public SummoningStorage getStorage() {
        return storage;
    }

    public boolean isBound(AbstractHorse horse) {
        return horse.getHorsemanBoundData() != null;
    }

    public @Nullable StoredBoundHorse getHorseBoundTo(UUID owner) {
        return getStorage().getBoundHorses().get(owner);
    }

    public @Nullable StoredBoundHorse getHorseBoundTo(Player owner) {
        return getHorseBoundTo(owner.getUUID());
    }

    protected void addOrUpdateBoundHorse(AbstractHorse horse) {
        @Nullable BoundData data = horse.getHorsemanBoundData();
        if (data == null) {
            Horseman.LOGGER.warn("Tried to update a horse that does not have a bound data. Horse: {}", horse);
            return;
        }
        getStorage().getBoundHorses().put(data.owner(), new StoredBoundHorse(horse));
        getStorage().setDirty();
        Horseman.LOGGER.debug("Updated Stored Horse to [{}]", horse.getUUID());
    }

    // --

    public void bind(ServerLevel level, AbstractHorse horse, Player player) {
        unbindExistingHorse(level, player);
        horse.setHorsemanBoundData(new BoundData(player));
        addOrUpdateBoundHorse(horse);
    }

    public void unbindHorse(ServerLevel level, StoredBoundHorse boundHorse) {
        boundHorse.boundData().ifPresent(data -> {
            getStorage().getBoundHorses().remove(data.owner());
        });
        if (tryFindLoadedHorse(level, boundHorse.uuid()) instanceof AbstractHorse loadedHorse) {
            loadedHorse.setHorsemanBoundData(null);
        } else {
            getStorage().getUnboundHorses().add(boundHorse.uuid());
        }
        getStorage().setDirty();
    }

    public void unbindExistingHorse(ServerLevel level, Player player) {
        @Nullable StoredBoundHorse boundHorse = getHorseBoundTo(player);
        if (boundHorse != null) {
            unbindHorse(level, boundHorse);
        }
    }

    // -- Calling

    public HorseSummoningResult summonBoundHorseTo(ServerPlayer player) {
        ServerLevel level = player.level();
        @Nullable StoredBoundHorse boundHorse = getHorseBoundTo(player);

        if (boundHorse == null) return HorseSummoningResult.NO_BOUND_HORSE;
        if (boundHorse.isDead()) return HorseSummoningResult.HORSE_IS_DEAD;

        @Nullable AbstractHorse existingHorse = tryFindLoadedHorse(level, boundHorse.uuid());
        if (existingHorse != null) {
            if (!isBound(existingHorse)) {
                return HorseSummoningResult.ERROR_HORSE_IS_NOT_BOUND;
            }

            addOrUpdateBoundHorse(existingHorse); // Update before calling to have the latest state
            boundHorse = getHorseBoundTo(player); // Re-query updated state
            Preconditions.checkNotNull(boundHorse); // Should not be null as we check if the horse isBound above.
        }

        if (!dimensionsAreValid(player, boundHorse)) return HorseSummoningResult.INVALID_DIMENSION;
        if (!isInRange(player, boundHorse)) return HorseSummoningResult.TOO_FAR;

        if (existingHorse != null && canWalkInsteadOfResummoning(player, existingHorse)) {
            return walkToPlayer(player, existingHorse);
        }

        return summonHorse(player, boundHorse);
    }

    protected HorseSummoningResult walkToPlayer(ServerPlayer player, AbstractHorse horse) {
        // Leashed horses would not react to calling. So we are unhitching to allow it to move.
        // (but only hitched, regular leashed will stay, to not drop leash far away from player)
        if (horse instanceof HitchableHorse hitchableHorse && HitchableHorse.isHitched(hitchableHorse)) {
            horse.removeLeash();
        }

        AttributeInstance followRangeAttribute = horse.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRangeAttribute != null) {
            followRangeAttribute.setBaseValue(Config.Server.HORSE_SUMMONING_MAX_WALKING_DISTANCE.get());
        }

        horse.getNavigation().moveTo(player, Config.Server.HORSE_SUMMONING_WALK_MOVEMENT_SPEED.get());
        addOrUpdateBoundHorse(horse);

        Horseman.CriteriaTriggers.HORSE_SUMMONED.get().trigger(player, horse);

        return HorseSummoningResult.SUCCESS;
    }

    protected HorseSummoningResult summonHorse(ServerPlayer player, @NotNull StoredBoundHorse boundHorse) {
        ServerLevel level = player.level();

        ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), boundHorse.tag());
        Optional<EntityType<?>> type = EntityType.by(input);
        if (type.isEmpty()) {
            Horseman.LOGGER.error("Failed to get the type from a stored boundHorse data. " +
                    "'id' probably wasn't saved properly. Tag '{}'.", boundHorse.tag());
            return HorseSummoningResult.ERROR_ENTITY_NOT_CREATED;
        }

        @Nullable Entity entity = type.get().create(player.level(), EntitySpawnReason.MOB_SUMMONED);
        if (!(entity instanceof AbstractHorse newHorse)) {
            Horseman.LOGGER.error("Created entity isn't an AbstractHorse but {}. Something went wrong.", entity);
            return HorseSummoningResult.ERROR_ENTITY_NOT_CREATED;
        }

        newHorse.load(input);
        newHorse.setUUID(UUID.randomUUID());
        newHorse.setPos(player.getX(), player.getY(), player.getZ());

        if (!hasSpaceFor(level, player, newHorse)) return HorseSummoningResult.NO_SPACE;

        removeOldBoundHorse(level, boundHorse);
        addOrUpdateBoundHorse(newHorse);

        player.level().addFreshEntity(newHorse);
        Horseman.CriteriaTriggers.HORSE_SUMMONED.get().trigger(player, newHorse);

        Horseman.LOGGER.debug("{} has summoned Horse [{}]", player.getScoreboardName(), newHorse.getUUID());

        return HorseSummoningResult.SUCCESS;
    }

    // -- Conditions

    protected boolean dimensionsAreValid(Player player, StoredBoundHorse boundHorse) {
        return switch (Config.Server.HORSE_SUMMONING_DIMENSION_HANDLING.get()) {
            case ANY -> true;
            case SAME -> boundHorse.isInSameDimension(player);
            case WHITELIST -> {
                String playerDimension = player.level().dimension().identifier().toString();
                yield Config.Server.HORSE_SUMMONING_DIMENSIONS.get().stream()
                        .anyMatch(dimension -> dimension.equals(playerDimension));
            }
            case BLACKLIST -> {
                String playerDimension = player.level().dimension().identifier().toString();
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
            horse.setHorsemanBoundData(null); // Remove data to avoid re-updating in onHorseUnloaded
            getStorage().getHorsesToRemove().remove(entityUUID);
            getStorage().getUnboundHorses().remove(entityUUID);
            getStorage().setDirty();
            horse.ejectPassengers();
            Horseman.LOGGER.debug("Horse [{}] is discarded", entityUUID);
            return true; // Prevent loading
        }

        if (getStorage().getUnboundHorses().contains(entityUUID)) {
            horse.setHorsemanBoundData(null);
            getStorage().getUnboundHorses().remove(entityUUID);
            getStorage().setDirty();
            Horseman.LOGGER.debug("Unbound Horse [{}] data is cleared.", entityUUID);
        }

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
                Horseman.LOGGER.info("Loaded Bound Horse [{}] is discarded", boundHorse.uuid());
                removed = true;
                break;
            }
        }

        if (!removed) {
            // Old entity will be removed when it tries to load:
            getStorage().getHorsesToRemove().add(boundHorse.uuid());
            getStorage().setDirty();
            Horseman.LOGGER.info("Bound Horse [{}] is scheduled to be removed next time it loads into the world.", boundHorse.uuid());
        }
    }
}
