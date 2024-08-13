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
    @Shadow @Final private AbstractHorse horse;
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

    /**
     * When Lead stack in player inventory is shift-clicked - splits only one item from clicked stack and inserts into Lead slot.
     * On Forge it's mostly a QOL thing (it will work without it, but not stop moving the rest of a stack in other slots),
     * but on Fabric without this mixin it's possible to insert 64 items into a slot with stack size of 1.
     * Fabric does not check slot limits when inserting.
     */
    @Inject(method = "quickMoveStack", at = @At(value = "HEAD"), cancellable = true)
    private void onQuickMoveStack(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        if (!Hitching.shouldHaveLeadSlot(this.horse) || index < this.horseContainer.getContainerSize()) {
            return;
        }

        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return;
        }

        ItemStack clickedStack = slot.getItem();
        if (!clickedStack.is(Items.LEAD)) {
            return;
        }

        ItemStack clickedStackCopy = clickedStack.copy();

        Slot leadSlot = getSlot(2);
        if (!leadSlot.mayPlace(clickedStack)) {
            return;
        }

        ItemStack movedStack = clickedStack.copyWithCount(1);

        if (!leadSlot.getItem().isEmpty()) {
            if (!moveItemStackTo(clickedStack, 3, this.horseContainer.getContainerSize(), false)) {
                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }
            else {
                if (clickedStack.isEmpty()) {
                    slot.setByPlayer(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }

                if (clickedStack.getCount() == clickedStackCopy.getCount()) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }

                slot.onTake(player, clickedStack);

                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }
        }

        clickedStack.shrink(1);
        this.slots.get(2).setByPlayer(movedStack);

        if (clickedStack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (clickedStack.getCount() == clickedStackCopy.getCount()) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        slot.onTake(player, clickedStack);

        cir.setReturnValue(ItemStack.EMPTY);
    }
}
