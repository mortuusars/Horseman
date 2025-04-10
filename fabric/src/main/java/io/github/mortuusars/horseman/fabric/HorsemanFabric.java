package io.github.mortuusars.horseman.fabric;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.network.fabric.FabricC2SPackets;
import io.github.mortuusars.horseman.network.fabric.FabricS2CPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;

public class HorsemanFabric implements ModInitializer {
    // Server field to access when no other objects are available to get it from.
    public static @Nullable MinecraftServer server = null;

    @Override
    public void onInitialize() {
        Horseman.init();

        NeoForgeConfigRegistry.INSTANCE.register(Horseman.ID, ModConfig.Type.SERVER, Config.Server.SPEC);
        NeoForgeConfigRegistry.INSTANCE.register(Horseman.ID, ModConfig.Type.COMMON, Config.Common.SPEC);
        NeoForgeConfigRegistry.INSTANCE.register(Horseman.ID, ModConfig.Type.CLIENT, Config.Client.SPEC);

        Horseman.Advancements.register();
        Horseman.Stats.register();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            HorsemanFabric.server = server;
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            HorsemanFabric.server = null;
        });

        FabricC2SPackets.register();
        FabricS2CPackets.register();
    }
}
