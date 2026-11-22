package io.github.mortuusars.horseman.client;

import io.github.mortuusars.horseman.Config;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.phys.EntityHitResult;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class HorseStatsTooltip {
    public static boolean extract(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        if (!Config.Common.HORSE_STATS_TOOLTIP.get()) return false;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gui.hud.isHidden()
              || minecraft.level == null
              || minecraft.player == null
              || minecraft.player.isSpectator()
              || minecraft.gui.screen() != null
              || !(minecraft.hitResult instanceof EntityHitResult entityHitResult)
              || !(entityHitResult.getEntity() instanceof AbstractHorse abstractHorse)
              || (!abstractHorse.isFood(minecraft.player.getMainHandItem())
                    && !abstractHorse.isFood(minecraft.player.getOffhandItem()))) {
            return false;
        }

        NumberFormat numberFormat = DecimalFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(2);

        ArrayList<FormattedCharSequence> lines = new ArrayList<>();
        lines.add(Component.translatable("gui.horseman.horse_stats_tooltip.stats").getVisualOrderText());

        float health = abstractHorse.getMaxHealth();
        lines.add(Component.translatable("gui.horseman.horse_stats_tooltip.health", numberFormat.format(health)).getVisualOrderText());

        if (abstractHorse.getAttributes().hasAttribute(Attributes.JUMP_STRENGTH)) {
            double jumpHeight = getJumpHeight(abstractHorse.getAttributeBaseValue(Attributes.JUMP_STRENGTH));
            lines.add(Component.translatable("gui.horseman.horse_stats_tooltip.jump_height", numberFormat.format(jumpHeight)).getVisualOrderText());
        }

        if (abstractHorse.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
            double blocksPerSecond = getSpeed(abstractHorse.getAttributeBaseValue(Attributes.MOVEMENT_SPEED));
            lines.add(Component.translatable("gui.horseman.horse_stats_tooltip.speed", numberFormat.format(blocksPerSecond)).getVisualOrderText());
        }

        int x = minecraft.getWindow().getGuiScaledWidth() / 2 + 8;
        int y = minecraft.getWindow().getGuiScaledHeight() / 2 - (int) (lines.size() / 2f * 9f);

        guiGraphics.setTooltipForNextFrame(minecraft.font, lines, x, y + 10);
        guiGraphics.extractDeferredElements(0, 0, 0); // Renders the tooltip

        return true;
    }

    /**
     * Simulates the jump basically, to give the most accurate result.
     */
    private static double getJumpHeight(double jumpStrength) {
        double velocity = jumpStrength;
        double jumpHeight = 0.0;

        while (velocity > 0.0) {
            jumpHeight += velocity;
            velocity = (velocity - 0.08) * 0.98;
        }

        return Math.max(0, jumpHeight);
    }

    /**
     * Copied from Jade.
     */
    private static double getSpeed(double speed) {
        return speed * 43.17181546666666 - 3.39999999E-7;
    }
}
