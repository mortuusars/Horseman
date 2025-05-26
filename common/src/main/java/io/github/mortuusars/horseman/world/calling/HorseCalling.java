package io.github.mortuusars.horseman.world.calling;

import com.google.common.base.Preconditions;
import io.github.mortuusars.horseman.Horseman;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HorseCalling {
    private static final Map<UUID, Map<ResourceKey<Instrument>, StoredBoundHorse>> BOUND_HORSES = new HashMap<>();
    private static final List<UUID> UNBOUND_HORSES = new ArrayList<>();
    private static final List<UUID> ENTITIES_TO_REMOVE = new ArrayList<>();

    public static boolean isBound(AbstractHorse horse) {
        return horse.getHorsemanBoundData() != null;
    }

    public static @Nullable HorseCalling.StoredBoundHorse getBoundHorse(UUID owner, ResourceKey<Instrument> instrument) {
        return BOUND_HORSES.getOrDefault(owner, Collections.emptyMap()).get(instrument);
    }

    public static @Nullable HorseCalling.StoredBoundHorse getBoundHorse(Player owner, ResourceKey<Instrument> instrument) {
        return getBoundHorse(owner.getUUID(), instrument);
    }

    public static Map<ResourceKey<Instrument>, StoredBoundHorse> getStoredHorsesOf(UUID owner) {
        return BOUND_HORSES.computeIfAbsent(owner, id -> new HashMap<>());
    }

    public static Map<ResourceKey<Instrument>, StoredBoundHorse> getStoredHorsesOf(Player owner) {
        return getStoredHorsesOf(owner.getUUID());
    }

    private static void addOrUpdateBoundHorse(AbstractHorse horse) {
        @Nullable BoundData data = horse.getHorsemanBoundData();
        if (data == null) {
            Horseman.LOGGER.warn("Tried to update a horse that does not have a bound data. Horse: {}", horse);
            return;
        }
        getStoredHorsesOf(data.owner()).put(data.instrument(), new StoredBoundHorse(horse, horse.isDeadOrDying()));
    }

    // --

    public static void bind(ServerLevel level, AbstractHorse horse, Player player, ResourceKey<Instrument> instrument) {
        unbindExistingHorse(level, player, instrument);
        horse.setHorsemanBoundData(new BoundData(player, instrument));
        addOrUpdateBoundHorse(horse);
        player.displayClientMessage(Component.translatable("gui.horseman.calling.bound_successfully"), true);
    }

    public static void unbindHorse(ServerLevel level, StoredBoundHorse boundHorse) {
        getStoredHorsesOf(boundHorse.getCallableData().owner()).remove(boundHorse.getCallableData().instrument());
        tryFindLoadedHorse(level, boundHorse.getEntityUuid()).ifPresentOrElse(
                existingHorse -> existingHorse.setHorsemanBoundData(null),
                () -> UNBOUND_HORSES.add(boundHorse.getEntityUuid()));
    }

    public static void unbindExistingHorse(ServerLevel level, Player player, ResourceKey<Instrument> instrument) {
        @Nullable HorseCalling.StoredBoundHorse boundHorse = getBoundHorse(player, instrument);
        if (boundHorse != null) {
            unbindHorse(level, boundHorse);
        }
    }

    // --

    public static boolean call(ServerLevel level, Player player, ResourceKey<Instrument> instrument) {
        @Nullable HorseCalling.StoredBoundHorse boundHorse = getBoundHorse(player, instrument);

        if (boundHorse == null) {
            player.displayClientMessage(Component.translatable("gui.horseman.calling.cannot_call.no_bound_horse"), true);
            return false;
        }

        if (boundHorse.isDead()) {
            player.displayClientMessage(Component.translatable("gui.horseman.calling.cannot_call.dead"), true);
            return false;
        }

        //TODO: check for space
        //TODO: check for dimension
        //TODO: check max calling distance

        //TODO: check in other dimensions
        //TODO: run to player if nearby

        // Check if already loaded and tp to the player
        if (level.getEntity(boundHorse.getEntityUuid()) instanceof AbstractHorse existingHorse) {
            if (!isBound(existingHorse)) {
                player.displayClientMessage(Component.translatable("gui.horseman.calling.cannot_call.horse_is_not_bound"), true);
                return false;
            }

            if (player.distanceTo(existingHorse) < 32) {
                return callNearbyHorse(player, existingHorse);
            } else {
                // Prepare for resummoning
                addOrUpdateBoundHorse(existingHorse); // Update before calling to have the latest state
                boundHorse = getBoundHorse(player, instrument); // Re-query updated state
                Preconditions.checkNotNull(boundHorse); // Should not be null as we check if the horse isBound above.
            }
        }

        Optional<EntityType<?>> type = EntityType.by(boundHorse.getTag());
        if (type.isEmpty()) {
            Horseman.LOGGER.error("Failed to get the type from a stored boundHorse data. 'id' probably wasn't saved properly. Tag '{}'.", boundHorse.getTag());
            player.displayClientMessage(Component.translatable("gui.horseman.calling.cannot_call.entity_not_created"), true);
            return false;
        }

        @Nullable Entity entity = type.get().create(player.level());
        if (!(entity instanceof AbstractHorse newHorse)) {
            Horseman.LOGGER.error("Created entity isn't an AbstractHorse but {}. Something went wrong.", entity);
            player.displayClientMessage(Component.translatable("gui.horseman.calling.cannot_call.entity_not_created"), true);
            return false;
        }

        newHorse.load(boundHorse.getTag());
        newHorse.setUUID(UUID.randomUUID());
        newHorse.setPos(player.getX(), player.getY(), player.getZ());
        player.level().addFreshEntity(newHorse);

        removeOldBoundHorse(level, boundHorse);
        addOrUpdateBoundHorse(newHorse);

        return true;
    }

    private static boolean callNearbyHorse(Player player, AbstractHorse horse) {
        // horse.setPos(player.getX(), player.getY(), player.getZ());
        horse.teleportTo(player.getX(), player.getY(), player.getZ());
        addOrUpdateBoundHorse(horse);
        return true;
    }

    // -- Events

    public static boolean horseLoaded(ServerLevel level, AbstractHorse horse) {
        if (!isBound(horse)) return false;

        UUID entityUUID = horse.getUUID();

        if (ENTITIES_TO_REMOVE.contains(entityUUID)) {
            ENTITIES_TO_REMOVE.remove(entityUUID);
            UNBOUND_HORSES.remove(entityUUID);
            return true; // Prevent loading
        }

        if (UNBOUND_HORSES.contains(entityUUID)) {
            horse.setHorsemanBoundData(null);
            UNBOUND_HORSES.remove(entityUUID);
        }

        return false;
    }

    // Also called when horse has died.
    public static void horseUnloaded(ServerLevel level, AbstractHorse horse) {
        if (isBound(horse)) {
            addOrUpdateBoundHorse(horse);
        }
    }

    // -- Util

    private static Optional<AbstractHorse> tryFindLoadedHorse(ServerLevel level, UUID entityUuid) {
        for (ServerLevel dimension : level.getServer().getAllLevels()) {
            if (dimension.getEntity(entityUuid) instanceof AbstractHorse horse) {
                return Optional.of(horse);
            }
        }
        return Optional.empty();
    }

    private static void removeOldBoundHorse(ServerLevel level, @NotNull StoredBoundHorse boundHorse) {
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
            ENTITIES_TO_REMOVE.add(boundHorse.getEntityUuid());
        }
    }

    // --

    public static class StoredBoundHorse {
        protected final BoundData boundData;
        protected final CompoundTag tag;
        protected final UUID entityUuid;
        protected final boolean isDead;

        public StoredBoundHorse(AbstractHorse horse, boolean dead) {
            boundData = horse.getHorsemanBoundData();
            tag = saveToTag(horse);
            entityUuid = horse.getUUID();
            isDead = dead;
        }

        public static CompoundTag saveToTag(AbstractHorse horse) {
            CompoundTag tag = new CompoundTag();
            tag.putString("id", EntityType.getKey(horse.getType()).toString());
            horse.saveWithoutId(tag);
            return tag;
        }

        public BoundData getCallableData() {
            return boundData;
        }

        public CompoundTag getTag() {
            return tag;
        }

        public UUID getEntityUuid() {
            return entityUuid;
        }

        public boolean isDead() {
            return isDead;
        }
    }
}
