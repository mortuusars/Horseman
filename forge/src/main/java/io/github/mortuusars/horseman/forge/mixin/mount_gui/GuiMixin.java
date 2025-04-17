package io.github.mortuusars.horseman.forge.mixin.mount_gui;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.client.ImprovedMountGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.PlayerRideableJumping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(method = "renderJumpMeter", at = @At("HEAD"), cancellable = true)
    private void renderJumpMeterReturn(PlayerRideableJumping rideable, GuiGraphics guiGraphics, int x, CallbackInfo ci) {
        if (!ImprovedMountGui.isEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null || !mc.gameMode.hasExperience()) return;

        if (!ImprovedMountGui.shouldRenderJumpBar()) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getXpNeededForNextLevel()I"))
    private int renderExperienceBar_getXpNeededForNextLevel(LocalPlayer instance) {
        return ImprovedMountGui.isEnabled() && ImprovedMountGui.shouldRenderJumpBar()
                ? -1 // Prevent xp bar from rendering, but still render level number.
                : instance.getXpNeededForNextLevel();
    }
}
