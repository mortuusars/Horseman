package io.github.mortuusars.horseman.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    @ModifyVariable(method = "handleMoveVehicle", at = @At(value = "STORE"),  ordinal = 2)
    private boolean movedWrongly(boolean value, @Local(ordinal = 10) double p) {
        if (value && player.getRootVehicle() instanceof AbstractHorse && Config.Common.FIX_HORSE_MOVED_WRONGLY.get() && p < 0.36) {
            Horseman.LOGGER.info("Suppressing 'moved wrongly'! {}", Math.sqrt(p));
            return false;
        }
        return value;
    }
}
