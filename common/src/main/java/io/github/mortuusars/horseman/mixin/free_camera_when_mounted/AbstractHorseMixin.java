package io.github.mortuusars.horseman.mixin.free_camera_when_mounted;

import io.github.mortuusars.horseman.Config;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal {
    protected AbstractHorseMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "getRiddenRotation", at = @At(value = "HEAD"), cancellable = true)
    private void onGetRiddenRotation(LivingEntity controller, CallbackInfoReturnable<Vec2> cir) {
        AbstractHorse horse = (AbstractHorse)(Object)this;
        if (!Config.Server.HORSE_FREE_CAMERA.get() || !(controller instanceof Player player) || player.xxa != 0 || player.zza != 0) {
            return;
        }

        float playerYRot = Mth.wrapDegrees(player.getYRot());
        float threshold = Config.Server.HORSE_FREE_CAMERA_ANGLE_THRESHOLD.get().floatValue();

        float rotationDifference = (playerYRot - horse.getYRot() + 540) % 360 - 180;

        if (Math.abs(rotationDifference) > threshold) {
            // Rotate the horse following player's rotation, with offset
            float y = playerYRot - Math.signum(rotationDifference) * threshold;
            y = Mth.wrapDegrees(y); // Does not really change much, but the value seems to change sign back and forth without it, idk.
            cir.setReturnValue(new Vec2(player.getXRot() * 0.5f, y));
        } else {
            // Keep horse rotation as it is:
            cir.setReturnValue(new Vec2(player.getXRot() * 0.5f, horse.getYRot()));
        }
    }
}
