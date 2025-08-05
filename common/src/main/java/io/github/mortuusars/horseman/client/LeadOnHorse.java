package io.github.mortuusars.horseman.client;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.world.HitchableHorse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Unique;

public class LeadOnHorse {
    @Unique
    public static final ResourceLocation LEAD_SLOT_TEXTURE = Horseman.resource("textures/gui/lead_slot.png");

    public static void renderInventory(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick,
                                       int leftPos, int topPos, AbstractHorse horse) {
        if (Config.Client.HORSE_HITCH_RENDER_LEAD_WITHOUT_SLOT.get() && horse instanceof HitchableHorse hitchableHorse) {
            guiGraphics.renderItem(hitchableHorse.horseman$getLead(), leftPos + 62, topPos + 18);

            if (mouseX >= leftPos + 62 && mouseX < leftPos + 62 + 16 && mouseY >= topPos + 18 && mouseY < topPos + 18 + 16) {
                guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font, Component.translatable("gui.horseman.has_lead"), mouseX, mouseY);
            }
        }
    }
}
