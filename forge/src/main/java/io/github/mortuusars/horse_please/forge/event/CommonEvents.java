package io.github.mortuusars.horse_please.forge.event;

import io.github.mortuusars.horse_please.HorsePlease;
import io.github.mortuusars.horse_please.network.forge.PacketsImpl;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonEvents {
    public static class ModBus {
        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                PacketsImpl.register();
                HorsePlease.Advancements.register();
                HorsePlease.Stats.register();
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
