package io.github.mortuusars.horseman.mixin.fix_water_jump_meter;

import com.mojang.authlib.GameProfile;
import io.github.mortuusars.horseman.Config;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player {
    public LocalPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Shadow public abstract float getJumpRidingScale();

    @Shadow @Nullable
    public abstract PlayerRideableJumping jumpableVehicle();

    @Shadow private float jumpRidingScale;
    @Shadow private int jumpRidingTicks;
    @Unique
    private long horseman$lastTickHorseInWater = -1;

    @Inject(method = "aiStep", at = @At(value = "RETURN"))
    private void aiStep(CallbackInfo ci) {
        if (!Config.Client.PREVENT_JUMPING_IN_WATER.get()) return;

        PlayerRideableJumping vehicle = jumpableVehicle();

        if (vehicle instanceof AbstractHorse horse) {
            if (horse.isInWater()) {
                horseman$lastTickHorseInWater = level().getGameTime();
            }

            if (getJumpRidingScale() > 0 && level().getGameTime() - horseman$lastTickHorseInWater < 10) {
                jumpRidingScale = 0;
                jumpRidingTicks = 0;
            }
        }
    }
}
