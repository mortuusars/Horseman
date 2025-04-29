package io.github.mortuusars.horseman.mixin.switch_inventory;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.client.SwitchInventory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.lwjgl.glfw.GLFW;
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

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (((AbstractContainerScreen<?>) (Object) this) instanceof HorseInventoryScreen
                && Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)
                && Screen.hasControlDown()) {
            SwitchInventory.switchFromHorse(((AbstractContainerScreen<?>)(Object) this));
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
            cir.setReturnValue(true);
        }

        if (((Object) this) instanceof InventoryScreen
                && Minecraft.getInstance().player != null && Minecraft.getInstance().player.jumpableVehicle() instanceof AbstractHorse
                && Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)
                && Screen.hasControlDown()) {
            SwitchInventory.switchFromInventory((AbstractContainerScreen<?>)(Object) this);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        if (Minecraft.getInstance().gameMode == null) return;

        if (((Object) this) instanceof HorseInventoryScreen) {
            if (SwitchInventory.mouseX != null && SwitchInventory.mouseY != null) {
                GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().getWindow(), SwitchInventory.mouseX, SwitchInventory.mouseY);
                // Clear remembered cursor pos after setting, to not apply it again when not needed:
                SwitchInventory.mouseX = null;
                SwitchInventory.mouseY = null;
            }

            ImageButton button = new ImageButton(
                    leftPos + Config.Client.INVENTORY_TOGGLE_HORSE_BUTTON_X.get(),
                    topPos + Config.Client.INVENTORY_TOGGLE_HORSE_BUTTON_Y.get(),
                    14, 15,
                    0, 0, 15,
                    SwitchInventory.BUTTON_TEXTURE,
                    256, 256,
                    b -> SwitchInventory.switchFromHorse(((AbstractContainerScreen<?>)(Object) this)));

            button.setTooltip(Tooltip.create(Component.translatable("gui.horseman.switch_inventory.button.from_horse.tooltip",
                Component.literal(Minecraft.getInstance().options.keyInventory.getTranslatedKeyMessage().getString()).withStyle(ChatFormatting.GRAY)
            )));

            addRenderableWidget(button);
        }

        if (((AbstractContainerScreen<?>)(Object) this) instanceof InventoryScreen
            && Minecraft.getInstance().gameMode.isServerControlledInventory()) {

            ImageButton button = new ImageButton(
                    leftPos + Config.Client.INVENTORY_TOGGLE_PLAYER_BUTTON_X.get(),
                    topPos + Config.Client.INVENTORY_TOGGLE_PLAYER_BUTTON_Y.get(),
                    14, 15,
                    0, 0, 15,
                    SwitchInventory.BUTTON_TEXTURE,
                    256, 256,
                    b -> SwitchInventory.switchFromInventory(((AbstractContainerScreen<?>)(Object) this)));

            Component vehicleName = Minecraft.getInstance().player != null
                    && Minecraft.getInstance().player.getVehicle() instanceof LivingEntity entity
                    ? entity.getName()
                    : Component.translatable("gui.horseman.switch_inventory.button.from_inventory..tooltip.mount");

            button.setTooltip(Tooltip.create(Component.translatable("gui.horseman.switch_inventory.button.from_inventory.tooltip",
                    vehicleName,
                    Component.literal(Minecraft.getInstance().options.keyInventory.getTranslatedKeyMessage().getString()).withStyle(ChatFormatting.GRAY)
            )));

            addRenderableWidget(button);
        }
    }
}

