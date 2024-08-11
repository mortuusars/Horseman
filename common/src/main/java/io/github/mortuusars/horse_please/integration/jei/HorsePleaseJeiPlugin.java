package io.github.mortuusars.horse_please.integration.jei;

import io.github.mortuusars.horse_please.HorsePlease;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class HorsePleaseJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = HorsePlease.resource("jei_plugin");

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