package io.github.mortuusars.horseman.mixin;

import io.github.mortuusars.horseman.data.IPersistentDataHolder;
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
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    protected void onReadAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("Horseman", Tag.TAG_COMPOUND)) {
            horseman$persistentData = tag.getCompound("Horseman");
        }
    }
}
