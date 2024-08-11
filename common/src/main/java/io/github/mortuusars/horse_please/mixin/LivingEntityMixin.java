package io.github.mortuusars.horse_please.mixin;

import io.github.mortuusars.horse_please.Config;
import io.github.mortuusars.horse_please.HorsePlease;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract float getSpeed();

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "getFlyingSpeed", at = @At("RETURN"), cancellable = true)
    private void onGetFlyingSpeed(CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof AbstractHorse
                && Config.Common.INCREASE_HORSE_AIRBORNE_SPEED.get()
                && getControllingPassenger() instanceof Player) {
            float delta = Config.Common.INCREASE_HORSE_AIRBORNE_SPEED_AMOUNT.get().floatValue();
            float vanillaSpeed = cir.getReturnValue();
            float groundSpeed = getSpeed() * 0.216f;
            cir.setReturnValue(Mth.lerp(delta, vanillaSpeed, groundSpeed));
        }
    }

    @Inject(method = "travelRidden", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;travel(Lnet/minecraft/world/phys/Vec3;)V"))
    private void onTravelRidden(Player player, Vec3 travelVector, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof AbstractHorse horse
                && Config.Common.HORSE_FAST_STEP_DOWN.get()
                && getControllingPassenger() instanceof Player
                && HorsePlease.shouldHorseStepDown(horse)) {
            // Applies downward momentum to connect with the ground faster and regain running speed.
            entity.addDeltaMovement(new Vec3(0, -0.5, 0));
        }
    }
}