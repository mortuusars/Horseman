package io.github.mortuusars.horseman.mixin.fix_running_back;

import io.github.mortuusars.horseman.Config;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class, priority = 950)
public abstract class EntityMixin {
    @Shadow public abstract Level level();

    @Shadow @Nullable public abstract Entity getVehicle();

    @Inject(method = "removeVehicle", at = @At(value = "HEAD"))
    private void removeVehicle(CallbackInfo ci) {
        if (Config.Server.FIX_RUNNING_BACK_AFTER_DISMOUNT.get() &&
                getVehicle() instanceof AbstractHorse horse && horse.getPassengers().size() == 1) {
            horse.getNavigation().stop();
        }
    }
}