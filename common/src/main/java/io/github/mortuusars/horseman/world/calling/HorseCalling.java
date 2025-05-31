package io.github.mortuusars.horseman.world.calling;

import com.google.common.base.Preconditions;
import io.github.mortuusars.horseman.Horseman;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HorseCalling {
    protected final HorseCallingStorage storage;

    public HorseCalling(MinecraftServer server) {
        storage = HorseCallingStorage.loadOrCreate(server);
    }

    public HorseCallingStorage getStorage() {
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

    private void addOrUpdateBoundHorse(AbstractHorse horse) {
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
        if (boundHorse.getBoundData() != null) {
            getStoredHorsesOf(boundHorse.getBoundData().owner()).remove(boundHorse.getBoundData().instrument());
        }
        if (tryFindLoadedHorse(level, boundHorse.getEntityUuid()) instanceof AbstractHorse loadedHorse) {
            loadedHorse.setHorsemanBoundData(null);
        } else {
            getStorage().getUnboundHorses().add(boundHorse.getEntityUuid());
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

    public HorseCallResult call(ServerLevel level, Player player, ResourceKey<Instrument> instrument) {
        @Nullable StoredBoundHorse boundHorse = getBoundHorse(player, instrument);

        if (boundHorse == null) return HorseCallResult.NO_BOUND_HORSE;
        if (boundHorse.isDead()) return HorseCallResult.HORSE_IS_DEAD;

        @Nullable AbstractHorse existingHorse = tryFindLoadedHorse(level, boundHorse.getEntityUuid());
        if (existingHorse != null) {
            if (!isBound(existingHorse)) {
                return HorseCallResult.ERROR_HORSE_IS_NOT_BOUND;
            }

            addOrUpdateBoundHorse(existingHorse); // Update before calling to have the latest state
            boundHorse = getBoundHorse(player, instrument); // Re-query updated state
            Preconditions.checkNotNull(boundHorse); // Should not be null as we check if the horse isBound above.
        }

        if (!dimensionsAreValid(player, boundHorse)) return HorseCallResult.INVALID_DIMENSION;
        if (!isInRange(player, boundHorse)) return HorseCallResult.TOO_FAR;

        if (existingHorse != null && canWalkInsteadOfResummoning(player, existingHorse)) {
            return walkToPlayer(level, player, existingHorse);
        }

        return summonHorse(level, player, boundHorse);
    }

    private boolean hasSpaceFor(ServerLevel level, Player player, AbstractHorse horse) {
        StoredBoundHorse boundHorse = new StoredBoundHorse(horse);
        Optional<EntityType<?>> type = EntityType.by(boundHorse.getTag());
        if (type.isEmpty()) return false;

        @Nullable Entity entity = type.get().create(player.level());
        if (!(entity instanceof AbstractHorse newHorse)) return false;

        newHorse.load(boundHorse.getTag());
        newHorse.setUUID(UUID.randomUUID());
        newHorse.setPos(player.getX(), player.getY(), player.getZ());

        return !newHorse.isInWall();
    }

    private boolean isInRange(Player player, StoredBoundHorse boundHorse) {
        //TODO: config for max range
        return true;
    }

    private HorseCallResult walkToPlayer(ServerLevel level, Player player, AbstractHorse horse) {
        AttributeInstance followRangeAttribute = horse.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRangeAttribute != null) {
            followRangeAttribute.setBaseValue(16);
        }

        horse.getNavigation().moveTo(player, 2);
        addOrUpdateBoundHorse(horse);
        return HorseCallResult.SUCCESS;
    }

    private HorseCallResult summonHorse(ServerLevel level, Player player, @NotNull StoredBoundHorse boundHorse) {
        Optional<EntityType<?>> type = EntityType.by(boundHorse.getTag());
        if (type.isEmpty()) {
            Horseman.LOGGER.error("Failed to get the type from a stored boundHorse data. 'id' probably wasn't saved properly. Tag '{}'.", boundHorse.getTag());
            return HorseCallResult.ERROR_ENTITY_NOT_CREATED;
        }

        @Nullable Entity entity = type.get().create(player.level());
        if (!(entity instanceof AbstractHorse newHorse)) {
            Horseman.LOGGER.error("Created entity isn't an AbstractHorse but {}. Something went wrong.", entity);
            return HorseCallResult.ERROR_ENTITY_NOT_CREATED;
        }

        newHorse.load(boundHorse.getTag());
        newHorse.setUUID(UUID.randomUUID());
        newHorse.setPos(player.getX(), player.getY(), player.getZ());

        if (!hasSpaceFor(level, player, newHorse)) return HorseCallResult.NO_SPACE;

        player.level().addFreshEntity(newHorse);

        removeOldBoundHorse(level, boundHorse);
        addOrUpdateBoundHorse(newHorse);

        return HorseCallResult.SUCCESS;
    }

    // -- Conditions

    private boolean dimensionsAreValid(Player player, StoredBoundHorse boundHorse) {
        //TODO: Config for dimensions
        return boundHorse.isInSameDimension(player);
    }

    private boolean canWalkInsteadOfResummoning(Player player, AbstractHorse horse) {
        return player.level().dimension().equals(horse.level().dimension()) && player.distanceTo(horse) < 16;
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
        }
    }

    // -- Util

    private @Nullable AbstractHorse tryFindLoadedHorse(ServerLevel level, UUID entityUuid) {
        for (ServerLevel dimension : level.getServer().getAllLevels()) {
            if (dimension.getEntity(entityUuid) instanceof AbstractHorse horse) {
                return horse;
            }
        }
        return null;
    }

    private void removeOldBoundHorse(ServerLevel level, @NotNull StoredBoundHorse boundHorse) {
        boolean removed = false;

        // Remove already loaded horse immediately:
        for (ServerLevel dimension : level.getServer().getAllLevels()) {
            @Nullable Entity existingHorse = dimension.getEntity(boundHorse.getEntityUuid());
            if (existingHorse != null) {
                existingHorse.discard();
                removed = true;
                break;
            }
        }

        if (!removed) {
            // Old entity will be removed when it tries to load:
            getStorage().getHorsesToRemove().add(boundHorse.getEntityUuid());
        }

        getStorage().setDirty();
    }
}
