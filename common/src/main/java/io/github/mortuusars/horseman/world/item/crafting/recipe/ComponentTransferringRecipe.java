package io.github.mortuusars.horseman.world.item.crafting.recipe;

import io.github.mortuusars.horseman.Horseman;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ComponentTransferringRecipe extends CustomRecipe {
    private final Ingredient sourceIngredient;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack result;

    public ComponentTransferringRecipe(CraftingBookCategory category, Ingredient sourceIngredient, NonNullList<Ingredient> ingredients, ItemStack result) {
        super(category);
        this.sourceIngredient = sourceIngredient;
        this.ingredients = ingredients;
        this.result = result;
    }

    @Override
    public @NotNull RecipeSerializer<ComponentTransferringRecipe> getSerializer() {
        return Horseman.RecipeSerializers.COMPONENT_TRANSFERRING.get();
    }

    public @NotNull Ingredient getSourceIngredient() {
        return sourceIngredient;
    }

    public @NotNull NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public @NotNull ItemStack getResultItem(HolderLookup.Provider registries) {
        return getResult();
    }

    public @NotNull ItemStack getResult() {
        return result;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (getSourceIngredient().isEmpty() || ingredients.isEmpty())
            return false;

        List<Ingredient> unmatchedIngredients = new ArrayList<>(ingredients);
        unmatchedIngredients.addFirst(getSourceIngredient());

        int itemsInCraftingGrid = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty())
                itemsInCraftingGrid++;

            if (itemsInCraftingGrid > ingredients.size() + 1)
                return false;

            if (!unmatchedIngredients.isEmpty()) {
                for (int j = 0; j < unmatchedIngredients.size(); j++) {
                    if (unmatchedIngredients.get(j).test(stack)) {
                        unmatchedIngredients.remove(j);
                        break;
                    }
                }
            }
        }

        return unmatchedIngredients.isEmpty() && itemsInCraftingGrid == ingredients.size() + 1;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        for (int index = 0; index < input.size(); index++) {
            ItemStack itemStack = input.getItem(index);

            if (getSourceIngredient().test(itemStack)) {
                return itemStack.transmuteCopy(getResultItem(registries).getItem());
            }
        }

        return getResultItem(registries);
    }

    @Override
    public @NotNull List<RecipeDisplay> display() {
        ArrayList<SlotDisplay> list = new ArrayList<>(ingredients.stream().map(Ingredient::display).toList());
        list.addFirst(sourceIngredient.display());
        return List.of(new ShapelessCraftingRecipeDisplay(list, new SlotDisplay.ItemSlotDisplay(this.result.getItem()), new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
    }
}
