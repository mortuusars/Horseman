package io.github.mortuusars.horseman.client;

import io.github.mortuusars.horseman.Config;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.phys.EntityHitResult;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class HorseStatsTooltip {
    public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!Config.Common.HORSE_STATS_TOOLTIP.get()) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui
                || minecraft.level == null
                || minecraft.player == null
                || minecraft.player.isSpectator()
                || minecraft.screen != null
                || !(minecraft.hitResult instanceof EntityHitResult entityHitResult)
                || !(entityHitResult.getEntity() instanceof Horse horse)
                || (!minecraft.player.getMainHandItem().is(ItemTags.HORSE_FOOD)
                    && !minecraft.player.getOffhandItem().is(ItemTags.HORSE_FOOD))) {
            return;
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
        guiGraphics.renderTooltip(minecraft.font, lines.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()),
                x, y + 10, DefaultTooltipPositioner.INSTANCE, null);
    }

    private static double getJumpHeight(double jumpStrength) {
        return -0.1817584952 * jumpStrength * jumpStrength * jumpStrength + 3.689713992 * jumpStrength * jumpStrength +
                2.128599134 * jumpStrength - 0.343930367;
    }
}
