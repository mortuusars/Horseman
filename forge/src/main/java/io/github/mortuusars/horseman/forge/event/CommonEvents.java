package io.github.mortuusars.horseman.forge.event;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.network.forge.PacketsImpl;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
    }

    @Mod.EventBusSubscriber(modid = Horseman.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBus {
        @SubscribeEvent
        public static void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
            if (event.getEntity().getControlledVehicle() instanceof AbstractHorse) {
                float speed = event.getNewSpeed() > 0 ? event.getNewSpeed() : event.getOriginalSpeed();
                event.setNewSpeed(speed * Config.Common.MOUNTED_BLOCK_BREAK_SPEED_MODIFIER.get().floatValue());
            }
        }
    }
}
