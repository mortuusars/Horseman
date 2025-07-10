package io.github.mortuusars.horseman.mixin.dismount;

import io.github.mortuusars.horseman.Config;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal {
    @Shadow
    @Nullable
    protected abstract Vec3 getDismountLocationInDirection(Vec3 direction, LivingEntity passenger);

    protected AbstractHorseMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "getDismountLocationForPassenger", at = @At("HEAD"), cancellable = true)
    private void getDismountLocation(LivingEntity passenger, CallbackInfoReturnable<Vec3> cir) {
        if (Config.Server.DISMOUNT_TOWARDS_VIEW_DIRECTION.get()) {
            Vec3 escapeVector = getCollisionHorizontalEscapeVector(getBbWidth(), passenger.getBbWidth(), passenger.getYRot());
            @Nullable Vec3 location = getDismountLocationInDirection(escapeVector, passenger);
            if (location != null) {
                cir.setReturnValue(location);
            }
        }
    }
}
