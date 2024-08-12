package io.github.mortuusars.horseman.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.mortuusars.horseman.Hitching;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractChestedHorse.class)
public abstract class AbstractChestedHorseMixin extends AbstractHorse {
    @Shadow public abstract boolean hasChest();

    protected AbstractChestedHorseMixin(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * +1 for Lead slot.
     * {@link AbstractHorseMixin} also has this change, so we should only add +1 if it's not calling the super method.
     */
    @ModifyReturnValue(method = "getInventorySize", at = @At("RETURN"))
    private int onGetInventorySize(int original) {
        return Hitching.shouldHaveLeadSlot(this) && hasChest() ? original + 1 : original;
    }
}
