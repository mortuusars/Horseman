package io.github.mortuusars.horseman.client;

import io.github.mortuusars.horseman.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;

public class TransparentMount {
    // This is for setting correct opacity for some render layers. We don't have access to render state in those places.
    public static float storedOpacity = 1f;

    public static float getOpacity(LivingEntity entity) {
        Minecraft mc = Minecraft.getInstance();

        if (!Config.Client.TRANSPARENT_MOUNT_ENABLED.get()
              || mc.player == null
              || !mc.options.getCameraType().isFirstPerson()
              || !entity.hasPassenger(mc.player)
              || (Config.Client.TRANSPARENT_MOUNT_ONLY_HORSES.get() && !(entity instanceof AbstractHorse))) {
            return 1.0f;
        }

        int startAngle = Config.Client.TRANSPARENT_MOUNT_START_ANGLE.get();

        float angle = mc.player.xRotO;
        if (angle < startAngle) return 1.0f;

        float maxTransparency = Config.Client.TRANSPARENT_MOUNT_MAX_OPACITY.get().floatValue();
        int endAngle = Config.Client.TRANSPARENT_MOUNT_END_ANGLE.get();

        float delta = (Math.min(angle, endAngle) - startAngle) / (endAngle - startAngle);
        return Mth.lerp(delta, 1.0f, maxTransparency);
    }

    public static int applyOpacity(int colorArgb, float opacity) {
        if (opacity <= 0f || opacity >= 1f) {
            return colorArgb;
        }

        return ARGB.colorFromFloat(opacity, ARGB.redFloat(colorArgb), ARGB.greenFloat(colorArgb), ARGB.blueFloat(colorArgb));
    }

    public interface RenderState {
        float horseman$getOpacity();
        void horseman$setOpacity(float opacity);
    }
}
