package io.github.mortuusars.horseman.mixin.switch_inventory;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.client.SwitchInventory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractMountInventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {
    @Shadow protected int leftPos;
    @Shadow protected int topPos;

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        if (!SwitchInventory.isEnabled()) return;
        if (Minecraft.getInstance().gameMode == null) return;

        if (((Object) this) instanceof AbstractMountInventoryScreen) {
            SwitchInventory.restoreMousePosIfNeeded();

            ImageButton switchButton = new ImageButton(leftPos + Config.Client.INVENTORY_SWITCH_HORSE_BUTTON_X.get(),
                  topPos + Config.Client.INVENTORY_SWITCH_HORSE_BUTTON_Y.get(), 14, 15,
                  SwitchInventory.SWITCH_BUTTON_LEFT_SPRITES,
                  _ -> SwitchInventory.switchToInventory(((AbstractContainerScreen<?>)(Object) this)));

            switchButton.setTooltip(Tooltip.create(Component.translatable("gui.horseman.switch_inventory.button.to_inventory.tooltip",
                  Component.literal(Minecraft.getInstance().options.keyInventory.getTranslatedKeyMessage().getString()).withStyle(ChatFormatting.GRAY)
            )));

            addRenderableWidget(switchButton);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!SwitchInventory.isEnabled()) return;

        Minecraft minecraft = Minecraft.getInstance();

        if (((AbstractContainerScreen<?>) (Object) this) instanceof AbstractMountInventoryScreen<?>
                && minecraft.options.keyInventory.matches(event)
                && minecraft.hasControlDown()) {
            SwitchInventory.switchToInventory(((AbstractContainerScreen<?>)(Object) this));
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
            cir.setReturnValue(true);
        }
    }
}