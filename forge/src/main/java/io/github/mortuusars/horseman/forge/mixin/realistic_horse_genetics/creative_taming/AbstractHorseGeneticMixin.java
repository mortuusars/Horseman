package io.github.mortuusars.horseman.forge.mixin.realistic_horse_genetics.creative_taming;

import io.github.mortuusars.horseman.Config;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SaddleItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;

@Mixin(AbstractHorseGenetic.class)
public abstract class AbstractHorseGeneticMixin extends AbstractHorse {
    protected AbstractHorseGeneticMixin(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (Config.Common.HORSE_CREATIVE_TAMING.get()
                && player.isCreative()
                && player.getItemInHand(hand).getItem() instanceof SaddleItem
                && isAlive()
                && !isSaddled()
                && !isTamed()
                && !isBaby()) {
            if (!level().isClientSide) {
                tameWithName(player);
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(level().isClientSide));
        }
    }
}
