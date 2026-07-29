package io.github.mortuusars.horseman.neoforge.mixin.mount_gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.mortuusars.horseman.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Hud.class)
public abstract class GuiMixin {
    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract boolean willPrioritizeJumpInfo();

    @Unique
    private long horseman$lastTickHorseInWater = -1;

    @WrapOperation(method = "extractFoodLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I"))
    private int renderFoodLevel_getVehicleMaxHearts(Hud instance, LivingEntity vehicle, Operation<Integer> original) {
        if (!Config.Client.IMPROVED_MOUNT_GUI.get()) return original.call(instance, vehicle);
        return 0; // Forces hunger bar rendering, because it is not rendered when vehicle hearts is not 0.
    }

    @ModifyVariable(method = "nextContextualInfoState", at = @At(value = "STORE"), name = "canShowVehicleJumpInfo")
    private boolean shouldChooseJumpBar(boolean canShowVehicleJumpInfo) {
        if (!Config.Client.IMPROVED_MOUNT_GUI.get() || (minecraft.player != null && minecraft.player.isCreative())) {
            return canShowVehicleJumpInfo;
        }

        if (minecraft.player != null && minecraft.player.jumpableVehicle() instanceof AbstractHorse horse) {
            if (horse.isInWater()) {
                horseman$lastTickHorseInWater = minecraft.player.level().getGameTime();
            }

            if (minecraft.level != null && minecraft.level.getGameTime() - horseman$lastTickHorseInWater < 10) {
                return false;
            }
        }

        return canShowVehicleJumpInfo && willPrioritizeJumpInfo();
    }
}
