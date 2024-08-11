package io.github.mortuusars.horseman.fabric;

import io.github.mortuusars.horseman.network.fabric.PacketsImpl;
import net.fabricmc.api.ClientModInitializer;

public class HorsemanFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PacketsImpl.registerS2CPackets();
    }
}
