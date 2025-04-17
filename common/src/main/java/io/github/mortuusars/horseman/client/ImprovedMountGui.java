package io.github.mortuusars.horseman.client;

import io.github.mortuusars.horseman.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

public class ImprovedMountGui {
    private static long vehicleInWaterLastTick = -1;

    public static boolean shouldRenderJumpBar() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null || !mc.gameMode.hasExperience()) return false;
        if (mc.level == null || mc.player == null || mc.player.jumpableVehicle() == null) return false;

        if (mc.player.jumpableVehicle() instanceof LivingEntity entity && entity.isInWater()) {
            vehicleInWaterLastTick = mc.level.getGameTime();
        }

        boolean isJumping = mc.options.keyJump.isDown() || mc.player.getJumpRidingScale() > 0;
        return isJumping && (mc.level.getGameTime() - vehicleInWaterLastTick >= 10);
    }

    public static boolean isEnabled() {
        return Config.Client.IMPROVED_MOUNT_GUI.get();
    }
}
