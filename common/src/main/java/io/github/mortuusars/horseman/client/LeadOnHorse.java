package io.github.mortuusars.horseman.client;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.horse.HitchableHorse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class LeadOnHorse {
    public static void renderInventory(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick,
                                       int leftPos, int topPos, AbstractHorse horse) {
        if (Config.Client.HORSE_HITCH_RENDER_LEAD_WITHOUT_SLOT.get() && horse instanceof HitchableHorse hitchableHorse) {
            guiGraphics.pose().pushPose();
            guiGraphics.renderItem(hitchableHorse.horseman$getLead(), leftPos + 62, topPos + 18);
            guiGraphics.pose().popPose();

            if (mouseX >= leftPos + 62 && mouseX < leftPos + 62 + 16 && mouseY >= topPos + 18 && mouseY < topPos + 18 + 16) {
                guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal("Has Hitching Lead"), mouseX, mouseY);
            }
        }
    }
}
