package io.github.mortuusars.horseman.forge.mixin.realistic_horse_genetics;

import io.github.mortuusars.horseman.Hitching;
import io.github.mortuusars.horseman.Horseman;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sekelsta.horse_colors.client.HorseGui;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;

@Mixin(HorseGui.class)
public abstract class HorseGUIMixin extends HorseInventoryScreen {
    @Shadow @Final private AbstractHorseGenetic horseGenetic;
    @Unique
    private static final ResourceLocation LEAD_SLOT_TEXTURE = Horseman.resource("textures/gui/lead_slot.png");

    public HorseGUIMixin(HorseInventoryMenu menu, Inventory playerInventory, AbstractHorse horse) {
        super(menu, playerInventory, horse);
    }

    @Inject(method = "renderBg", at = @At(value = "RETURN"))
    private void onRenderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY, CallbackInfo ci) {
        if (Hitching.shouldHaveLeadSlot(this.horseGenetic)) {
            int leftPos = (this.width - this.imageWidth) / 2;
            int topPos = (this.height - this.imageHeight) / 2;
            guiGraphics.blit(LEAD_SLOT_TEXTURE, leftPos + 7, topPos + 53, 0, 0, 18, 18);
        }
    }
}

