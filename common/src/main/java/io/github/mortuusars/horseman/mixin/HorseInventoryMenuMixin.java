package io.github.mortuusars.horseman.mixin;

import io.github.mortuusars.horseman.Hitching;
import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HorseInventoryMenu.class)
public abstract class HorseInventoryMenuMixin extends AbstractContainerMenu {
    @Shadow @Final private Container horseContainer;
    @Unique
    private boolean horseman$leadSlotAdded = false;

    protected HorseInventoryMenuMixin(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    /**
     * We cannot mixin with "INVOKE" into constructors on forge apparently, so adding Lead slot here is likely the only option.
     * It should come before chest slots for Shift+Clicking to work properly.
     * Unfortunately we cannot change (or at least not easily) indexes of chest slots,
     * so Lead slot index will be after chest slots (for inventory, it's still added before then in order)
     */
    @Inject(method = "hasChest", at = @At(value = "HEAD"))
    private void onHasChest(AbstractHorse horse, CallbackInfoReturnable<Boolean> cir) {
        if (!horseman$leadSlotAdded && Hitching.shouldHaveLeadSlot(horse)) {
            int leadSlotIndex = Hitching.getLeadSlotIndex(horse);
            addSlot(new Slot(this.horseContainer, leadSlotIndex, 8, 54) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return Hitching.mayPlaceInLeadSlot(horse, stack);
                }

                @Override
                public boolean mayPickup(Player player) {
                    ItemStack stack = this.getItem();
                    return !stack.is(Items.LEAD) || stack.getTag() == null || !stack.getTag().getBoolean(Hitching.PREVENT_LEAD_DROP_TAG);
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean isActive() {
                    return Hitching.isLeadSlotActive(horse);
                }
            });
            horseman$leadSlotAdded = true;
        }
    }
}
