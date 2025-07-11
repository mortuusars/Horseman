package io.github.mortuusars.horseman;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class HorsemanClient {
    public static void init() {
        ItemProperties.register(Horseman.Items.COPPER_HORN.get(), ResourceLocation.withDefaultNamespace("tooting"),
                (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
    }
}
