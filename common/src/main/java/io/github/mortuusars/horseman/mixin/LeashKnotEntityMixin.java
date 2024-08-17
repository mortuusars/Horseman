package io.github.mortuusars.horseman.mixin;

import io.github.mortuusars.horseman.Hitching;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeashFenceKnotEntity.class)
public abstract class LeashKnotEntityMixin extends HangingEntity {
    @Shadow public abstract void playPlacementSound();

    protected LeashKnotEntityMixin(EntityType<? extends HangingEntity> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * Hitch a horse to an existing knot, without removing it first.
     */
    @Inject(method = "interact", at = @At("HEAD"))
    private void onInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!level().isClientSide && player.getRootVehicle() instanceof AbstractHorse horse && Hitching.canHitch(horse)) {
            horse.setLeashedTo(player, true);
            Hitching.setDropsLeash(horse, false);
            playPlacementSound();
        }
    }
}
