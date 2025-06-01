package io.github.mortuusars.horseman.fabric;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.HorsemanServer;
import io.github.mortuusars.horseman.network.fabric.FabricC2SPackets;
import io.github.mortuusars.horseman.network.fabric.FabricS2CPackets;
import io.github.mortuusars.horseman.world.item.CopperHornItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;

public class HorsemanFabric implements ModInitializer {
    // Server field to access when no other objects are available to get it from.
    public static @Nullable MinecraftServer server = null;

    @Override
    public void onInitialize() {
        Horseman.init();

        NeoForgeConfigRegistry.INSTANCE.register(Horseman.ID, ModConfig.Type.SERVER, Config.Server.SPEC);
        NeoForgeConfigRegistry.INSTANCE.register(Horseman.ID, ModConfig.Type.CLIENT, Config.Client.SPEC);

        Horseman.Stats.register();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
            content.getContext().holders()
                    .lookup(Registries.INSTRUMENT)
                    .flatMap(registryLookup -> registryLookup.get(InstrumentTags.GOAT_HORNS))
                    .ifPresent(named -> named.stream()
                            .map(holder -> CopperHornItem.create(Horseman.Items.COPPER_HORN.get(), holder))
                            .forEach(itemStack -> content.accept(itemStack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS)));
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            HorsemanServer.init(server);
            HorsemanFabric.server = server;
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            HorsemanServer.stop(server);
            HorsemanFabric.server = null;
        });

        FabricC2SPackets.register();
        FabricS2CPackets.register();
    }
}
