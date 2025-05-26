package io.github.mortuusars.horseman.neoforge.event;

import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.HorsemanClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents {
    @EventBusSubscriber(modid = Horseman.ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(HorsemanClient::init);
        }
    }

    public static class GameBus {

    }
}
