package io.github.mortuusars.horseman.mixin.less_wander;

import com.mojang.authlib.GameProfile;
import io.github.mortuusars.horseman.world.LessWanderingHorse;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Inject(method = "stopRiding", at = @At("HEAD"))
    private void onStopRiding(CallbackInfo ci) {
        if (getVehicle() instanceof LessWanderingHorse horse) {
            horse.horseman$setWanderAnchor(getVehicle().position());
        }
    }
}
