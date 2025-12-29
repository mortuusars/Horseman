package io.github.mortuusars.horseman.mixin.hitching;

import io.github.mortuusars.horseman.client.LeadOnHorse;
import io.github.mortuusars.horseman.world.HitchableHorse;
import io.github.mortuusars.horseman.world.menu.LeadSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseInventoryScreen.class)
public abstract class HorseInventoryScreenMixin extends AbstractContainerScreen<HorseInventoryMenu> {
    @Shadow
    @Final
    private AbstractHorse horse;

    public HorseInventoryScreenMixin(HorseInventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/HorseInventoryScreen;renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V"))
    private void onRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!(this.horse instanceof HitchableHorse hitchableHorse)) return;

        if (HitchableHorse.shouldHaveLeadSlot(hitchableHorse)) {
            if (!HitchableHorse.isHitched(hitchableHorse)) return;

            for (Slot slot : getMenu().slots) {
                if (slot instanceof LeadSlot && slot.getItem().is(Items.LEAD)) {
                    int leftPos = (this.width - this.imageWidth) / 2;
                    int topPos = (this.height - this.imageHeight) / 2;
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, LeadOnHorse.LEAD_SLOT_TEXTURE, leftPos + slot.x - 1, topPos + slot.y - 1,
                            0, 18, 18, 18, 256, 256);
                }
            }
        } else if (HitchableHorse.requiresLead() && HitchableHorse.hasLead(hitchableHorse)) {
            LeadOnHorse.renderInventory(guiGraphics, mouseX, mouseY, partialTick, leftPos, topPos, this.horse);
        }
    }

    @Inject(method = "renderBg", at = @At(value = "RETURN"))
    private void onRenderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        if (!(this.horse instanceof HitchableHorse hitchableHorse)) return;

        if (HitchableHorse.shouldHaveLeadSlot(hitchableHorse)) {
            int leftPos = (this.width - this.imageWidth) / 2;
            int topPos = (this.height - this.imageHeight) / 2;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, LeadOnHorse.LEAD_SLOT_TEXTURE, leftPos + 7, topPos + 53,
                    0, 0, 18, 18, 256, 256);
        }
    }
}
