package io.github.mortuusars.horseman.network.fabric;

import io.github.mortuusars.horseman.network.packet.CommonPackets;
import io.github.mortuusars.horseman.network.packet.S2CPackets;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class FabricS2CPackets {
    @SuppressWarnings("unchecked")
    public static void register() {
        for (var definition : S2CPackets.getDefinitions()) {
            PayloadTypeRegistry.clientboundPlay().register(
                    (CustomPacketPayload.Type<CustomPacketPayload>) definition.type(),
                    (StreamCodec<FriendlyByteBuf, CustomPacketPayload>) definition.codec());
        }

        for (var definition : CommonPackets.getDefinitions()) {
            PayloadTypeRegistry.clientboundPlay().register(
                    (CustomPacketPayload.Type<CustomPacketPayload>) definition.type(),
                    (StreamCodec<FriendlyByteBuf, CustomPacketPayload>) definition.codec());
        }
    }
}
