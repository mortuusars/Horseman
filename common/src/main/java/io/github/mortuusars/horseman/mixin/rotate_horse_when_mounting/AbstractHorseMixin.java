package io.github.mortuusars.horseman.mixin.rotate_horse_when_mounting;

import io.github.mortuusars.horseman.Config;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal {
    protected AbstractHorseMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "doPlayerRide", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"), cancellable = true)
    private void onDoPlayerRide(Player player, CallbackInfo ci) {
        if (Config.Server.ROTATE_HORSE_INSTEAD_OF_PLAYER.get()) {
            setYRot(player.getYRot());
            yRotO = getYRot();
            setYHeadRot(player.getYHeadRot());
            setXRot(player.getXRot());

            player.startRiding(this);

            // Makes mounting smoother
            player.setYRot(getYRot());
            player.yRotO = yRotO;
            player.setXRot(getXRot());

            ci.cancel();
        }
    }
}
