package io.github.mortuusars.horseman.neoforge.mixin.fix_moved_wrongly;

import io.github.mortuusars.horseman.Config;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * This mixin was previously in common module, as it's the same for both fabric and forge.
 * But for some interesting reason, fabric game launch started failing with 'mixin loaded too early' error for unrelated fabric mixin, not even in my code.
 * The neat part is that it worked before without an issue, and I don't recall adding anything that would cross this mixin somehow.
 * Why it's always fabric?
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    @ModifyConstant(method = "handleMoveVehicle", constant = @Constant(doubleValue = 0.0625))
    private double onHandleMoveVehicle(double value) {
        if (player.getRootVehicle() instanceof AbstractHorse && Config.Server.FIX_HORSE_MOVED_WRONGLY.get())
            return 0.36;
        return value;
    }
}
