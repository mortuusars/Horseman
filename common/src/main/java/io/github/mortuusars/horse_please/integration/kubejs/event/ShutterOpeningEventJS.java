package io.github.mortuusars.horse_please.integration.kubejs.event;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Fired when Camera tries to take a photo. Cancelable.
 * Client-side event wouldn't fire if server-side event was canceled.
 * If canceled only on the client - shutter would be opened, but the image would not be captured.
 * All checks are passed at this point, and if this event is not canceled - photo will be taken.
 */
public class ShutterOpeningEventJS extends PlayerEventJS {
    private final Player player;
    private final ItemStack cameraStack;
    private final int lightLevel;
    private final boolean shouldFlashFire;

    public ShutterOpeningEventJS(Player player, ItemStack cameraStack, int lightLevel, boolean shouldFlashFire) {
        this.player = player;
        this.cameraStack = cameraStack;
        this.lightLevel = lightLevel;
        this.shouldFlashFire = shouldFlashFire;
    }

    @Override
    public Player getEntity() {
        return player;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public ItemStack getCameraStack() {
        return cameraStack;
    }

    public int getLightLevel() {
        return lightLevel;
    }

    public boolean shouldFlashFire() {
        return shouldFlashFire;
    }
}
