package io.github.mortuusars.horseman.fabric;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class PlatformHelperImpl {
    public static boolean canShear(ItemStack stack) {
        return stack.getItem() instanceof ShearsItem;
    }

    public static boolean canStrip(ItemStack stack) {
        return stack.getItem() instanceof AxeItem;
    }

    public static void openMenu(ServerPlayer serverPlayer, MenuProvider menuProvider, Consumer<FriendlyByteBuf> extraDataWriter) {
        LoadingWeirdness.openMenu(serverPlayer, menuProvider, extraDataWriter);
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    /**
     * This method is here because on forge checking if mod is loaded at mixin apply time is different (LoadingModList vs ModList)
     * But on fabric we can use the same code.
     */
    public static boolean isModLoading(String modId) {
        return isModLoaded(modId);
    }

    /**
     * This is separated to not crash on game loading, possibly because class getting initialized fully and screen factory is loaded too early.
     * I may be stupid for not knowing this ahead of time, but this is f*ing bonkers.
     * At least it's not taken whole day of head bashing to figure it out.
     */
    public static class LoadingWeirdness {
        public static void openMenu(ServerPlayer serverPlayer, MenuProvider menuProvider, Consumer<FriendlyByteBuf> extraDataWriter) {
            ExtendedScreenHandlerFactory extendedScreenHandlerFactory = new ExtendedScreenHandlerFactory() {
                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
                    return menuProvider.createMenu(i, inventory, player);
                }

                @Override
                public @NotNull Component getDisplayName() {
                    return menuProvider.getDisplayName();
                }

                @Override
                public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buffer) {
                    extraDataWriter.accept(buffer);
                }
            };

            serverPlayer.openMenu(extendedScreenHandlerFactory);
        }
    }
}
