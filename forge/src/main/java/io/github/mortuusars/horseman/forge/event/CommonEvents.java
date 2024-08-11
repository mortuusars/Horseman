package io.github.mortuusars.horseman.forge.event;

import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.network.forge.PacketsImpl;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonEvents {
    public static class ModBus {
        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                PacketsImpl.register();
                Horseman.Advancements.register();
                Horseman.Stats.register();
            });
        }

//        @SubscribeEvent
//        public static void onCreativeTabsBuild(BuildCreativeModeTabContentsEvent event) {
//
//        }
    }

    public static class ForgeBus {
//        @SubscribeEvent
//        public static void serverStarting(ServerStartingEvent event) {
//
//        }
    }
}
