package io.github.mortuusars.horseman.world.summoning;

import org.jetbrains.annotations.Nullable;

/**
 * Injected in AbstractHorse.<br>
 * Injected interfaces must have all methods as 'default'.
 */
public interface SummonableHorse {
    default @Nullable BoundData getHorsemanBoundData() {
        throw new IllegalStateException("This method should be implemented.");
    }

    default void setHorsemanBoundData(@Nullable BoundData data) {
        throw new IllegalStateException("This method should be implemented.");
    }
}
