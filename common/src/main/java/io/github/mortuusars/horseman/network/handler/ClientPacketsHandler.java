package io.github.mortuusars.horseman.network.handler;

import io.github.mortuusars.horseman.world.HitchableHorse;
import io.github.mortuusars.horseman.network.packet.client.SyncHorseDataS2CP;
import net.minecraft.client.Minecraft;

public class ClientPacketsHandler {
    private static void executeOnMainThread(Runnable runnable) {
        Minecraft.getInstance().execute(runnable);
    }

    public static void syncHorseData(SyncHorseDataS2CP packet) {
        if (Minecraft.getInstance().level == null) return;
        executeOnMainThread(() -> {
            if (Minecraft.getInstance().level.getEntity(packet.entityId()) instanceof HitchableHorse hitchableHorse) {
                hitchableHorse.horseman$setLead(packet.leadStack());
                hitchableHorse.horseman$setHitched(packet.isHitched());
            }
        });
    }
}
