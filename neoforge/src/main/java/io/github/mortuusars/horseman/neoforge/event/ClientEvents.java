package io.github.mortuusars.horseman.neoforge.event;

import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.HorsemanClient;
import io.github.mortuusars.horseman.client.HorseStatsTooltip;
import net.minecraft.world.entity.animal.horse.Horse;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public class ClientEvents {
    @EventBusSubscriber(modid = Horseman.ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(HorsemanClient::init);
        }
    }

    @EventBusSubscriber(modid = Horseman.ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class GameBus {
        @SubscribeEvent
        public static void onRenderGuiPost(RenderGuiEvent.Post event) {
            HorseStatsTooltip.render(event.getGuiGraphics(), event.getPartialTick());
        }
    }
}
