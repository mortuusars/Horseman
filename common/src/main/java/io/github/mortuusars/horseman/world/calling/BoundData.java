package io.github.mortuusars.horseman.world.calling;

import io.github.mortuusars.horseman.Horseman;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record BoundData(UUID owner, ResourceKey<Instrument> instrument) {
    public BoundData(Player player, ResourceKey<Instrument> instrument) {
        this(player.getUUID(), instrument);
    }

    public boolean isBoundTo(UUID uuid) {
        return owner().equals(uuid);
    }

    public boolean isBoundTo(Player player) {
        return isBoundTo(player.getUUID());
    }

    // --

    public void save(CompoundTag tag) {
        tag.putUUID("HorsemanCallingOwner", owner);
        tag.putString("HorsemanCallingInstrument", instrument.location().toString());
    }

    public static @Nullable BoundData load(CompoundTag tag) {
        if (!tag.contains("HorsemanCallingOwner", Tag.TAG_INT_ARRAY)) return null;
        if (!tag.contains("HorsemanCallingInstrument", Tag.TAG_STRING)) return null;

        return new BoundData(tag.getUUID("HorsemanCallingOwner"),
                getResourceKey1(tag, "HorsemanCallingInstrument", Registries.INSTRUMENT));
    }

    @SuppressWarnings("SameParameterValue")
    private static <T> ResourceKey<T> getResourceKey1(CompoundTag tag, String key, ResourceKey<Registry<T>> registry) {
        String string = tag.getString(key);
        try {
            return ResourceKey.create(registry, ResourceLocation.parse(string));
        } catch (Exception e) {
            Horseman.LOGGER.error("Cannot parse '{}' from '{}'.", key, string, e);
            return null;
        }
    }
}