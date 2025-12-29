package io.github.mortuusars.horseman.mixin.fix_camera_lag;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import io.github.mortuusars.horseman.Config;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player {
    public LocalPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @ModifyReturnValue(method = "getViewYRot", at = @At("RETURN"))
    private float onGetViewYRot(float original) {
        return Config.Client.FIX_MOUNTED_CAMERA_LAG.get() && getVehicle() instanceof AbstractHorse ? getYRot() : original;
    }
}
