package io.github.mortuusars.horseman.mixin.summoning;

import io.github.mortuusars.horseman.world.item.CopperHornItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camel.class)
public class CamelMixin {
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        // Allow Copper Horn to process its interaction logic:
        if (player.isSecondaryUseActive() && player.getItemInHand(hand).getItem() instanceof CopperHornItem) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
