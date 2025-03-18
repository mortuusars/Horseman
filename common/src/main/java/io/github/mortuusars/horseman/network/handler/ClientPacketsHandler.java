package io.github.mortuusars.horseman.network.handler;

import io.github.mortuusars.horseman.data.HitchableHorse;
import io.github.mortuusars.horseman.network.packet.client.SyncHorseDataS2CP;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;

public class ClientPacketsHandler {
    private static void executeOnMainThread(Runnable runnable) {
        Minecraft.getInstance().execute(runnable);
    }

    public static void syncHorseData(SyncHorseDataS2CP packet) {
        if (Minecraft.getInstance().level == null) return;
        executeOnMainThread(() -> {
            if (Minecraft.getInstance().level.getEntity(packet.entityId()) instanceof AbstractHorse horse) {
                for (int i = 0; i < packet.inventory().size(); i++) {
                    ItemStack itemStack = packet.inventory().get(i);
                    if (i < horse.inventory.getContainerSize()) {
                        horse.inventory.setItem(i, itemStack);
                    }
                }

                if (horse instanceof HitchableHorse hitchableHorse) {
                    hitchableHorse.horseman$setHitched(packet.isHitched());
                    hitchableHorse.horseman$setLead(packet.leadStack());
                }
            }
        });
    }
}
