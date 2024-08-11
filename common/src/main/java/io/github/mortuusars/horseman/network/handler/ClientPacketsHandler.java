package io.github.mortuusars.horseman.network.handler;

import net.minecraft.client.Minecraft;

public class ClientPacketsHandler {
    private static void executeOnMainThread(Runnable runnable) {
        Minecraft.getInstance().execute(runnable);
    }
}
