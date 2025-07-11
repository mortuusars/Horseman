package io.github.mortuusars.horseman.mixin.fits_in_boat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Boat.class)
public abstract class BoatMixin extends VehicleEntity {
    public BoatMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyReturnValue(method = "hasEnoughSpaceFor", at = @At("RETURN"))
    private boolean hasEnoughSpaceFor(boolean original, @Local(argsOnly = true) Entity entity) {
        return (Config.Server.HORSE_IN_BOAT.get()
                && entity instanceof AbstractHorse
                && !entity.getType().is(Horseman.Tags.EntityTypes.FORBIDS_HORSES)) || original;
    }
}
