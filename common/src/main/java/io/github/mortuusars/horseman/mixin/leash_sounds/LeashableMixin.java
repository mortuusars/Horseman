package io.github.mortuusars.horseman.mixin.leash_sounds;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Leashable.class)
public interface LeashableMixin {
    @Inject(method = "tickLeash", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;leashTooFarBehaviour()V"))
    private static <E extends Entity & Leashable> void tickLeash(ServerLevel level, E entity, CallbackInfo ci) {
        if (Config.Server.LEASH_KNOT_SOUNDS.get()) {
            entity.level().playSound(null, entity, Horseman.SoundEvents.LEASH_BREAK.get(), SoundSource.NEUTRAL, 1f, 1f);
        }
    }
}
