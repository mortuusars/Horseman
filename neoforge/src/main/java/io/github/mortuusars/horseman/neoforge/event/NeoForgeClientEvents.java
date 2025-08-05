package io.github.mortuusars.horseman.neoforge.event;

import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.HorsemanClient;
import io.github.mortuusars.horseman.client.HorseStatsTooltip;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = Horseman.ID, value = Dist.CLIENT)
public class NeoForgeClientEvents {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(HorsemanClient::init);
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        HorseStatsTooltip.render(event.getGuiGraphics(), event.getPartialTick());
    }
}
