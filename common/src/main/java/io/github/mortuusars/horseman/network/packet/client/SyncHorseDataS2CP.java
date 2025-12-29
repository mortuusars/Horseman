package io.github.mortuusars.horseman.network.packet.client;

import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.network.handler.ClientPacketsHandler;
import io.github.mortuusars.horseman.network.packet.Packet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record SyncHorseDataS2CP(int entityId, ItemStack leadStack, boolean isHitched) implements Packet {
    public static final Identifier ID = Horseman.resource("sync_horse_data");
    public static final Type<SyncHorseDataS2CP> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncHorseDataS2CP> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncHorseDataS2CP::entityId,
            ItemStack.OPTIONAL_STREAM_CODEC, SyncHorseDataS2CP::leadStack,
            ByteBufCodecs.BOOL, SyncHorseDataS2CP::isHitched,
            SyncHorseDataS2CP::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public boolean handle(PacketFlow direction, Player player) {
        ClientPacketsHandler.syncHorseData(this);
        return true;
    }
}
