package io.github.mortuusars.horseman.mixin.no_rearing;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.mortuusars.horseman.Config;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AbstractHorse.class, priority = 950)
public abstract class AbstractHorseMixin {
    @Shadow public abstract boolean isJumping();
    @Shadow public abstract boolean isTamed();
    @Shadow public abstract LivingEntity getControllingPassenger();

    @ModifyReturnValue(method = "isStanding", at = @At("RETURN"))
    private boolean isStanding(boolean original) {
        if (Config.Common.HORSE_PREVENT_REARING_WHEN_RIDING.get()
            && !isJumping()
            && isTamed()
            && getControllingPassenger() != null) {
            return false;
        }
        return original;
    }
}