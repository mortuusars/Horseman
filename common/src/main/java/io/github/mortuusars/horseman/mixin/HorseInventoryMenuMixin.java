package io.github.mortuusars.horseman.mixin;

import io.github.mortuusars.horseman.world.HitchableHorse;
import io.github.mortuusars.horseman.world.menu.LeadSlot;
import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HorseInventoryMenu.class)
public abstract class HorseInventoryMenuMixin extends AbstractContainerMenu {
    @Shadow @Final private Container horseContainer;
    @Shadow @Final private AbstractHorse horse;

    protected HorseInventoryMenuMixin(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(method = "<init>",
            at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/HorseInventoryMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;",
            ordinal = 1, shift = At.Shift.AFTER))
    private void onInit(int containerId, Inventory inventory, Container horseContainer, AbstractHorse horse, int columns, CallbackInfo ci) {
        if (!(horse instanceof HitchableHorse hitchableHorse)) return;
        if (!HitchableHorse.shouldHaveLeadSlot(hitchableHorse)) return;

        addSlot(new LeadSlot(hitchableHorse.horseman$getLeadAccess(), 0, 8, 54) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return HitchableHorse.mayPlaceInLeadSlot(hitchableHorse, stack);
            }

            @Override
            public boolean mayPickup(Player player) {
                ItemStack stack = this.getItem();
                return !stack.is(Items.LEAD) || !HitchableHorse.isHitched(hitchableHorse);
            }

            @Override
            public boolean isActive() {
                // Making it active on client to keep rendering the slot.
                return horse.level().isClientSide || HitchableHorse.isLeadSlotActive(hitchableHorse);
            }
        });
    }

    /**
     * When Lead stack in player inventory is shift-clicked - splits only one item from clicked stack and inserts into Lead slot.
     * On Forge it's mostly a QOL thing (it will work without it, but not stop moving the rest of a stack in other slots),
     * but on Fabric without this mixin it's possible to insert 64 items into a slot with stack size of 1.
     * Fabric does not check slot limits when inserting.
     */
    @Inject(method = "quickMoveStack", at = @At(value = "HEAD"), cancellable = true)
    private void onQuickMoveStack(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        if (!(this.horse instanceof HitchableHorse hitchableHorse)) return;
        if (!HitchableHorse.shouldHaveLeadSlot(hitchableHorse)) return;
        if (index < this.horseContainer.getContainerSize() + 1) return;

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
