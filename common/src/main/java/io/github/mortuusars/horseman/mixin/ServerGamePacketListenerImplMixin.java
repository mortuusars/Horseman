package io.github.mortuusars.horseman.mixin;

import io.github.mortuusars.horseman.Config;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    // This method fails when mod is ran outside of devenv for some reason.
//    @ModifyVariable(method = "handleMoveVehicle", at = @At(value = "STORE"),  ordinal = 2, remap = false)
//    private boolean onHandleMoveVehicle(boolean value, @Local(ordinal = 10) double p) {
//        if (value && player.getRootVehicle() instanceof AbstractHorse && Config.Common.FIX_HORSE_MOVED_WRONGLY.get() && p < 0.36) {
//            Horseman.LOGGER.info("Suppressing 'moved wrongly'! {}", Math.sqrt(p));
//            return false;
//        }
//        return value;
//    }

    @ModifyConstant(method = "handleMoveVehicle", constant = @Constant(doubleValue = 0.0625))
    private double onHandleMoveVehicle(double value) {
        if (player.getRootVehicle() instanceof AbstractHorse && Config.Common.FIX_HORSE_MOVED_WRONGLY.get())
            return 0.36;
        return value;
    }
}
