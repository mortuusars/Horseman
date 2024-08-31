package io.github.mortuusars.horseman.forge.mixin.realistic_horse_genetics;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.PlatformHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;

@Mixin(AbstractHorseGenetic.class)
public abstract class AbstractHorseGeneticMixin extends AbstractChestedHorse {
    protected AbstractHorseGeneticMixin(EntityType<? extends AbstractChestedHorse> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!Config.Common.HORSE_SHEARS_REMOVE_CHEST.get() || isVehicle() || isBaby()
                || !isTamed() || !hasChest() || player.isSecondaryUseActive()) {
            return;
        }

        ItemStack itemInHand = player.getItemInHand(hand);
        if (PlatformHelper.canShear(itemInHand)) {
            if (level().isClientSide) {
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }

            cir.setReturnValue(super.mobInteract(player, hand));
        }
    }
}
