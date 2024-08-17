package io.github.mortuusars.horseman.mixin;

import io.github.mortuusars.horseman.Hitching;
import io.github.mortuusars.horseman.data.IPersistentDataHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceBlock.class)
public class FenceBlockMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                       BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.getRootVehicle() instanceof AbstractHorse horse) {
            /*
             Cannot properly check 'canHitch' on client because we don't have access to horse inventory.
             Returning InteractionResult.SUCCESS prevents placing ghost block for a split second (if player holds a block).
             This can potentially interfere with other item/block logic when hitching wasn't done.
             But it still shouldn't be a problem because it's only when player is on a horse and important logic is done server-side anyway.
             Worst case scenario some particles or other client effect would be missing. (If hitching wasn't done)
            */
            if (level.isClientSide) {
                // Some of the checks from Hitching#canHitch to reduce interference.
                if (Hitching.isEnabled() && Hitching.supportsHitching(horse) && !horse.isLeashed()) {
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
                return;
            }

            if (Hitching.canHitch(horse)) {
                horse.setLeashedTo(player, true);
                Hitching.setDropsLeash(horse, false);
                cir.setReturnValue(LeadItem.bindPlayerMobs(player, level, pos));
            }
        }
    }
}
