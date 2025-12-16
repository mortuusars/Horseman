package io.github.mortuusars.horseman.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;

public class PlatformHelperImpl {
    public static boolean canShear(ItemStack stack) {
        return stack.getItem() instanceof ShearsItem;
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    /**
     * This method is here because on forge checking if mod is loaded at mixin apply time is different (LoadingModList vs ModList)
     * But on fabric we can use the same code.
     */
    public static boolean isModLoading(String modId) {
        return isModLoaded(modId);
    }
}
