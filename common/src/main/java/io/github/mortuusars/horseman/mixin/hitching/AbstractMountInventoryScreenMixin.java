package io.github.mortuusars.horseman.mixin.hitching;

import io.github.mortuusars.horseman.client.LeadOnHorse;
import io.github.mortuusars.horseman.world.HitchableHorse;
import io.github.mortuusars.horseman.world.menu.LeadSlot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractMountInventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractMountInventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMountInventoryScreen.class)
public abstract class AbstractMountInventoryScreenMixin<T extends AbstractMountInventoryMenu> extends AbstractContainerScreen<T> {
    @Final
    @Shadow
    protected LivingEntity mount;

    public AbstractMountInventoryScreenMixin(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "extractBackground", at = @At(value = "RETURN"))
    private void onExtractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!(this.mount instanceof HitchableHorse hitchableHorse)) return;

        if (HitchableHorse.shouldHaveLeadSlot(hitchableHorse)) {
            int leftPos = (this.width - this.imageWidth) / 2;
            int topPos = (this.height - this.imageHeight) / 2;

            for (Slot slot : getMenu().slots) {
                if (slot instanceof LeadSlot) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, LeadOnHorse.LEAD_SLOT_TEXTURE, leftPos + slot.x - 1, topPos + slot.y - 1,
                          0, 0, 18, 18, 256, 256);
                }
            }
        } else if (HitchableHorse.requiresLead() && HitchableHorse.hasLead(hitchableHorse)) {
            LeadOnHorse.renderInventory(graphics, mouseX, mouseY, leftPos, topPos, hitchableHorse.horseman$asHorse());
        }
    }

    @Inject(method = "extractRenderState", at = @At(value = "RETURN"))
    private void onExtractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!(this.mount instanceof HitchableHorse hitchableHorse) || !HitchableHorse.shouldHaveLeadSlot(hitchableHorse)) return;

        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        for (Slot slot : getMenu().slots) {
            if (slot instanceof LeadSlot) {
                // Disabled slot overlay
                if (slot.getItem().is(Items.LEAD) && HitchableHorse.isHitched(hitchableHorse)) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, LeadOnHorse.LEAD_SLOT_TEXTURE, leftPos + slot.x - 1, topPos + slot.y - 1,
                          0, 18, 18, 18, 256, 256);
                }
            }
        }
    }
}
