package io.github.mortuusars.horseman.fabric.mixin.mount_gui;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.mortuusars.horseman.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = Gui.class)
public abstract class GuiMixin {
    @Shadow
    protected abstract int getVisibleVehicleHeartRows(int vehicleHealth);

    @Shadow
    @Nullable
    protected abstract LivingEntity getPlayerVehicleWithHealth();

    @Shadow
    protected abstract int getVehicleMaxHearts(LivingEntity vehicle);

    @Unique
    private long horseman$lastTickVehicleInWater = -1;

    @WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;jumpableVehicle()Lnet/minecraft/world/entity/PlayerRideableJumping;"))
    private PlayerRideableJumping renderHotbarAndDecorations_jumpableVehicle(LocalPlayer player, Operation<PlayerRideableJumping> original) {
        @Nullable PlayerRideableJumping vehicle = original.call(player);
        if (vehicle == null || !Config.Client.IMPROVED_MOUNT_GUI.get()) return vehicle;

        Minecraft mc = Minecraft.getInstance();

        if (mc.gameMode == null || !mc.gameMode.hasExperience()) return vehicle;

        if (vehicle instanceof LivingEntity entity && entity.isInWater()) {
            horseman$lastTickVehicleInWater = player.level().getGameTime();
        }

        if (!mc.options.keyJump.isDown() && player.getJumpRidingScale() <= 0
                || (player.level().getGameTime() - horseman$lastTickVehicleInWater < 10)) {
            return null; // Prevent jump bar from rendering.
        }

        return vehicle;
    }

    @WrapOperation(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I"))
    private int renderPlayerHealth_getVehicleMaxHearts(Gui instance, LivingEntity vehicle, Operation<Integer> original) {
        if (!Config.Client.IMPROVED_MOUNT_GUI.get()) return original.call(instance, vehicle);
        return 0; // Forces hunger bar rendering, because it is not rendered when vehicle hearts is not 0.
    }

    @WrapOperation(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getVisibleVehicleHeartRows(I)I"))
    private int renderPlayerHealth_getVisibleVehicleHeartRows(Gui instance, int vehicleHealth, Operation<Integer> original) {
        if (!Config.Client.IMPROVED_MOUNT_GUI.get()) return original.call(instance, vehicleHealth);
        // 'hears' will be 0 here, due to it being set in 'renderPlayerHealth_getVehicleMaxHearts'.
        LivingEntity livingEntity = getPlayerVehicleWithHealth();
        int vehicleHearts = getVehicleMaxHearts(livingEntity);
        return getVisibleVehicleHeartRows(vehicleHearts);
    }

    @ModifyVariable(method = "renderVehicleHealth", at = @At(value = "STORE"), ordinal = 2)
    private int renderVehicleHealth(int y) {
        if (Config.Client.IMPROVED_MOUNT_GUI.get()
                && Minecraft.getInstance().gameMode != null
                && Minecraft.getInstance().gameMode.canHurtPlayer()) {
            y -= 10; // Make room for hunger bar
        }
        return y;
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

    @WrapOperation(method = "renderExperienceLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;isExperienceBarVisible()Z"))
    private boolean renderExperienceLevel(Gui instance, Operation<Boolean> original) {
        if (!Config.Client.IMPROVED_MOUNT_GUI.get()) return original.call(instance);
        // Always render exp level:
        return Minecraft.getInstance().gameMode != null && Minecraft.getInstance().gameMode.hasExperience();
    }
}
