package io.github.mortuusars.horseman.mixin;

import io.github.mortuusars.horseman.Hitching;
import io.github.mortuusars.horseman.data.IPersistentDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LivingEntity.class)
public abstract class PersistentDataLivingEntityMixin implements IPersistentDataHolder {
    @Unique
    @Nullable
    private CompoundTag horseman$persistentData;

    @Override
    public @NotNull CompoundTag horseman$getPersistentData() {
        if (horseman$persistentData == null) {
            horseman$persistentData = new CompoundTag();
        }

        return horseman$persistentData;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    protected void onAddAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (horseman$persistentData != null) {
            tag.put("Horseman", horseman$persistentData);
        }

        //noinspection ConstantValue
        if (((Object) this) instanceof AbstractHorse horse && Hitching.shouldHaveLeadSlot(horse)) {
            ItemStack leadStack = horse.inventory.getItem(Hitching.getLeadSlotIndex(horse));
            if (!leadStack.isEmpty()) {
                tag.put("HorsemanLeadItem", leadStack.save(new CompoundTag()));
            }
        }

    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    protected void onReadAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("Horseman", Tag.TAG_COMPOUND)) {
            horseman$persistentData = tag.getCompound("Horseman");
        }

        //noinspection ConstantValue
        if (((Object) this) instanceof AbstractHorse horse && Hitching.shouldHaveLeadSlot(horse)
                && tag.contains("HorsemanLeadItem", Tag.TAG_COMPOUND)) {
            ItemStack itemStack = ItemStack.of(tag.getCompound("HorsemanLeadItem"));
            if (itemStack.is(Items.LEAD)) {
                horse.inventory.setItem(Hitching.getLeadSlotIndex(horse), itemStack);
            }
        }
    }
}
