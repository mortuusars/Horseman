package io.github.mortuusars.horse_please.network.handler;

import net.minecraft.client.Minecraft;

public class ClientPacketsHandler {
    private static void executeOnMainThread(Runnable runnable) {
        Minecraft.getInstance().execute(runnable);
    }
}
