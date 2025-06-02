package io.github.mortuusars.horseman.mixin.ride_though_leaves;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.world.LeavesCollisionMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
    @ModifyReturnValue(method = "getCollisionShape", at = @At("RETURN"))
    protected VoxelShape getCollisionShape(VoxelShape original,
                                           @Local(argsOnly = true) BlockState state,
                                           @Local(argsOnly = true) BlockGetter level,
                                           @Local(argsOnly = true) BlockPos pos,
                                           @Local(argsOnly = true) CollisionContext context) {
        if (!Config.Server.SPEC.isLoaded()) return original;
        LeavesCollisionMode collisionMode = Config.Server.LEAVES_COLLISION_MODE_WHEN_MOUNTED.get();
        if (collisionMode == LeavesCollisionMode.FULL) return original;

        if (state.getBlock() instanceof LeavesBlock
                && context instanceof EntityCollisionContext entityContext
                && entityContext.getEntity() instanceof AbstractHorse horse
                && horse.getControllingPassenger() instanceof Player) {

            if (collisionMode == LeavesCollisionMode.IGNORE_LOWEST_BLOCK
                    && level.getBlockState(pos.below()).getBlock() instanceof LeavesBlock) {
                return original;
            }

            return Shapes.empty();
        }

        return original;
    }
}
