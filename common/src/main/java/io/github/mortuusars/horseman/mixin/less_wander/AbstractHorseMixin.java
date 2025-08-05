package io.github.mortuusars.horseman.mixin.less_wander;

import io.github.mortuusars.horseman.world.LessWanderingHorse;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    protected void addAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
        if (horseman$wanderAnchor != null && LessWanderingHorse.isEnabled()) {
            output.putDouble("HorsemanWanderAnchorX", horseman$wanderAnchor.x);
            output.putDouble("HorsemanWanderAnchorY", horseman$wanderAnchor.y);
            output.putDouble("HorsemanWanderAnchorZ", horseman$wanderAnchor.z);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    protected void readAdditionalSaveData(ValueInput input, CallbackInfo ci) {
        if (!LessWanderingHorse.isEnabled()) return;

        double x = input.getDoubleOr("HorsemanWanderAnchorX", 0.0);
        double y = input.getDoubleOr("HorsemanWanderAnchorY", 0.0);
        double z = input.getDoubleOr("HorsemanWanderAnchorZ", 0.0);

        if (x != 0.0 && y != 0.0 && z != 0.0) {
            horseman$wanderAnchor = new Vec3(x, y, z);
        }
    }
}
