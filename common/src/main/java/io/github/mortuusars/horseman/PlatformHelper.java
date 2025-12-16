package io.github.mortuusars.horseman;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.ItemStack;

public class PlatformHelper {
    @ExpectPlatform
    public static boolean canShear(ItemStack stack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isModLoading(String modId) {
        throw new AssertionError();
    }
}
