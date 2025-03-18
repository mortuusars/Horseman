package io.github.mortuusars.horseman.network.fabric;

import io.github.mortuusars.horseman.network.PacketDirection;
import io.github.mortuusars.horseman.network.packet.IPacket;
import io.github.mortuusars.horseman.network.packet.client.SyncHorseDataS2CP;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

public class ClientPackets {
    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SyncHorseDataS2CP.ID, new ClientHandler(SyncHorseDataS2CP::fromBuffer));
    }

    public static void sendToServer(IPacket packet) {
        ClientPlayNetworking.send(packet.getId(), packet.toBuffer(PacketByteBufs.create()));
    }

    private record ClientHandler(Function<FriendlyByteBuf, IPacket> decodeFunction) implements ClientPlayNetworking.PlayChannelHandler {
        @Override
        public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
            IPacket packet = decodeFunction.apply(buf);
            packet.handle(PacketDirection.TO_CLIENT, null);
        }
    }
}
