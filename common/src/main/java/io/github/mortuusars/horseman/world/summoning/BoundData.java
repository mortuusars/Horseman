package io.github.mortuusars.horseman.world.summoning;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record BoundData(UUID owner) {
    public static final Codec<BoundData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("owner").forGetter(BoundData::owner)
    ).apply(instance, BoundData::new));

    public BoundData(Player player) {
        this(player.getUUID());
    }

    public boolean isBoundTo(UUID uuid) {
        return owner().equals(uuid);
    }

    public boolean isBoundTo(Player player) {
        return isBoundTo(player.getUUID());
    }
}