package io.github.mortuusars.horseman.client;

import io.github.mortuusars.horseman.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class HorseRenderUtils {
//    public static boolean isJeb(LivingEntity entityRenderState) {
//        entityRenderState.getCustomName()
//        return ModConfig.getInstance().jeb_Horses && entityRenderState.customName != null && "jeb_".equals(entityRenderState.customName.getString());
//    }

    public static int getAlpha(LivingEntity entity) {
        if (!Config.Client.TRANSPARENT_HORSE_ENABLED.get()) return 255;

        if (!Config.Client.TRANSPARENT_HORSE_ENABLED.get()
                || Minecraft.getInstance().player == null
                || !Minecraft.getInstance().options.getCameraType().isFirstPerson()
                || !(entity instanceof AbstractHorse horse)
                || !Minecraft.getInstance().player.equals(horse.getControllingPassenger())) {
            return 255;
        }

        int startAngle = Config.Client.TRANSPARENT_HORSE_START_ANGLE.get();

        float angle = Minecraft.getInstance().player.xRotO;
        if (angle < startAngle) return 255;

        int maxTransparency = Config.Client.TRANSPARENT_HORSE_MAX_TRANSPARENCY.get();
        int endAngle = Config.Client.TRANSPARENT_HORSE_END_ANGLE.get();

        float delta = (Math.min(angle, endAngle) - startAngle) / (endAngle - startAngle);
        return (int) Mth.lerp(delta, 255, maxTransparency);
    }
}
