package io.github.mortuusars.horseman.client;

import io.github.mortuusars.horseman.Config;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.item.ItemStack;
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
                || !(entityHitResult.getEntity() instanceof AbstractHorse horse)
                || (!isStatsFood(minecraft.player.getMainHandItem())
                    && !isStatsFood(minecraft.player.getOffhandItem()))) {
            return false;
        }

        NumberFormat numberFormat = DecimalFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(2);

        ArrayList<FormattedCharSequence> lines = new ArrayList<>();
        lines.add(Component.translatable("gui.horseman.horse_stats_tooltip.stats").getVisualOrderText());

        float health = horse.getMaxHealth();
        lines.add(Component.translatable("gui.horseman.horse_stats_tooltip.health", numberFormat.format(health)).getVisualOrderText());

        if (horse.getAttributes().hasAttribute(Attributes.JUMP_STRENGTH)) {
            double jumpHeight = getJumpHeight(horse.getAttributeBaseValue(Attributes.JUMP_STRENGTH));
            lines.add(Component.translatable("gui.horseman.horse_stats_tooltip.jump_height", numberFormat.format(jumpHeight)).getVisualOrderText());
        }

        if (horse.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
            double blocksPerSecond = horse.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * 42.16;
            lines.add(Component.translatable("gui.horseman.horse_stats_tooltip.speed", numberFormat.format(blocksPerSecond)).getVisualOrderText());
        }

        int x = minecraft.getWindow().getGuiScaledWidth() / 2 + 8;
        int y = minecraft.getWindow().getGuiScaledHeight() / 2 - (int)(lines.size() / 2f * 9f);

        guiGraphics.setTooltipForNextFrame(minecraft.font, lines, x, y + 10);
        guiGraphics.extractDeferredElements(0, 0, 0); // Renders the tooltip

        return true;
    }

    private static boolean isStatsFood(ItemStack stack) {
        return stack.is(ItemTags.HORSE_FOOD) || stack.is(ItemTags.ZOMBIE_HORSE_FOOD);
    }

    private static double getJumpHeight(double jumpStrength) {
        return -0.1817584952 * jumpStrength * jumpStrength * jumpStrength + 3.689713992 * jumpStrength * jumpStrength +
                2.128599134 * jumpStrength - 0.343930367;
    }
}
