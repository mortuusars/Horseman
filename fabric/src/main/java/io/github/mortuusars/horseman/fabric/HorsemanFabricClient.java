package io.github.mortuusars.horseman.fabric;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.HorsemanClient;
import io.github.mortuusars.horseman.client.HorseStatsTooltip;
import io.github.mortuusars.horseman.network.fabric.FabricS2CPacketHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public class HorsemanFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HorsemanClient.init();
        FabricS2CPacketHandler.register();
        ConfigScreenFactoryRegistry.INSTANCE.register(Horseman.ID, ConfigurationScreen::new);
        HudRenderCallback.EVENT.register(HorseStatsTooltip::render);
    }
}
