package io.github.mortuusars.horseman.world;

import io.github.mortuusars.horseman.Config;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LessWanderingHorse {
    static boolean isEnabled() {
        return Config.Server.SADDLED_HORSE_WANDER_RADIUS.get() >= 0;
    }

    @Nullable Vec3 horseman$getWanderAnchor();
    void horseman$setWanderAnchor(@Nullable Vec3 pos);

    default boolean horseman$isOutsideWanderingLimit(@NotNull Vec3 pos) {
        @Nullable Vec3 anchor = horseman$getWanderAnchor();
        if (anchor == null) {
            return false;
        }

        int maxWanderDistance = getMaxWanderDistance();
        double distance = anchor.distanceTo(pos);

        // Update anchor when far from current anchor position. For cases when horse was moved by minecart, etc.
        // This will prevent it from staying completely still when brought outside of it's wander radius.
        if (distance > maxWanderDistance + getAnchorUpdateThreshold()) {
            horseman$setWanderAnchor(((Entity)this).position());
            return false;
        }

        return distance > maxWanderDistance;
    }

    static int getMaxWanderDistance() {
        return Config.Server.SADDLED_HORSE_WANDER_RADIUS.get();
    }

    static int getAnchorUpdateThreshold() {
        return 12;
    }
}
