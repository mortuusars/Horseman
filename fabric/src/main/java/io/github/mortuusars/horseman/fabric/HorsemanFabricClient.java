package io.github.mortuusars.horseman.fabric;

import io.github.mortuusars.horseman.network.fabric.FabricS2CPacketHandler;
import net.fabricmc.api.ClientModInitializer;

public class HorsemanFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricS2CPacketHandler.register();
    }
}
