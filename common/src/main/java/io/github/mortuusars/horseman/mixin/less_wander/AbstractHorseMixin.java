package io.github.mortuusars.horseman.mixin.less_wander;

import io.github.mortuusars.horseman.world.LessWanderingHorse;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal implements LessWanderingHorse {
    @Unique
    @Nullable
    private Vec3 horseman$wanderAnchor = null;

    protected AbstractHorseMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public @Nullable Vec3 horseman$getWanderAnchor() {
        return horseman$wanderAnchor;
    }

    @Override
    public void horseman$setWanderAnchor(@Nullable Vec3 pos) {
        horseman$wanderAnchor = pos;
    }

    // --

    @Inject(method = "equipSaddle", at = @At("RETURN"))
    protected void equipSaddle(ItemStack stack, SoundSource soundSource, CallbackInfo ci) {
        if (LessWanderingHorse.isEnabled()) {
            horseman$wanderAnchor = this.position();
        }
    }

    // --

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    protected void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (horseman$wanderAnchor != null && LessWanderingHorse.isEnabled()) {
            tag.putDouble("HorsemanWanderAnchorX", horseman$wanderAnchor.x);
            tag.putDouble("HorsemanWanderAnchorY", horseman$wanderAnchor.y);
            tag.putDouble("HorsemanWanderAnchorZ", horseman$wanderAnchor.z);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    protected void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (!LessWanderingHorse.isEnabled()) return;

        @Nullable Double x = null;
        @Nullable Double y = null;
        @Nullable Double z = null;

        if (tag.contains("HorsemanWanderAnchorX", Tag.TAG_DOUBLE)) {
            x = tag.getDouble("HorsemanWanderAnchorX");
        }
        if (tag.contains("HorsemanWanderAnchorY", Tag.TAG_DOUBLE)) {
            y = tag.getDouble("HorsemanWanderAnchorY");
        }
        if (tag.contains("HorsemanWanderAnchorZ", Tag.TAG_DOUBLE)) {
            z = tag.getDouble("HorsemanWanderAnchorZ");
        }

        if (x != null && y != null && z != null) {
            horseman$wanderAnchor = new Vec3(x, y, z);
        }
    }
}
