package io.github.mortuusars.horseman.mixin.swim;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class)
public abstract class Swim {
    @Inject(method = "travelRidden", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;travel(Lnet/minecraft/world/phys/Vec3;)V"))
    private void onTravelRidden(Player player, Vec3 travelVector, CallbackInfo ci) {
        if ((LivingEntity)(Object)this instanceof AbstractHorse horse
                && Config.Server.HORSE_SWIM_WHEN_RIDDEN.get()
                && horse.getType().is(Horseman.Tags.EntityTypes.CAN_SWIM_WHEN_RIDDEN)) {
            double depth = horse.getFluidHeight(FluidTags.WATER) - horse.getFluidJumpThreshold();
            if (depth > 0 && depth < horse.getBbHeight() * 0.95f) { // Limit depth to not allow horses be better than bubble column elevators.
                double buoyancy = 0.005 + Math.max(0.1, 0.05 * (depth * depth * depth));
                horse.addDeltaMovement(new Vec3(0, buoyancy, 0));
            }
        }
    }
}