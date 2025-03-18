package io.github.mortuusars.horseman.fabric;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.network.fabric.PacketsImpl;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraftforge.fml.config.ModConfig;

public class HorsemanFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Horseman.init();

        ForgeConfigRegistry.INSTANCE.register(Horseman.ID, ModConfig.Type.COMMON, Config.Common.SPEC);
        ForgeConfigRegistry.INSTANCE.register(Horseman.ID, ModConfig.Type.CLIENT, Config.Client.SPEC);

        Horseman.Advancements.register();
        Horseman.Stats.register();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            PacketsImpl.onServerStarting(server);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(PacketsImpl::onServerStopped);

//        EntityTrackingEvents.START_TRACKING.register((trackedEntity, player) -> {
//            if (trackedEntity instanceof LeadHolder leadHolder && leadHolder.horseman$hasLead()) {
//                Packets.sendToClient(new SyncStoredLeadS2CP(trackedEntity.getId(), leadHolder.horseman$getLead()), player);
//            }
//        });

        PacketsImpl.registerC2SPackets();
    }
}
