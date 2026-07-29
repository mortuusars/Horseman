package io.github.mortuusars.horseman.mixin.hitching;

import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.world.HitchableHorse;
import io.github.mortuusars.horseman.world.menu.LeadSlot;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMountInventoryMenu.class)
public abstract class AbstractMountInventoryMenuMixin extends AbstractContainerMenu {
    @Shadow @Final protected LivingEntity mount;

    @Shadow @Final protected Container mountContainer;

    protected AbstractMountInventoryMenuMixin(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    /**
     * When Lead stack in player inventory is shift-clicked - splits only one item from clicked stack and inserts into Lead slot.
     * On Forge it's mostly a QOL thing (it will work without it, but not stop moving the rest of a stack in other slots),
     * but on Fabric without this mixin it's possible to insert 64 items into a slot with stack size of 1.
     * Fabric does not check slot limits when inserting.
     */
    @Inject(method = "quickMoveStack", at = @At(value = "HEAD"), cancellable = true)
    private void onQuickMoveStack(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
        if (!(this.mount instanceof HitchableHorse hitchableHorse)) return;
        if (!HitchableHorse.shouldHaveLeadSlot(hitchableHorse)) return;
        if (slotIndex < this.mountContainer.getContainerSize() + 1) return;

        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) {
            return;
        }

        if (slot instanceof LeadSlot) {
            return;
        }

        ItemStack clickedStack = slot.getItem();
        if (!clickedStack.is(Items.LEAD)) {
            return;
        }

        ItemStack clickedStackCopy = clickedStack.copy();

        LeadSlot leadSlot = null;
        for (Slot s : slots) {
            if (s instanceof LeadSlot ls) {
                leadSlot = ls;
            }
        }
        if (leadSlot == null) {
            Horseman.LOGGER.error("LeadSlot is not found. Something went wrong. Try without other mods and report to github.");
            return;
        }

        if (!leadSlot.mayPlace(clickedStack)) {
            return;
        }

        ItemStack movedStack = clickedStack.copyWithCount(1);

        if (!leadSlot.getItem().isEmpty()) {
            if (!moveItemStackTo(clickedStack, 3, this.mountContainer.getContainerSize(), false)) {
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
        leadSlot.setByPlayer(movedStack);

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
