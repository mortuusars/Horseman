package io.github.mortuusars.horseman.client;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class SwitchInventory {
    public static final WidgetSprites SWITCH_BUTTON_SPRITES = new WidgetSprites(
            Horseman.resource("switch_inventory_button"),
            Horseman.resource("switch_inventory_button_disabled"),
            Horseman.resource("switch_inventory_button_highlighted"));

    public static @Nullable Double mouseX, mouseY;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isEnabled() {
        return Config.Client.INVENTORY_SWITCH_ENABLED.get();
    }

    public static void switchToInventory(AbstractContainerScreen<?> screen) {
        if (Minecraft.getInstance().player == null) return;

        double cursorX = Minecraft.getInstance().mouseHandler.xpos();
        double cursorY = Minecraft.getInstance().mouseHandler.ypos();

        screen.onClose();
        Minecraft.getInstance().setScreen(new InventoryScreen(Minecraft.getInstance().player));

        // Move cursor to previous position, as setScreen resets it to center every time:
        Minecraft.getInstance().execute(() -> {
            GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().handle(), cursorX, cursorY);
        });
    }

    public static void switchToMount(AbstractContainerScreen<?> screen) {
        if (Minecraft.getInstance().player == null) return;
        // Cannot move cursor like from mount, because screen is opened later due to it being sent from server.
        // So we remember pos here, and set it when screen is initialized.
        mouseX = Minecraft.getInstance().mouseHandler.xpos();
        mouseY = Minecraft.getInstance().mouseHandler.ypos();

        screen.onClose();
        Minecraft.getInstance().player.sendOpenInventory();
    }

    public static void restoreMousePosIfNeeded() {
        if (SwitchInventory.mouseX != null && SwitchInventory.mouseY != null) {
            GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().handle(), SwitchInventory.mouseX, SwitchInventory.mouseY);
            // Clear remembered cursor pos after setting, to not apply it again when not needed:
            SwitchInventory.mouseX = null;
            SwitchInventory.mouseY = null;
        }
    }
}
