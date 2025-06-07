package io.github.mortuusars.horseman;

import com.google.common.base.Preconditions;
import io.github.mortuusars.horseman.world.summoning.Summoning;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HorsemanServer {
    private static @Nullable MinecraftServer server;
    private static @Nullable Summoning summoning;

    public static @NotNull MinecraftServer getServer() {
        Preconditions.checkNotNull(server, "Tried to retrieve server before it has initialized.");
        return server;
    }

    public static @NotNull Summoning getSummoning() {
        Preconditions.checkNotNull(summoning, "Tried to retrieve HorseSummoning before the server has initialized.");
        return summoning;
    }

    // --

    public static void serverStarted(MinecraftServer server) {
        HorsemanServer.server = server;
        summoning = new Summoning(server);
    }

    public static void serverStopped(MinecraftServer server) {
        HorsemanServer.server = null;
        summoning = null;
    }
}
