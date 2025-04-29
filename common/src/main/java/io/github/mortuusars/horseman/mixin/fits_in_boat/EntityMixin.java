package io.github.mortuusars.horseman.mixin.fits_in_boat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.mortuusars.horseman.Config;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {
    @ModifyReturnValue(method = "getBoundingBox", at = @At("RETURN"))
    private AABB onGetBoundingBox(AABB original) {
        if (((Entity) (Object) this) instanceof AbstractHorse horse && horse.getVehicle() instanceof Boat
                && Config.Server.SPEC.isLoaded() && Config.Server.HORSE_IN_BOAT.get()) {
            return original.deflate(0.1f, 0, 0.1f);
        }
        return original;
    }
}
