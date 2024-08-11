package io.github.mortuusars.horse_please.integration.kubejs.event;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Fired at the very end of a shot, when frame is added to the film.
 * Fired only on the server side.
 */
public class FrameAddedEventJS extends PlayerEventJS {
    private final Player player;
    private final ItemStack cameraStack;
    private final CompoundTag frame;

    public FrameAddedEventJS(Player player, ItemStack cameraStack, CompoundTag frame) {
        this.player = player;
        this.cameraStack = cameraStack;
        this.frame = frame;
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

    public CompoundTag getFrame() {
        return frame;
    }
}
