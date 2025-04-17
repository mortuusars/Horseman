package io.github.mortuusars.horseman.forge.event;

import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.client.ImprovedMountGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = Horseman.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), "horseman_xp_bar",
                    (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
                        if (!ImprovedMountGui.isEnabled()) return;
                        @Nullable LocalPlayer player = Minecraft.getInstance().player;
                        @Nullable MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
                        if (player != null && player.jumpableVehicle() != null && gameMode != null && gameMode.hasExperience()) {
                            gui.setupOverlayRenderState(true, false);
                            gui.renderExperienceBar(guiGraphics, screenWidth / 2 - 91);
                        }
                    });

            event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "horseman_food_level",
                    (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
                        @Nullable MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
                        if (!ImprovedMountGui.isEnabled() || gui.getMinecraft().options.hideGui
                                || gameMode == null || !gameMode.canHurtPlayer()) return;
                        @Nullable LocalPlayer player = Minecraft.getInstance().player;
                        if (player != null && player.jumpableVehicle() != null) {
                            gui.setupOverlayRenderState(true, false);
                            gui.renderFood(screenWidth, screenHeight, guiGraphics);
                        }
                    });
        }
    }

    public static class ForgeBus {

    }
}
