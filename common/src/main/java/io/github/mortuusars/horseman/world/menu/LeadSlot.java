package io.github.mortuusars.horseman.world.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class LeadSlot extends Slot {
    public LeadSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
