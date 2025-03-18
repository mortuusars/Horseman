package io.github.mortuusars.horseman.mixin;

import io.github.mortuusars.horseman.data.HitchableHorse;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceBlock.class)
public class Hitching_FenceBlockMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                       BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.getRootVehicle() instanceof AbstractHorse horse
                && horse instanceof HitchableHorse hitchableHorse
                && HitchableHorse.canHitch(hitchableHorse)) {
            horse.setLeashedTo(player, true);
            HitchableHorse.setHitched(hitchableHorse, true);
            if (!level.isClientSide) {
                InteractionResult result = LeadItem.bindPlayerMobs(player, level, pos);
                HitchableHorse.syncHorseDataToTrackingClients(hitchableHorse);
                cir.setReturnValue(result);
            } else {
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}
