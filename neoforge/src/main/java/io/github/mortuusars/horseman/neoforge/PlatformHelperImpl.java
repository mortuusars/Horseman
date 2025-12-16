package io.github.mortuusars.horseman.neoforge;

import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforge.common.ItemAbilities;

public class PlatformHelperImpl {
    public static boolean canShear(ItemStack stack) {
        return stack.canPerformAction(ItemAbilities.SHEARS_REMOVE_ARMOR);
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static boolean isModLoading(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }
}
