package io.github.mortuusars.horseman.mixin;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.world.HitchableHorse;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.ticks.ContainerSingleItem;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal implements HitchableHorse {
    @Unique
    protected final Container horseman$leadAccess = horseman$createLeadAccess(this.horseman$asHorse());
    @Unique
    protected ItemStack horseman$leadItem = ItemStack.EMPTY;
    @Unique
    protected boolean horseman$isHitched = false;

    protected AbstractHorseMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public ItemStack horseman$getLead() {
        return horseman$leadItem;
    }

    @Override
    public void horseman$setLead(ItemStack stack) {
        horseman$leadItem = stack;
    }

    @Override
    public boolean horseman$isHitched() {
        return isLeashed() && horseman$isHitched;
    }

    @Override
    public void horseman$setHitched(boolean hitched) {
        horseman$isHitched = hitched;
    }

    @Override
    public Container horseman$getLeadAccess() {
        return horseman$leadAccess;
    }

    // --

    // Drops the Lead if it's not in inventory (slot is disabled)
    @Inject(method = "dropEquipment", at = @At(value = "RETURN"))
    private void onDropEquipment(CallbackInfo ci) {
        if (HitchableHorse.hasLead(this)) {
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
            tag.put("HorsemanLeadItem", leadStack.save(registryAccess()));
        }

        if (horseman$isHitched()) {
            tag.putBoolean("HorsemanHitched", true);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    protected void onReadAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("HorsemanLeadItem", Tag.TAG_COMPOUND)) {
            ItemStack leadStack = ItemStack.parse(registryAccess(), tag.getCompound("HorsemanLeadItem")).orElse(ItemStack.EMPTY);
            horseman$setLead(leadStack);
        }

        horseman$isHitched = tag.getBoolean("HorsemanHitched");
    }

    // --

    @Unique
    private static Container horseman$createLeadAccess(AbstractHorse horse) {
        return new ContainerSingleItem() {
            private final AbstractHorse ownerHorse = horse;

            @Override
            public @NotNull ItemStack getTheItem() {
                return ((HitchableHorse) horse).horseman$getLead();
            }

            @Override
            public void setTheItem(ItemStack item) {
                ((HitchableHorse) horse).horseman$setLead(item);
            }

            @Override
            public void setChanged() {
            }

            @Override
            public boolean stillValid(Player player) {
                return player.getVehicle() == ownerHorse || player.canInteractWithEntity(ownerHorse, 4.0);
            }
        };
    }
}
