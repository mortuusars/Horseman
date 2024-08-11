package io.github.mortuusars.horseman.integration.jei;

import io.github.mortuusars.horseman.Horseman;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class HorsemanJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = Horseman.resource("jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

//    @Override
//    public void registerCategories(IRecipeCategoryRegistration registration) {
//    }
//
//    @Override
//    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
//    }

//    @Override
//    public void registerRecipes(@NotNull IRecipeRegistration registration) {
//
//    }

//    @Override
//    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
//
//    }

//    @Override
//    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
//
//    }
}