package io.github.mortuusars.horseman.client;

public class HorseRenderUtils {
    /* Disabled in modern mc versions as I haven't found a way to implement it.
    public static boolean isJeb(LivingEntity entity) {
        return Config.Client.JEB_HORSE.get() && entity.getCustomName() != null && entity.getCustomName().getString().equals("jeb_");
    }

    public static float getAlpha(LivingEntity entity) {
        if (!Config.Client.TRANSPARENT_HORSE_ENABLED.get()) return 1.0f;

        if (!Config.Client.TRANSPARENT_HORSE_ENABLED.get()
                || Minecraft.getInstance().player == null
                || !Minecraft.getInstance().options.getCameraType().isFirstPerson()
                || !(entity instanceof AbstractHorse horse)
                || !Minecraft.getInstance().player.equals(horse.getControllingPassenger())) {
            return 1.0f;
        }

        int startAngle = Config.Client.TRANSPARENT_HORSE_START_ANGLE.get();

        float angle = Minecraft.getInstance().player.xRotO;
        if (angle < startAngle) return 1.0f;

        float maxTransparency = Config.Client.TRANSPARENT_HORSE_MAX_OPACITY.get().floatValue();
        int endAngle = Config.Client.TRANSPARENT_HORSE_END_ANGLE.get();

        float delta = (Math.min(angle, endAngle) - startAngle) / (endAngle - startAngle);
        return Mth.lerp(delta, 1.0f, maxTransparency);
    }
    */

    public interface HorsemanEquineRenderState {
        boolean getHorsemanRiddenByPlayerInFirstPerson();
        void setHorsemanRiddenByPlayerInFirstPerson(boolean value);
    }
}
