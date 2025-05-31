package io.github.mortuusars.horseman.world.calling;

import io.github.mortuusars.horseman.Horseman;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HorseCallingStorage extends SavedData {
    protected final Map<UUID, Map<ResourceKey<Instrument>, StoredBoundHorse>> boundHorses;
    protected final List<UUID> unboundHorses;
    protected final List<UUID> horsesToRemove;

    private HorseCallingStorage(Map<UUID, Map<ResourceKey<Instrument>, StoredBoundHorse>> boundHorses,
                                List<UUID> unboundHorses, List<UUID> horsesToRemove) {
        this.boundHorses = boundHorses;
        this.unboundHorses = unboundHorses;
        this.horsesToRemove = horsesToRemove;
    }

    private HorseCallingStorage() {
        this(new HashMap<>(), new ArrayList<>(), new ArrayList<>());
    }

    // --

    public Map<UUID, Map<ResourceKey<Instrument>, StoredBoundHorse>> getBoundHorses() {
        return boundHorses;
    }

    public List<UUID> getUnboundHorses() {
        return unboundHorses;
    }

    public List<UUID> getHorsesToRemove() {
        return horsesToRemove;
    }


    // --

    public static @NotNull HorseCallingStorage loadOrCreate(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(HorseCallingStorage.factory(), "horseman_horse_calling");
    }

    private static Factory<HorseCallingStorage> factory() {
        return new Factory<>(HorseCallingStorage::new, HorseCallingStorage::load, null);
    }

    private static HorseCallingStorage load(CompoundTag tag, HolderLookup.Provider provider) {
        try {
            CompoundTag boundHorsesTag = tag.getCompound("BoundHorses");
            Map<UUID, Map<ResourceKey<Instrument>, StoredBoundHorse>> boundHorses = new HashMap<>();
            for (String uuidStr : boundHorsesTag.getAllKeys()) {
                UUID uuid = UUID.fromString(uuidStr);
                CompoundTag innerTag = boundHorsesTag.getCompound(uuidStr);

                Map<ResourceKey<Instrument>, StoredBoundHorse> innerMap = new HashMap<>();
                for (String instrumentString : innerTag.getAllKeys()) {
                    ResourceLocation location = ResourceLocation.parse(instrumentString);
                    ResourceKey<Instrument> instrumentKey = ResourceKey.create(Registries.INSTRUMENT, location);

                    CompoundTag horseTag = innerTag.getCompound(instrumentString);
                    @Nullable StoredBoundHorse horse = StoredBoundHorse.load(horseTag);

                    if (horse != null) {
                        innerMap.put(instrumentKey, horse);
                    }
                }

                boundHorses.put(uuid, innerMap);
            }

            ArrayList<UUID> unboundHorses = new ArrayList<>();
            ListTag unboundHorsesListTag = tag.getList("UnboundHorses", Tag.TAG_INT_ARRAY);
            for (Tag unboundUUID : unboundHorsesListTag) {
                unboundHorses.add(NbtUtils.loadUUID(unboundUUID));
            }

            ArrayList<UUID> horsesToRemove = new ArrayList<>();
            ListTag horsesToRemoveListTag = tag.getList("HorsesToRemove", Tag.TAG_INT_ARRAY);
            for (Tag toRemoveUUID : horsesToRemoveListTag) {
                horsesToRemove.add(NbtUtils.loadUUID(toRemoveUUID));
            }

            return new HorseCallingStorage(boundHorses, unboundHorses, horsesToRemove);
        } catch (Exception e) {
            Horseman.LOGGER.error("Failed to load HorseCallingStorage: ", e);
            return new HorseCallingStorage();
        }
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag boundHorsesTag = new CompoundTag();
        for (Map.Entry<UUID, Map<ResourceKey<Instrument>, StoredBoundHorse>> outerEntry : boundHorses.entrySet()) {
            UUID uuid = outerEntry.getKey();
            Map<ResourceKey<Instrument>, StoredBoundHorse> innerMap = outerEntry.getValue();

            CompoundTag innerTag = new CompoundTag();
            for (Map.Entry<ResourceKey<Instrument>, StoredBoundHorse> innerEntry : innerMap.entrySet()) {
                ResourceKey<Instrument> key = innerEntry.getKey();
                StoredBoundHorse horse = innerEntry.getValue();

                CompoundTag horseTag = horse.save(new CompoundTag());

                innerTag.put(key.location().toString(), horseTag);
            }

            boundHorsesTag.put(uuid.toString(), innerTag);
        }
        tag.put("BoundHorses", boundHorsesTag);

        ListTag unboundHorsesListTag = new ListTag();
        for (UUID uuid : unboundHorses) {
            unboundHorsesListTag.add(NbtUtils.createUUID(uuid));
        }
        tag.put("UnboundHorses", unboundHorsesListTag);

        ListTag entitiesToRemoveListTag = new ListTag();
        for (UUID uuid : horsesToRemove) {
            entitiesToRemoveListTag.add(NbtUtils.createUUID(uuid));
        }
        tag.put("HorsesToRemove", entitiesToRemoveListTag);

        return tag;
    }
}
