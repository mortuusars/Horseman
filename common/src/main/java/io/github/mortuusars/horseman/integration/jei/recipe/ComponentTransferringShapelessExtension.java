package io.github.mortuusars.horseman.integration.jei.recipe;

import io.github.mortuusars.horseman.world.item.crafting.recipe.ComponentTransferringRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class ComponentTransferringShapelessExtension /*implements ICraftingCategoryExtension<ComponentTransferringRecipe>*/ {
//    @Override
//    public @NotNull List<SlotDisplay> getIngredients(RecipeHolder<ComponentTransferringRecipe> recipeHolder) {
//        recipeHolder.value().
//        return List.of();
//    }


//    @Override
//    public void setRecipe(RecipeHolder<ComponentTransferringRecipe> recipeHolder, IRecipeLayoutBuilder builder,
//                          ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
//        ComponentTransferringRecipe recipe = recipeHolder.value();
//
//        List<List<ItemStack>> inputs = recipe.getIngredients().stream()
//                .map(ingredient -> List.of(ingredient.getItems()))
//                .collect(Collectors.toList());
//
//        inputs.addFirst(List.of(recipe.getSourceIngredient().getItems()));
//
//        ItemStack resultItem = recipe.getResult();
//
//        int width = getWidth(recipeHolder);
//        int height = getHeight(recipeHolder);
//        craftingGridHelper.createAndSetInputs(builder, inputs, width, height);
//        craftingGridHelper.createAndSetOutputs(builder, List.of(resultItem));
//    }
}