package io.github.mortuusars.horseman.network.packet;

import io.github.mortuusars.horseman.network.packet.client.SyncHorseDataS2CP;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public class S2CPackets {
    public static List<CustomPacketPayload.TypeAndCodec<? extends FriendlyByteBuf, ? extends CustomPacketPayload>> getDefinitions() {
        return List.of(
                new CustomPacketPayload.TypeAndCodec<>(SyncHorseDataS2CP.TYPE, SyncHorseDataS2CP.STREAM_CODEC)
        );
    }
}
