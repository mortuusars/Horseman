package io.github.mortuusars.horse_please.fabric;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import io.github.mortuusars.horse_please.Config;
import io.github.mortuusars.horse_please.HorsePlease;
import io.github.mortuusars.horse_please.network.fabric.PacketsImpl;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraftforge.fml.config.ModConfig;

public class HorsePleaseFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        HorsePlease.init();

        ForgeConfigRegistry.INSTANCE.register(HorsePlease.ID, ModConfig.Type.COMMON, Config.Common.SPEC);
        ForgeConfigRegistry.INSTANCE.register(HorsePlease.ID, ModConfig.Type.CLIENT, Config.Client.SPEC);

        HorsePlease.Advancements.register();
        HorsePlease.Stats.register();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            PacketsImpl.onServerStarting(server);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(PacketsImpl::onServerStopped);

        PacketsImpl.registerC2SPackets();
    }
}
