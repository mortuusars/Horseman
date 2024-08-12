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
        double distanceToFence = Vec3.atCenterOf(pos).distanceTo(player.position());

        if (distanceToFence < 5 && player.getRootVehicle() instanceof AbstractHorse horse && Hitching.canHitch(horse)) {
            if (!level.isClientSide) {
                horse.setLeashedTo(player, true);
                Hitching.setDropsLeash(horse, false);
                cir.setReturnValue(LeadItem.bindPlayerMobs(player, level, pos));
            }
            else {
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}
