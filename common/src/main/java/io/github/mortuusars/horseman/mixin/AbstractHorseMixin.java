package io.github.mortuusars.horseman.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Hitching;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal {
    @Shadow protected abstract int getInventorySize();
    @Shadow public SimpleContainer inventory;

    protected AbstractHorseMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "doPlayerRide", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setYRot(F)V"), cancellable = true)
    private void onDoPlayerRide(Player player, CallbackInfo ci) {
        if (Config.Common.ROTATE_HORSE_INSTEAD_OF_PLAYER.get()) {
            this.setYRot(player.getYRot());
            this.setXRot(player.getXRot());
            player.startRiding(this);
            ci.cancel();
        }
    }

    /**
     * Add +1 for the lead slot. {@link AbstractChestedHorseMixin} should also be changed as it overrides this method.
     */
    @ModifyReturnValue(method = "getInventorySize", at = @At("RETURN"))
    private int onGetInventorySize(int original) {
        AbstractHorse horse = (AbstractHorse)(Object)this;
        return Hitching.shouldHaveLeadSlot(horse) ? original + 1 : original;
    }

    /**
     * All hitching stuff is hacky, but this is beyond stupid and just waiting to brake something.
     * This is done to move the Lead to a correct slot after chest has been put on a horse.
     * Lead slot is always the last one, and when placing a chest it will change from 2 to 17.
     * And if we don't move the item, it will appear in first chest slot.
     */
    @Inject(method = "createInventory", at = @At(value = "HEAD"))
    private void onCreateInventory(CallbackInfo ci) {
        AbstractHorse horse = (AbstractHorse)(Object)this;
        if (horse instanceof AbstractChestedHorse chestedHorse && chestedHorse.hasChest()
                && getInventorySize() > inventory.getContainerSize() && Hitching.shouldHaveLeadSlot(horse)) {
            @Nullable SimpleContainer prevInventory = inventory;
            inventory = new SimpleContainer(getInventorySize());
            if (prevInventory != null) {
                prevInventory.removeListener(horse);
                int slots = Math.min(prevInventory.getContainerSize(), this.inventory.getContainerSize());
                for (int i = 0; i < slots; ++i) {
                    ItemStack itemStack = prevInventory.getItem(i);
                    if (itemStack.isEmpty()) continue;
                    this.inventory.setItem(i, itemStack.copy());
                }

                // Swap lead that's now in the wrong slot to last inventory slot.
                if (inventory.getItem(2).is(Items.LEAD)) {
                    ItemStack lastItem = inventory.getItem(inventory.getContainerSize() - 1);
                    inventory.setItem(inventory.getContainerSize() - 1, inventory.getItem(2));
                    inventory.setItem(2, lastItem);
                }
            }
        }
    }
}
