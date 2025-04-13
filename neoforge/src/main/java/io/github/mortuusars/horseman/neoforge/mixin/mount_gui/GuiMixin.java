package io.github.mortuusars.horseman.neoforge.mixin.mount_gui;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.mortuusars.horseman.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow protected abstract int getVehicleMaxHearts(@Nullable LivingEntity vehicle);

    @Shadow protected abstract boolean isExperienceBarVisible();

    @Unique
    private long horseman$lastTickVehicleInWater = -1;

    @Redirect(method = "renderFoodLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I"))
    private int renderFoodLevel_getVehicleMaxHearts(Gui instance, LivingEntity entity) {
        if (!Config.Client.IMPROVED_MOUNT_GUI.get()) return getVehicleMaxHearts(entity);
        return 0; // Forces hunger bar rendering, because it is not rendered when vehicle hearts is not 0.
    }

    @Inject(method = "renderJumpMeter", at = @At("HEAD"), cancellable = true)
    private void renderJumpMeter(PlayerRideableJumping rideable, GuiGraphics guiGraphics, int x, CallbackInfo ci) {
        if (!Config.Client.IMPROVED_MOUNT_GUI.get()) return;

        Minecraft mc = Minecraft.getInstance();

        if (mc.gameMode == null || !mc.gameMode.hasExperience() || mc.player == null || mc.level == null) return;

        if (rideable instanceof LivingEntity entity && entity.isInWater()) {
            horseman$lastTickVehicleInWater = mc.player.level().getGameTime();
        }

        if (!mc.options.keyJump.isDown() && mc.player.getJumpRidingScale() <= 0
                || (mc.level.getGameTime() - horseman$lastTickVehicleInWater < 10)) {
            ci.cancel(); // Prevent jump bar from rendering.
        }
    }

    @ModifyReturnValue(method = "isExperienceBarVisible", at = @At("RETURN"))
    private boolean isExperienceBarVisible(boolean original) {
        if (!Config.Client.IMPROVED_MOUNT_GUI.get()) return original;
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null || !mc.gameMode.hasExperience() || mc.level == null || mc.player == null) return original;
        if (!mc.options.keyJump.isDown() && mc.player.getJumpRidingScale() <= 0
                || mc.level.getGameTime() - horseman$lastTickVehicleInWater < 10) {
            return true;
        }
        return original;
    }

    @Redirect(method = "maybeRenderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;jumpableVehicle()Lnet/minecraft/world/entity/PlayerRideableJumping;"))
    private PlayerRideableJumping renderHotbarAndDecorations_jumpableVehicle(LocalPlayer player) {
        @Nullable PlayerRideableJumping vehicle = player.jumpableVehicle();
        if (!Config.Client.IMPROVED_MOUNT_GUI.get()) return vehicle;

        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null || !mc.gameMode.hasExperience() || mc.level == null || mc.player == null) return vehicle;
        if (!mc.options.keyJump.isDown() && mc.player.getJumpRidingScale() <= 0
                || mc.level.getGameTime() - horseman$lastTickVehicleInWater < 10) {
            return null; // Force experience bar to render.
        }

        return vehicle;
    }

    @Redirect(method = "renderExperienceLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;isExperienceBarVisible()Z"))
    private boolean renderExperienceLevel_isExperienceBarVisible(Gui instance) {
        if (!Config.Client.IMPROVED_MOUNT_GUI.get()) return isExperienceBarVisible();
        // Always render exp level:
        return Minecraft.getInstance().gameMode != null && Minecraft.getInstance().gameMode.hasExperience();
    }
}
