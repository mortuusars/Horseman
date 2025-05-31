package io.github.mortuusars.horseman;

import com.google.common.base.Preconditions;
import io.github.mortuusars.horseman.world.calling.HorseCalling;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HorsemanServer {
    private static @Nullable HorseCalling horseCalling;

    public static @NotNull HorseCalling horseCalling() {
        Preconditions.checkNotNull(horseCalling, "Tried to retrieve horseCalling before the server has initialized.");
        return horseCalling;
    }

    // --

    public static void init(MinecraftServer server) {
        horseCalling = new HorseCalling(server);
    }

    public static void stop(MinecraftServer server) {
        horseCalling = null;
    }
}
