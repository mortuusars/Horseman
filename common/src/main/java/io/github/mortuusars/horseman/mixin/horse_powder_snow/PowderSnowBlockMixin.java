package io.github.mortuusars.horseman.mixin.horse_powder_snow;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.mortuusars.horseman.Config;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PowderSnowBlock.class)
public abstract class PowderSnowBlockMixin {
    @ModifyReturnValue(method = "canEntityWalkOnPowderSnow", at = @At("RETURN"))
    private static boolean canEntityWalkOnPowderSnow(boolean original, @Local(argsOnly = true) Entity entity) {
        return original ||
                (Config.Server.LEATHER_HORSE_ARMOR_POWDER_SNOW.get()
                        && entity instanceof AbstractHorse horse
                        && horse.getBodyArmorItem().is(Items.LEATHER_HORSE_ARMOR));
    }
}
