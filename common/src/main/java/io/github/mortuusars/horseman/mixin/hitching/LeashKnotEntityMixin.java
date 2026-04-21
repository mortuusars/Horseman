package io.github.mortuusars.horseman.mixin.hitching;

import io.github.mortuusars.horseman.world.HitchableHorse;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeashFenceKnotEntity.class)
public abstract class LeashKnotEntityMixin extends BlockAttachedEntity {
    @Shadow
    public abstract void playPlacementSound();

    protected LeashKnotEntityMixin(EntityType<? extends HangingEntity> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * Hitch a horse to an existing knot, without removing it first.
     */
    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void onInteract(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.getRootVehicle() instanceof AbstractHorse horse
                && horse instanceof HitchableHorse hitchableHorse) {
            if (HitchableHorse.isHitched(hitchableHorse)) {
                horse.removeLeash();
                this.playSound(SoundEvents.LEAD_UNTIED);
                cir.setReturnValue(InteractionResult.SUCCESS);
            } else if (HitchableHorse.canHitch(hitchableHorse)) {
                horse.setLeashedTo(player, true);
                HitchableHorse.setHitched(hitchableHorse, true);

                if (!level().isClientSide()) {
                    HitchableHorse.syncHorseDataToTrackingClients(hitchableHorse);
                    playPlacementSound();
                }
            }
        }
    }
}
