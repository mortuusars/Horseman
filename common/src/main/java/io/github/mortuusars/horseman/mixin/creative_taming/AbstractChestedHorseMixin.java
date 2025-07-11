package io.github.mortuusars.horseman.mixin.creative_taming;

import io.github.mortuusars.horseman.Config;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SaddleItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractChestedHorse.class)
public abstract class AbstractChestedHorseMixin extends AbstractHorse {
    protected AbstractChestedHorseMixin(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (Config.Server.HORSE_CREATIVE_TAMING.get()
                && player.isCreative()
                && player.getItemInHand(hand).getItem() instanceof SaddleItem
                && isAlive()
                && !isSaddled()
                && !isTamed()
                && !isBaby()) {
            if (!level().isClientSide) {
                tameWithName(player);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
