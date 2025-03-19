package io.github.mortuusars.horseman.network.fabric;

import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.fabric.HorsemanFabric;
import io.github.mortuusars.horseman.network.packet.Packet;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class PacketsImpl {
    public static void sendToServer(Packet packet) {
        FabricC2SPackets.sendToServer(packet);
    }

    public static void sendToClient(Packet packet, ServerPlayer player) {
        ServerPlayNetworking.send(player, packet);
    }

    public static void sendToClients(Packet packet, Predicate<ServerPlayer> filter) {
        if (HorsemanFabric.server == null) {
            Horseman.LOGGER.error("Cannot send a packet to players. Server is not available.");
            return;
        }

        for (ServerPlayer player : HorsemanFabric.server.getPlayerList().getPlayers()) {
            if (filter.test(player)) {
                sendToClient(packet, player);
            }
        }
    }

    public static void sendToAllClients(Packet packet) {
        if (HorsemanFabric.server == null) {
            Horseman.LOGGER.error("Cannot send a packet to all players. Server is not available.");
            return;
        }

        for (ServerPlayer player : HorsemanFabric.server.getPlayerList().getPlayers()) {
            sendToClient(packet, player);
        }
    }

    public static void sendToPlayersNear(Packet packet, @NotNull ServerLevel level, @Nullable ServerPlayer excludedPlayer,
                                         double x, double y, double z, double radius) {
        sendToClients(packet, player -> {
            if (player != excludedPlayer && player.level().dimension() == level.dimension()) {
                double d0 = x - player.getX();
                double d1 = y - player.getY();
                double d2 = z - player.getZ();
                return d0 * d0 + d1 * d1 + d2 * d2 < radius * radius;
            }

            return false;
        });
    }

    public static void sendToPlayersTrackingEntity(Entity entity, Packet packet) {
        // I haven't found alternative to forge TRACKING_ENTITY target,
        // but it should not be a big deal anyway, client will not update the entity it does not have.
        sendToAllClients(packet);
    }
}
