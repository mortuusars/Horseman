package io.github.mortuusars.horseman.mixin.hitching;

import io.github.mortuusars.horseman.world.HitchableHorse;
import io.github.mortuusars.horseman.world.menu.LeadSlot;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseInventoryMenu.class)
public abstract class HorseInventoryMenuMixin extends AbstractMountInventoryMenu {
    protected HorseInventoryMenuMixin(int id, Inventory inventory, Container container, LivingEntity mount) {
        super(id, inventory, container, mount);
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
            public boolean mayPlace(@NotNull ItemStack stack) {
                return HitchableHorse.mayPlaceInLeadSlot(hitchableHorse, stack);
            }

            @Override
            public boolean mayPickup(@NotNull Player player) {
                ItemStack stack = this.getItem();
                return !stack.is(Items.LEAD) || !HitchableHorse.isHitched(hitchableHorse);
            }

            @Override
            public boolean isActive() {
                // Making it active on client to keep rendering the slot.
                return horse.level().isClientSide() || HitchableHorse.isLeadSlotActive(hitchableHorse);
            }
        });
    }
}
