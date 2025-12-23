package io.github.mortuusars.horseman.world.summoning;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.*;

public class SummoningStorage extends SavedData {
    public static final Codec<SummoningStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(UUIDUtil.STRING_CODEC, StoredBoundHorse.CODEC)
                  .optionalFieldOf("bound_horses", new HashMap<>())
                  .forGetter(SummoningStorage::getBoundHorses),
            Codec.list(UUIDUtil.CODEC)
                  .optionalFieldOf("unbound_horses", new ArrayList<>())
                  .forGetter(SummoningStorage::getUnboundHorses),
            Codec.list(UUIDUtil.CODEC)
                  .optionalFieldOf("horses_to_remove", new ArrayList<>())
                  .forGetter(SummoningStorage::getHorsesToRemove)
    ).apply(instance, SummoningStorage::new));

    @SuppressWarnings("DataFlowIssue")
    public static final SavedDataType<SummoningStorage> TYPE = new SavedDataType<>(
            "horseman_horse_calling",
            SummoningStorage::new,
            CODEC,
            null // Thanks mojang for mod-friendly code
    );

    protected final Map<UUID, StoredBoundHorse> boundHorses;
    protected final List<UUID> unboundHorses;
    protected final List<UUID> horsesToRemove;

    private SummoningStorage(Map<UUID, StoredBoundHorse> boundHorses,
                             List<UUID> unboundHorses,
                             List<UUID> horsesToRemove) {
        this.boundHorses = new HashMap<>(boundHorses); // Make sure it's mutable
        this.unboundHorses = new ArrayList<>(unboundHorses); // Make sure it's mutable
        this.horsesToRemove = new ArrayList<>(horsesToRemove); // Make sure it's mutable
        setDirty();
    }

    private SummoningStorage() {
        this(Collections.emptyMap(), Collections.emptyList(), Collections.emptyList());
        setDirty();
    }

    // --

    public Map<UUID, StoredBoundHorse> getBoundHorses() {
        return boundHorses;
    }

    public List<UUID> getUnboundHorses() {
        return unboundHorses;
    }

    public List<UUID> getHorsesToRemove() {
        return horsesToRemove;
    }
}
