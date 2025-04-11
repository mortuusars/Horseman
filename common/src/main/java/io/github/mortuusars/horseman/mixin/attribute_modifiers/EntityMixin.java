package io.github.mortuusars.horseman.mixin.attribute_modifiers;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds attribute modifiers to player & horse while mounted to
 * - increase horse's StepHeight by 10% (when enabled)
 * - remove BreakSpeed debuff from not being grounded (when enabled)
 */
@Mixin(value = Entity.class, priority = 950)
public abstract class EntityMixin {
    @Shadow public abstract Level level();

    @Shadow @Nullable public abstract Entity getVehicle();

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addPassenger(Lnet/minecraft/world/entity/Entity;)V"))
    private void startRiding(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if (!(((Entity)(Object) this) instanceof ServerPlayer player)) return;

        if (vehicle instanceof AbstractHorse horse) {
            double stepHeightModifier = Config.Server.HORSE_STEP_HEIGHT_MODIFIER.get();
            if (stepHeightModifier != 0) {
                AttributeInstance attribute = horse.getAttribute(Attributes.STEP_HEIGHT);
                if (attribute != null) {
                    attribute.addTransientModifier(new AttributeModifier(Horseman.EntityAttributes.MOUNTED_STEP_HEIGHT,
                            stepHeightModifier, AttributeModifier.Operation.ADD_VALUE));
                }
            }
        }

        double breakSpeedModifier = Config.Server.MOUNTED_BLOCK_BREAK_SPEED_MODIFIER.get();
        if (breakSpeedModifier != 0) {
            AttributeInstance attribute = player.getAttribute(Attributes.BLOCK_BREAK_SPEED);
            if (attribute != null) {
                attribute.addTransientModifier(new AttributeModifier(Horseman.EntityAttributes.MOUNTED_BREAK_SPEED,
                        breakSpeedModifier, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }

    @Inject(method = "removeVehicle", at = @At(value = "HEAD"))
    private void removeVehicle(CallbackInfo ci) {
        if (!(((Entity)(Object) this) instanceof ServerPlayer player)) return;

        if (getVehicle() instanceof AbstractHorse horse) {
            AttributeInstance attribute = horse.getAttribute(Attributes.STEP_HEIGHT);
            if (attribute != null) {
                attribute.removeModifier(Horseman.EntityAttributes.MOUNTED_STEP_HEIGHT);
            }
        }

        AttributeInstance attribute = player.getAttribute(Attributes.BLOCK_BREAK_SPEED);
        if (attribute != null) {
            attribute.removeModifier(Horseman.EntityAttributes.MOUNTED_BREAK_SPEED);
        }
    }
}