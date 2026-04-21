package io.github.mortuusars.horseman.fabric.client.render;

import io.github.mortuusars.horseman.client.HorseStatsTooltip;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

public class HorseStatsTooltipElement implements HudElement {
    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
        HorseStatsTooltip.extract(graphics, deltaTracker);
    }
}
