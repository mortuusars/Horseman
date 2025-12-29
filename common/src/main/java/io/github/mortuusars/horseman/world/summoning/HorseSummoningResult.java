package io.github.mortuusars.horseman.world.summoning;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public enum HorseSummoningResult {
    SUCCESS,
    NO_BOUND_HORSE,
    HORSE_IS_DEAD,
    TOO_FAR,
    INVALID_DIMENSION,
    NO_SPACE,
    ERROR_HORSE_IS_NOT_BOUND,
    ERROR_ENTITY_NOT_CREATED;

    public @Nullable Component getMessage() {
        return switch (this) {
            case SUCCESS -> null;
            case NO_BOUND_HORSE -> Component.translatable("gui.horseman.summoning.cannot_summon.no_bound_horse");
            case HORSE_IS_DEAD -> Component.translatable("gui.horseman.summoning.cannot_summon.dead");
            case TOO_FAR -> Component.translatable("gui.horseman.summoning.cannot_summon.too_far");
            case INVALID_DIMENSION ->
                  Component.translatable("gui.horseman.summoning.cannot_summon.in_other_dimension");
            case NO_SPACE -> Component.translatable("gui.horseman.summoning.cannot_summon.no_space");
            case ERROR_HORSE_IS_NOT_BOUND, ERROR_ENTITY_NOT_CREATED ->
                  Component.translatable("gui.horseman.summoning.cannot_summon.wrong_or_defective_horse");
        };
    }
}
