package io.github.mortuusars.horseman.integration.jei.subtypes;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InstrumentSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
	public static final InstrumentSubtypeInterpreter INSTANCE = new InstrumentSubtypeInterpreter();

	private InstrumentSubtypeInterpreter() { }

	@Override
	public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
		if (context == UidContext.Recipe) return null; // Show all in recipe lookup
		return ingredient.get(DataComponents.INSTRUMENT);
	}
}
