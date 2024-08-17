package io.github.mortuusars.horseman.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Hitching;
import io.github.mortuusars.horseman.PlatformHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractChestedHorse.class)
public abstract class AbstractChestedHorseMixin extends AbstractHorse {
    @Shadow public abstract boolean hasChest();

    @Shadow public abstract int getInventoryColumns();

    @Shadow public abstract void setChest(boolean chested);

    protected AbstractChestedHorseMixin(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * +1 for Lead slot.
     * {@link AbstractHorseMixin} also has this change, so we should only add +1 if it's not calling the super method.
     */
    @ModifyReturnValue(method = "getInventorySize", at = @At("RETURN"))
    private int onGetInventorySize(int original) {
        return Hitching.shouldHaveLeadSlot(this) && hasChest() ? original + 1 : original;
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!Config.Common.HORSE_SHEARS_REMOVE_CHEST.get() || isVehicle() || isBaby()
                || !isTamed() || !hasChest() || player.isSecondaryUseActive()) {
            return;
        }

        ItemStack itemInHand = player.getItemInHand(hand);
        if (PlatformHelper.canShear(itemInHand)) {
            if (level().isClientSide) {
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }

            int chestInventorySize = 3 * getInventoryColumns();
            SimpleContainer dropContainer = new SimpleContainer(chestInventorySize);

            for (int i = 0; i < dropContainer.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(2 + i);
                dropContainer.setItem(i, stack);
            }

            Containers.dropContents(level(), this, dropContainer);
            Containers.dropItemStack(level(), this.getX(), this.getY(), this.getZ(), new ItemStack(Items.CHEST));

            // Move lead to a correct slot (always last). Same as in AbstractHorseMixin#createInventory but in reverse.
            if (Hitching.shouldHaveLeadSlot(this)) {
                int leadSlot = Hitching.getLeadSlotIndex(this);
                ItemStack leadStack = inventory.getItem(leadSlot);
                if (!leadStack.isEmpty()) {
                    inventory.setItem(2, leadStack);
                    inventory.setItem(leadSlot, ItemStack.EMPTY);
                }
            }

            playSound(SoundEvents.SHEEP_SHEAR, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            setChest(false);
            createInventory();

            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
