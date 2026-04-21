package io.github.mortuusars.horseman.fabric;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.HorsemanServer;
import io.github.mortuusars.horseman.network.fabric.FabricC2SPackets;
import io.github.mortuusars.horseman.network.fabric.FabricS2CPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;

public class HorsemanFabric implements ModInitializer {
    // Server field to access when no other objects are available to get it from.
    public static @Nullable MinecraftServer server = null;

    @Override
    public void onInitialize() {
        Horseman.init();

        ConfigRegistry.INSTANCE.register(Horseman.ID, ModConfig.Type.SERVER, Config.Server.SPEC);
        ConfigRegistry.INSTANCE.register(Horseman.ID, ModConfig.Type.COMMON, Config.Common.SPEC);
        ConfigRegistry.INSTANCE.register(Horseman.ID, ModConfig.Type.CLIENT, Config.Client.SPEC);

        Horseman.Stats.register();

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
            content.accept(Horseman.Items.COPPER_HORN.get());
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            HorsemanServer.serverStarted(server);
            HorsemanFabric.server = server;
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            HorsemanServer.serverStopped(server);
            HorsemanFabric.server = null;
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            try {
                if (entity instanceof AbstractHorse horse && world instanceof ServerLevel level) {
                    if (HorsemanServer.getSummoning().onHorseLoaded(level, horse)) {
                        horse.discard();
                    }
                }
            } catch (Exception e) {
                Horseman.LOGGER.warn("Failed to handle Horseman's entityLoad event. {}.", e.getMessage());
            }
        });

        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            try {
                if (entity instanceof AbstractHorse horse && world instanceof ServerLevel level) {
                    HorsemanServer.getSummoning().onHorseUnloaded(level, horse);
                }
            } catch (Exception e) {
                Horseman.LOGGER.warn("Failed to handle Horseman's entityUnload event. {}.", e.getMessage());
            }
        });

        FabricC2SPackets.register();
        FabricS2CPackets.register();
    }
}
