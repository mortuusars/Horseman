package io.github.mortuusars.horse_please.mixin;

import io.github.mortuusars.horse_please.HorsePlease;
import io.github.mortuusars.horse_please.data.IPersistentDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LivingEntity.class)
public abstract class PersistentDataHolderLivingEntityMixin implements IPersistentDataHolder {
    @Unique
    @Nullable
    private CompoundTag horse_Please$persistentData;

    @Override
    public @NotNull CompoundTag horse_Please$getPersistentData() {
        if (horse_Please$persistentData == null) {
            horse_Please$persistentData = new CompoundTag();
        }

        return horse_Please$persistentData;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    protected void onAddAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (horse_Please$persistentData != null) {
            tag.put("Horseman", horse_Please$persistentData);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    protected void onReadAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("Horseman", Tag.TAG_COMPOUND)) {
            horse_Please$persistentData = tag.getCompound("Horseman");
        }
    }
}
