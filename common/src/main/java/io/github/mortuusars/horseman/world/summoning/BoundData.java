package io.github.mortuusars.horseman.world.summoning;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;

import java.util.UUID;

public record BoundData(UUID owner, ResourceKey<Instrument> instrument) {
    public static final Codec<BoundData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("owner").forGetter(BoundData::owner),
            ResourceKey.codec(Registries.INSTRUMENT).fieldOf("instrument").forGetter(BoundData::instrument)
    ).apply(instance, BoundData::new));

    public BoundData(Player player, ResourceKey<Instrument> instrument) {
        this(player.getUUID(), instrument);
    }

    public boolean isBoundTo(UUID uuid) {
        return owner().equals(uuid);
    }

    public boolean isBoundTo(Player player) {
        return isBoundTo(player.getUUID());
    }
}