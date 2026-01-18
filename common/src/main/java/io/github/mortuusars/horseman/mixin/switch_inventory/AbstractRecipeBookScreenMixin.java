package io.github.mortuusars.horseman.mixin.switch_inventory;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.client.SwitchInventory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.RecipeBookMenu;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractRecipeBookScreen.class)
public abstract class AbstractRecipeBookScreenMixin<T extends RecipeBookMenu> extends AbstractContainerScreen<T> {
    @Unique
    private @Nullable ImageButton horseman$playerInventorySwitchButton;

    public AbstractRecipeBookScreenMixin(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        if (!SwitchInventory.isEnabled()
              || Minecraft.getInstance().gameMode == null
              || !Minecraft.getInstance().gameMode.isServerControlledInventory()) {
            return;
        }

        if ((Screen) this instanceof InventoryScreen inventoryScreen) {
            horseman$playerInventorySwitchButton = new ImageButton(
                  leftPos + Config.Client.INVENTORY_SWITCH_PLAYER_BUTTON_X.get(),
                  topPos + Config.Client.INVENTORY_SWITCH_PLAYER_BUTTON_Y.get(),
                  14,
                  15,
                  SwitchInventory.SWITCH_BUTTON_RIGHT_SPRITES,
                  b -> SwitchInventory.switchToMount(inventoryScreen));

            Component vehicleName = Minecraft.getInstance().player != null
                  && Minecraft.getInstance().player.getVehicle() instanceof Entity entity
                  ? entity.getName()
                  : Component.translatable("gui.horseman.switch_inventory.button.to_mount.tooltip.mount");

            horseman$playerInventorySwitchButton.setTooltip(Tooltip.create(Component.translatable("gui.horseman.switch_inventory.button.to_mount.tooltip",
                  vehicleName,
                  Component.literal(Minecraft.getInstance().options.keyInventory.getTranslatedKeyMessage().getString())
                        .withStyle(ChatFormatting.GRAY)
            )));

            addRenderableWidget(horseman$playerInventorySwitchButton);
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (horseman$playerInventorySwitchButton != null) {
            // Update button pos to adjust to recipe book ui opening/closing
            horseman$playerInventorySwitchButton.setX(leftPos + Config.Client.INVENTORY_SWITCH_PLAYER_BUTTON_X.get());
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!SwitchInventory.isEnabled()
              || Minecraft.getInstance().gameMode == null
              || !Minecraft.getInstance().gameMode.isServerControlledInventory()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        //noinspection ConstantValue
        if (((Screen)this instanceof InventoryScreen)
              && minecraft.options.keyInventory.matches(event)
              && minecraft.hasControlDown()) {
            SwitchInventory.switchToMount(this);
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
            cir.setReturnValue(true);
        }
    }
}