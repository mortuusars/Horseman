package io.github.mortuusars.horseman.integration.jei.recipe;

import io.github.mortuusars.horseman.world.item.crafting.recipe.ComponentTransferringRecipe;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ComponentTransferringCategoryExtension implements ICraftingCategoryExtension<CraftingRecipe> {
	@Override
	public boolean isHandled(RecipeHolder<CraftingRecipe> recipeHolder) {
		return recipeHolder.value() instanceof ComponentTransferringRecipe;
	}

	@Override
	public @NotNull List<SlotDisplay> getIngredients(RecipeHolder<CraftingRecipe> recipeHolder) {
		List<RecipeDisplay> displays = recipeHolder.value().display();
		if (displays.isEmpty()) {
			return List.of();
		}
		RecipeDisplay display = displays.getFirst();
		if (display instanceof ShapedCraftingRecipeDisplay shapedCraftingRecipeDisplay) {
			return shapedCraftingRecipeDisplay.ingredients();
		} else if (display instanceof ShapelessCraftingRecipeDisplay shapelessCraftingRecipeDisplay) {
			return shapelessCraftingRecipeDisplay.ingredients();
		} else {
			return List.of();
		}
	}
}
