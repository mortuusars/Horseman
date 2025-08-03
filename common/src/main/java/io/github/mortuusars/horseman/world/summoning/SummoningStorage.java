package io.github.mortuusars.horseman.world.summoning;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.*;

public class SummoningStorage extends SavedData {
    public static final Codec<Map<ResourceKey<Instrument>, StoredBoundHorse>> PLAYER_HORSES_CODEC =
            Codec.unboundedMap(ResourceKey.codec(Registries.INSTRUMENT), StoredBoundHorse.CODEC);

    public static final Codec<SummoningStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(UUIDUtil.CODEC, PLAYER_HORSES_CODEC).optionalFieldOf("bound_horses", new HashMap<>()).forGetter(SummoningStorage::getBoundHorses),
            Codec.list(UUIDUtil.CODEC).optionalFieldOf("unbound_horses", new ArrayList<>()).forGetter(SummoningStorage::getUnboundHorses),
            Codec.list(UUIDUtil.CODEC).optionalFieldOf("horses_to_remove", new ArrayList<>()).forGetter(SummoningStorage::getHorsesToRemove)
    ).apply(instance, SummoningStorage::new));

    @SuppressWarnings("DataFlowIssue")
    public static final SavedDataType<SummoningStorage> TYPE = new SavedDataType<>(
            "horseman_horse_calling",
            SummoningStorage::new,
            CODEC,
            null // Thanks mojang for mod-friendly code
    );

    protected final Map<UUID, Map<ResourceKey<Instrument>, StoredBoundHorse>> boundHorses;
    protected final List<UUID> unboundHorses;
    protected final List<UUID> horsesToRemove;

    private SummoningStorage(Map<UUID, Map<ResourceKey<Instrument>, StoredBoundHorse>> boundHorses,
                             List<UUID> unboundHorses, List<UUID> horsesToRemove) {
        this.boundHorses = boundHorses;
        this.unboundHorses = unboundHorses;
        this.horsesToRemove = horsesToRemove;
        setDirty();
    }

    private SummoningStorage() {
        this(new HashMap<>(), new ArrayList<>(), new ArrayList<>());
        setDirty();
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

//    public static @NotNull SummoningStorage loadOrCreate(MinecraftServer server) {
//        return server.overworld().getDataStorage().computeIfAbsent(SummoningStorage.factory(), "horseman_horse_calling");
//    }

//    private static DataProvider.Factory<SummoningStorage> factory() {
//        return new DataProvider.Factory<>(SummoningStorage::new, SummoningStorage::load, null);
//    }

//    private static SummoningStorage load(CompoundTag tag, HolderLookup.Provider provider) {
//        try {
//            CompoundTag boundHorsesTag = tag.getCompound("BoundHorses");
//            Map<UUID, Map<ResourceKey<Instrument>, StoredBoundHorse>> boundHorses = new HashMap<>();
//            for (String uuidStr : boundHorsesTag.getAllKeys()) {
//                UUID uuid = UUID.fromString(uuidStr);
//                CompoundTag innerTag = boundHorsesTag.getCompound(uuidStr);
//
//                Map<ResourceKey<Instrument>, StoredBoundHorse> innerMap = new HashMap<>();
//                for (String instrumentString : innerTag.getAllKeys()) {
//                    ResourceLocation location = ResourceLocation.parse(instrumentString);
//                    ResourceKey<Instrument> instrumentKey = ResourceKey.create(Registries.INSTRUMENT, location);
//
//                    CompoundTag horseTag = innerTag.getCompound(instrumentString);
//                    @Nullable StoredBoundHorse horse = StoredBoundHorse.load(horseTag);
//
//                    if (horse != null) {
//                        innerMap.put(instrumentKey, horse);
//                    }
//                }
//
//                boundHorses.put(uuid, innerMap);
//            }
//
//            ArrayList<UUID> unboundHorses = new ArrayList<>();
//            ListTag unboundHorsesListTag = tag.getList("UnboundHorses", Tag.TAG_INT_ARRAY);
//            for (Tag unboundUUID : unboundHorsesListTag) {
//                unboundHorses.add(NbtUtils.loadUUID(unboundUUID));
//            }
//
//            ArrayList<UUID> horsesToRemove = new ArrayList<>();
//            ListTag horsesToRemoveListTag = tag.getList("HorsesToRemove", Tag.TAG_INT_ARRAY);
//            for (Tag toRemoveUUID : horsesToRemoveListTag) {
//                horsesToRemove.add(NbtUtils.loadUUID(toRemoveUUID));
//            }
//
//            return new SummoningStorage(boundHorses, unboundHorses, horsesToRemove);
//        } catch (Exception e) {
//            Horseman.LOGGER.error("Failed to load HorseCallingStorage: ", e);
//            return new SummoningStorage();
//        }
//    }
//
//    @Override
//    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
//        CompoundTag boundHorsesTag = new CompoundTag();
//        for (Map.Entry<UUID, Map<ResourceKey<Instrument>, StoredBoundHorse>> outerEntry : boundHorses.entrySet()) {
//            UUID uuid = outerEntry.getKey();
//            Map<ResourceKey<Instrument>, StoredBoundHorse> innerMap = outerEntry.getValue();
//
//            CompoundTag innerTag = new CompoundTag();
//            for (Map.Entry<ResourceKey<Instrument>, StoredBoundHorse> innerEntry : innerMap.entrySet()) {
//                ResourceKey<Instrument> key = innerEntry.getKey();
//                StoredBoundHorse horse = innerEntry.getValue();
//
//                CompoundTag horseTag = horse.save(new CompoundTag());
//
//                innerTag.put(key.location().toString(), horseTag);
//            }
//
//            boundHorsesTag.put(uuid.toString(), innerTag);
//        }
//        tag.put("BoundHorses", boundHorsesTag);
//
//        ListTag unboundHorsesListTag = new ListTag();
//        for (UUID uuid : unboundHorses) {
//            unboundHorsesListTag.add(NbtUtils.createUUID(uuid));
//        }
//        tag.put("UnboundHorses", unboundHorsesListTag);
//
//        ListTag entitiesToRemoveListTag = new ListTag();
//        for (UUID uuid : horsesToRemove) {
//            entitiesToRemoveListTag.add(NbtUtils.createUUID(uuid));
//        }
//        tag.put("HorsesToRemove", entitiesToRemoveListTag);
//
//        return tag;
//    }
}
