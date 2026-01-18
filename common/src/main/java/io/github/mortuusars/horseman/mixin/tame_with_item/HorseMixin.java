package io.github.mortuusars.horseman.mixin.tame_with_item;

import io.github.mortuusars.horseman.Config;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Horse.class)
public class HorseMixin extends AbstractHorse {
    protected HorseMixin(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!Config.Server.HORSE_ALLOW_TAMING_WITH_ITEM_IN_HAND.get()) return;
        if (player.isSecondaryUseActive() || isVehicle() || isBaby() || isTamed() || isFood(player.getItemInHand(hand))) return;
        if (!player.getItemInHand(hand).isEmpty()) {
            doPlayerRide(player);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
