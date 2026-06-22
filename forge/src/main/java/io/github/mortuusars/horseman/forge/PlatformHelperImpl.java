package io.github.mortuusars.horseman.forge;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;

public class PlatformHelperImpl {
    public static boolean canShear(ItemStack stack) {
        return stack.canPerformAction(ToolActions.SHEARS_CARVE);
    }

    public static boolean canStrip(ItemStack stack) {
        return stack.canPerformAction(ToolActions.AXE_STRIP);
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static boolean isModLoading(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }
}
