package io.github.mortuusars.horseman.mixin.step_height;

import io.github.mortuusars.horseman.Config;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

// Increases step-height for horses. Allows riding up a path to a full block (With default setting. Can add more if desired).
@Mixin(value = AbstractHorse.class, priority = 950)
public class AbstractHorseMixin {
	@ModifyConstant(method = "<init>", constant = @Constant(floatValue = 1.0f))
	private float horseHigherStepHeight(float value) {
		return 1.0f + Config.Common.HORSE_STEP_HEIGHT_MODIFIER.get().floatValue();
	}
}