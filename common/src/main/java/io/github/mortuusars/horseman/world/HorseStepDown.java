package io.github.mortuusars.horseman.world;

import io.github.mortuusars.horseman.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HorseStepDown {
    public static boolean shouldHorseStepDown(AbstractHorse horse) {
        if (!Config.Server.HORSE_FAST_STEP_DOWN.get()) return false;
        if (!(horse.getControllingPassenger() instanceof Player)) return false;
        if (horse.onGround() || horse.isJumping() || horse.isInWater()) return false;
        return horse.fallDistance > 0.0 && horse.fallDistance < 0.2 && canStepDownOnBlock(horse);
    }

    private static boolean canStepDownOnBlock(AbstractHorse horse) {
        Level level = horse.level();
        BlockPos pos = horse.blockPosition();
        BlockState state = level.getBlockState(pos.below());
        boolean isOnFluid = !state.getFluidState().isEmpty() || !level.getFluidState(pos).isEmpty();
        if (!isOnFluid && !state.getCollisionShape(level, pos.below()).isEmpty()) return true;
        return Config.Server.HORSE_FAST_STEP_DOWN_TWO_BLOCKS.get()
                && !level.getBlockState(pos.below(2)).getCollisionShape(level, pos.below(2)).isEmpty();
    }
}
