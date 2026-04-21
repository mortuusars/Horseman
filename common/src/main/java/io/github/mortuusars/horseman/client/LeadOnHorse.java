package io.github.mortuusars.horseman.client;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.world.HitchableHorse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import org.spongepowered.asm.mixin.Unique;

public class LeadOnHorse {
    @Unique
    public static final Identifier LEAD_SLOT_TEXTURE = Horseman.identifier("textures/gui/lead_slot.png");

    public static void renderInventory(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY,
                                       int leftPos, int topPos, AbstractHorse horse) {
        if (Config.Client.HORSE_HITCH_RENDER_LEAD_WITHOUT_SLOT.get() && horse instanceof HitchableHorse hitchableHorse) {
            guiGraphics.item(hitchableHorse.horseman$getLead(), leftPos + 62, topPos + 18);

            if (mouseX >= leftPos + 62 && mouseX < leftPos + 62 + 16 && mouseY >= topPos + 18 && mouseY < topPos + 18 + 16) {
                guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font, Component.translatable("gui.horseman.has_lead"), mouseX, mouseY);
            }
        }
    }
}
