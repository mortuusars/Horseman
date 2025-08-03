package io.github.mortuusars.horseman.mixin.hitching;

import io.github.mortuusars.horseman.world.HitchableHorse;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.ticks.ContainerSingleItem;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    private void onDropEquipment(ServerLevel level, CallbackInfo ci) {
        if (HitchableHorse.hasLead(this)) {
            spawnAtLocation(level, HitchableHorse.getLead(this));
            HitchableHorse.setLead(this, ItemStack.EMPTY);
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
        horseman$leadItem = tag.getCompound("HorsemanLeadItem")
                .map(compound -> ItemStack.parse(registryAccess(), compound)
                        .orElse(ItemStack.EMPTY))
                .orElse(ItemStack.EMPTY);
        horseman$isHitched = tag.getBooleanOr("HorsemanHitched", false);
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
