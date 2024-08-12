package io.github.mortuusars.horseman;

import com.google.common.base.Preconditions;
import io.github.mortuusars.horseman.data.IPersistentDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Hitching {
    public static final String PREVENT_LEAD_DROP_TAG = "PreventLeadDrop";

    public static boolean isEnabled() {
        return Config.Common.HORSE_HITCH.get();
    }

    public static boolean requiresSlot() {
        return Config.Common.HORSE_HITCH_INVENTORY_SLOT.get();
    }

    public static boolean supportsHitching(AbstractHorse horse) {
        return !horse.getType().is(Horseman.Tags.EntityTypes.CANNOT_BE_HITCHED);
    }

    public static boolean canHitch(AbstractHorse horse) {
        if (!isEnabled() || !supportsHitching(horse) || horse.isLeashed()) {
            return false;
        }

        return !shouldHaveLeadSlot(horse) || horse.inventory.getItem(getLeadSlotIndex(horse)).is(Items.LEAD);
    }

    public static boolean isHitched(AbstractHorse horse) {
        return horse.isLeashed() && !shouldDropLeash(horse);
    }

    public static boolean shouldHaveLeadSlot(AbstractHorse horse) {
        return isEnabled() && requiresSlot() && supportsHitching(horse);
    }

    public static boolean shouldDropLeash(AbstractHorse horse) {
        if (shouldHaveLeadSlot(horse)) {
            ItemStack leadStack = horse.inventory.getItem(getLeadSlotIndex(horse));
            return leadStack.getTag() == null || !leadStack.getTag().getBoolean(PREVENT_LEAD_DROP_TAG);
        }

        return !isEnabled() || (horse instanceof IPersistentDataHolder data
                && !data.horseman$getPersistentData().getBoolean(PREVENT_LEAD_DROP_TAG));
    }

    public static void setDropsLeash(AbstractHorse horse, boolean shouldDrop) {
        if (shouldHaveLeadSlot(horse)) {
            ItemStack leadStack = horse.inventory.getItem(getLeadSlotIndex(horse));
            if (leadStack.isEmpty()) {
                return;
            }

            CompoundTag tag = leadStack.getOrCreateTag();
            if (shouldDrop) {
                tag.remove(PREVENT_LEAD_DROP_TAG);

                if (tag.isEmpty()) {
                    leadStack.setTag(null);
                }
            }
            else {
                tag.putBoolean(PREVENT_LEAD_DROP_TAG, true);
            }

            return;
        }

        if (isEnabled() && horse instanceof IPersistentDataHolder data) {
            if (shouldDrop) {
                data.horseman$getPersistentData().remove(PREVENT_LEAD_DROP_TAG);
            }
            else {
                data.horseman$getPersistentData().putBoolean(PREVENT_LEAD_DROP_TAG, true);
            }
        }
    }

    public static int getLeadSlotIndex(AbstractHorse horse) {
        Preconditions.checkState(shouldHaveLeadSlot(horse),
                "Tried to get lead slot index when the hitching is disabled or horse cannot be hitched.");

        if (horse instanceof AbstractChestedHorse chestedHorse && chestedHorse.hasChest()) {
            int columns = ((AbstractChestedHorse) horse).getInventoryColumns();
            return 2 + columns * 3;
        } else {
            return 2;
        }
    }

    public static boolean mayPlaceInLeadSlot(AbstractHorse horse, ItemStack stack) {
        return stack.is(Items.LEAD);
    }

    public static boolean mayRemoveFromLeadSlot(AbstractHorse horse, Player player) {
        return !isHitched(horse);
    }

    public static boolean isLeadSlotActive(AbstractHorse horse) {
        return !isHitched(horse);
    }
}
