package io.github.mortuusars.horseman.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.data.HitchableHorse;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal implements HitchableHorse {
    @Shadow protected abstract int getInventorySize();
    @Shadow public SimpleContainer inventory;

    @Unique
    protected boolean horseman$isHitched = false;
    @Unique
    protected ItemStack horseman$builtInLead = ItemStack.EMPTY;

    protected AbstractHorseMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public ItemStack horseman$getLead() {
        if (!HitchableHorse.isEnabled()) {
            return ItemStack.EMPTY;
        }

        if (HitchableHorse.shouldHaveLeadSlot(this)) {
            return inventory.getItem(HitchableHorse.getLeadSlotIndex(this));
        } else {
            return horseman$builtInLead;
        }
    }

    @Override
    public void horseman$setLead(ItemStack stack) {
        if (HitchableHorse.shouldHaveLeadSlot(this)) {
            inventory.setItem(HitchableHorse.getLeadSlotIndex(this), stack);
        } else {
            horseman$builtInLead = stack;
        }
    }

    @Override
    public boolean horseman$isHitched() {
        return isLeashed() && horseman$isHitched;
    }

    @Override
    public void horseman$setHitched(boolean hitched) {
        horseman$isHitched = hitched;
    }

    // --

    /**
     * Add +1 for the lead slot. {@link AbstractChestedHorseMixin} should also be changed as it overrides this method.
     */
    @ModifyReturnValue(method = "getInventorySize", at = @At("RETURN"))
    private int onGetInventorySize(int original) {
        return HitchableHorse.shouldHaveLeadSlot(this) ? original + 1 : original;
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
        if (horse instanceof AbstractChestedHorse chestedHorse
                && chestedHorse.hasChest()
                && getInventorySize() > inventory.getContainerSize()
                && HitchableHorse.shouldHaveLeadSlot(this)) {
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

    // Drops the Lead if it's not in inventory (slot is disabled)
    @Inject(method = "dropEquipment", at = @At(value = "RETURN"))
    private void onDropEquipment(CallbackInfo ci) {
        if (HitchableHorse.isEnabled() && HitchableHorse.requiresLead()
                && !HitchableHorse.shouldHaveLeadSlot(this)
                && HitchableHorse.hasLead(this)) {
            spawnAtLocation(HitchableHorse.getLead(this));
            HitchableHorse.setLead(this, ItemStack.EMPTY);
        }
    }

    // --

    @Inject(method = "doPlayerRide", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setYRot(F)V"), cancellable = true)
    private void onDoPlayerRide(Player player, CallbackInfo ci) {
        if (Config.Common.ROTATE_HORSE_INSTEAD_OF_PLAYER.get()) {
            this.setYRot(player.getYRot());
            this.setXRot(player.getXRot());
            player.startRiding(this);
            ci.cancel();
        }
    }

    @Inject(method = "getRiddenRotation", at = @At(value = "HEAD"), cancellable = true)
    private void onGetRiddenRotation(LivingEntity entity, CallbackInfoReturnable<Vec2> cir) {
        AbstractHorse horse = (AbstractHorse)(Object)this;
        if (!Config.Common.HORSE_FREE_CAMERA.get() || !(entity instanceof Player player) || player.xxa != 0 || player.zza != 0) {
            return;
        }

        float threshold = Config.Common.HORSE_FREE_CAMERA_ANGLE_THRESHOLD.get().floatValue();

        float rotationDifference = (player.getYRot() - horse.getYRot() + 540) % 360 - 180;

        if (Math.abs(rotationDifference) > threshold) {
            // Rotate the horse following player's rotation, with offset
            cir.setReturnValue(new Vec2(player.getXRot() * 0.5f, player.getYRot() - Math.signum(rotationDifference) * threshold));
        }
        else {
            cir.setReturnValue(new Vec2(player.getXRot() * 0.5f, horse.getYRot()));
        }
    }

    // --

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    protected void onAddAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        ItemStack leadStack = horseman$getLead();
        if (!leadStack.isEmpty()) {
            tag.put("HorsemanLeadItem", leadStack.save(new CompoundTag()));
        }

        if (horseman$isHitched()) {
            tag.putBoolean("HorsemanHitched", true);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    protected void onReadAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("HorsemanLeadItem", Tag.TAG_COMPOUND)) {
            ItemStack leadStack = ItemStack.of(tag.getCompound("HorsemanLeadItem"));

            // Backwards compat with <=1.1.4
            if (leadStack.getTag() != null && leadStack.getTag().contains("PreventLeadDrop")) {
                HitchableHorse.setHitched(this, true);
                leadStack.getTag().remove("PreventLeadDrop");
                if (leadStack.getTag().isEmpty()) {
                    leadStack.setTag(null);
                }
            }

            horseman$setLead(leadStack);
        }

        horseman$isHitched = tag.getBoolean("HorsemanHitched");
    }
}
