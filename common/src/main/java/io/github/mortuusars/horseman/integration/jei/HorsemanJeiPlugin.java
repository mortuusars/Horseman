package io.github.mortuusars.horseman.integration.jei;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class HorsemanJeiPlugin implements IModPlugin {
    private static final Identifier ID = Horseman.resource("jei_plugin");

    @Override
    public @NotNull Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        if (Config.Client.SHOW_JEI_INFORMATION.get()) {
            registration.addItemStackInfo(List.of(new ItemStack(Horseman.Items.COPPER_HORN.get())),
                    Component.translatable("horseman.jei.info.copper_horn1"),
                    Component.translatable("horseman.jei.info.copper_horn2"));
        }
    }
}