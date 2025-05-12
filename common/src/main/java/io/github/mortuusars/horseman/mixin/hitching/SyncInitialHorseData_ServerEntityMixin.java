package io.github.mortuusars.horseman.mixin.hitching;

import io.github.mortuusars.horseman.horse.HitchableHorse;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public class SyncInitialHorseData_ServerEntityMixin {
    @Shadow @Final private Entity entity;

    @Inject(method = "addPairing", at = @At("RETURN"))
    private void onSendPairingData(ServerPlayer player, CallbackInfo ci) {
        if (this.entity instanceof HitchableHorse horse && HitchableHorse.isEnabled()) {
            HitchableHorse.syncHorseDataToClient(horse, player);
        }
    }
}
