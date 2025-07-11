package io.github.mortuusars.horseman.integration.jei;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.integration.jei.recipe.ComponentTransferringShapelessExtension;
import io.github.mortuusars.horseman.integration.jei.subtypes.InstrumentSubtypeInterpreter;
import io.github.mortuusars.horseman.world.item.crafting.recipe.ComponentTransferringRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class HorsemanJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = Horseman.resource("jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        // Adds all horns with all instruments to JEI. Otherwise, only one will show up.
        registration.registerSubtypeInterpreter(Horseman.Items.COPPER_HORN.get(), InstrumentSubtypeInterpreter.INSTANCE);
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        if (Config.Client.SHOW_JEI_INFORMATION.get()) {
            registration.addItemStackInfo(List.of(new ItemStack(Horseman.Items.COPPER_HORN.get())),
                    Component.translatable("horseman.jei.info.copper_horn1"),
                    Component.translatable("horseman.jei.info.copper_horn2"),
                    Component.translatable("horseman.jei.info.copper_horn3"));
        }
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(ComponentTransferringRecipe.class, new ComponentTransferringShapelessExtension());
    }
}