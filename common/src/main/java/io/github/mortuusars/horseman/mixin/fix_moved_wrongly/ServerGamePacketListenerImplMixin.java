package io.github.mortuusars.horseman.mixin.fix_moved_wrongly;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.mortuusars.horseman.Config;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    @ModifyExpressionValue(method = "handleMoveVehicle", at = @At(value = "CONSTANT", args = "doubleValue=0.0625"))
    private double onHandleMoveVehicle(double original) {
        if (player.getRootVehicle() instanceof AbstractHorse && Config.Common.FIX_HORSE_MOVED_WRONGLY.get())
            return 0.36;
        return original;
    }
}
