package io.github.mortuusars.horseman.mixin.leash_sounds;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeashFenceKnotEntity.class)
public abstract class LeashFenceKnotEntityMixin {
    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/LeashFenceKnotEntity;discard()V"))
    private void interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (Config.Server.LEASH_KNOT_SOUNDS.get()) {
            LeashFenceKnotEntity e = ((LeashFenceKnotEntity) (Object) this);
            e.level().playSound(null, e.getX(), e.getY(), e.getZ(),
                    Horseman.SoundEvents.LEASH_UNTIED.get(), SoundSource.NEUTRAL, 1f, 1f);
        }
    }
}
