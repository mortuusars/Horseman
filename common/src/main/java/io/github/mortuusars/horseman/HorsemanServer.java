package io.github.mortuusars.horseman;

import com.google.common.base.Preconditions;
import io.github.mortuusars.horseman.world.summoning.Summoning;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HorsemanServer {
    private static @Nullable MinecraftServer server;

    private static @Nullable Summoning horseCalling;

    public static @NotNull MinecraftServer getServer() {
        Preconditions.checkNotNull(server, "Tried to retrieve server before it has initialized.");
        return server;
    }

    public static @NotNull Summoning summoning() {
        Preconditions.checkNotNull(horseCalling, "Tried to retrieve horseCalling before the server has initialized.");
        return horseCalling;
    }

    // --

    public static void init(MinecraftServer server) {
        HorsemanServer.server = server;
        horseCalling = new Summoning(server);
    }

    public static void stop(MinecraftServer server) {
        HorsemanServer.server = null;
        horseCalling = null;
    }
}
