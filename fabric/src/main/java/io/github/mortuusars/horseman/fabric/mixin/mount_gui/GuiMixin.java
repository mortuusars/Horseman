package io.github.mortuusars.horseman.fabric.mixin.mount_gui;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.client.ImprovedMountGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Gui.class)
public abstract class GuiMixin {
    @Shadow
    protected abstract int getVisibleVehicleHeartRows(int vehicleHealth);

    @Shadow
    @Nullable
    protected abstract LivingEntity getPlayerVehicleWithHealth();

    @Shadow
    protected abstract int getVehicleMaxHearts(LivingEntity vehicle);

    @Shadow public abstract void renderExperienceBar(GuiGraphics guiGraphics, int x);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;jumpableVehicle()Lnet/minecraft/world/entity/PlayerRideableJumping;"))
    private PlayerRideableJumping renderHotbarAndDecorations_jumpableVehicle(LocalPlayer player) {
        @Nullable PlayerRideableJumping vehicle = player.jumpableVehicle();
        if (!ImprovedMountGui.isEnabled()) return vehicle;
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null || !mc.gameMode.hasExperience()) return vehicle;
        if (!ImprovedMountGui.shouldRenderJumpBar()) return null; // Prevent jump bar from rendering.
        return vehicle;
    }

    @Redirect(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I"))
    private int renderPlayerHealth_getVehicleMaxHearts(Gui instance, LivingEntity entity) {
        if (!ImprovedMountGui.isEnabled()) return getVehicleMaxHearts(entity);
        return 0; // Forces hunger bar rendering, because it is not rendered when vehicle hearts is not 0.
    }

    @Redirect(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getVisibleVehicleHeartRows(I)I"))
    private int renderPlayerHealth_getVisibleVehicleHeartRows(Gui instance, int hearts) {
        if (!ImprovedMountGui.isEnabled()) return getVisibleVehicleHeartRows(hearts);
        // 'hears' will be 0 here, due to it being set in 'renderPlayerHealth_getVehicleMaxHearts'.
        LivingEntity livingEntity = getPlayerVehicleWithHealth();
        int vehicleHearts = getVehicleMaxHearts(livingEntity);
        return getVisibleVehicleHeartRows(vehicleHearts);
    }

    @ModifyVariable(method = "renderVehicleHealth", at = @At(value = "STORE"), ordinal = 2)
    private int renderVehicleHealth(int y) {
        if (ImprovedMountGui.isEnabled()
                && Minecraft.getInstance().gameMode != null
                && Minecraft.getInstance().gameMode.canHurtPlayer()) {
            y -= 10; // Make room for hunger bar
        }
        return y;
    }

    @Inject(method = "renderJumpMeter", at = @At(value = "RETURN"))
    private void renderJumpMeter(PlayerRideableJumping rideable, GuiGraphics guiGraphics, int x, CallbackInfo ci) {
        if (!ImprovedMountGui.isEnabled()) return;

        MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
        if (gameMode != null && gameMode.hasExperience()) {
            renderExperienceBar(guiGraphics, x);
        }
    }

    @Redirect(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getXpNeededForNextLevel()I"))
    private int renderExperienceBar(LocalPlayer instance) {
        if (ImprovedMountGui.isEnabled()
                && Minecraft.getInstance().gameMode != null && Minecraft.getInstance().gameMode.hasExperience()
                && ImprovedMountGui.shouldRenderJumpBar()) {
            return -1; // Prevent bar from rendering, but keep level number.
        }

        return instance.getXpNeededForNextLevel();
    }
}
