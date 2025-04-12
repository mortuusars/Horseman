package io.github.mortuusars.horseman.mixin.swim;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal {
    protected AbstractHorseMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "getRiddenInput", at = @At(value = "RETURN"))
    private void getRiddenInput(Player player, Vec3 travelVector, CallbackInfoReturnable<Vec3> cir) {
        if (Config.Server.HORSE_SWIM_WHEN_RIDDEN.get()
                && !getType().is(Horseman.Tags.EntityTypes.CANNOT_SWIM)
                && player.jumping
                && isInWater()
                && getFluidHeight(FluidTags.WATER) > 0) {
            setDeltaMovement(getDeltaMovement().add(0.0, 0.04, 0.0));
        }
    }
}