package io.github.mortuusars.horse_please.fabric;

import io.github.mortuusars.horse_please.network.fabric.PacketsImpl;
import net.fabricmc.api.ClientModInitializer;

public class HorsePleaseFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PacketsImpl.registerS2CPackets();
    }
}
