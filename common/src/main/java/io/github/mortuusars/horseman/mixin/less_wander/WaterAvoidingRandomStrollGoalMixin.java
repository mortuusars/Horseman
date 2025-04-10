package io.github.mortuusars.horseman.mixin.less_wander;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.mortuusars.horseman.world.LessWanderingHorse;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WaterAvoidingRandomStrollGoal.class)
public abstract class WaterAvoidingRandomStrollGoalMixin extends RandomStrollGoal {
    public WaterAvoidingRandomStrollGoalMixin(PathfinderMob mob, double speedModifier) {
        super(mob, speedModifier);
    }

    @ModifyReturnValue(method = "getPosition", at = @At("RETURN"))
    private Vec3 onGetPosition(Vec3 original) {
        if (original != null
                && mob instanceof AbstractHorse horse
                && horse.isSaddled()
                && !mob.isInWaterOrBubble() // Allow horse to escape water. But this does not seem to be working in vanilla.
                && mob instanceof LessWanderingHorse lessWanderingHorse
                && lessWanderingHorse.horseman$isOutsideWanderingLimit(original)) {
            return null; // Stay at current pos
        }

        return original;
    }
}
