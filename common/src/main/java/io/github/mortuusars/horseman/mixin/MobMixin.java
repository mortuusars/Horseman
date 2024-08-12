package io.github.mortuusars.horseman.mixin;

import io.github.mortuusars.horseman.Hitching;
import io.github.mortuusars.horseman.data.IPersistentDataHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyVariable(method = "dropLeash", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private boolean shouldDropLeash(boolean value) {
        if (((Object)this) instanceof AbstractHorse horse) {
            return Hitching.shouldDropLeash(horse);
        }

        return value;
    }

    @Inject(method = "dropLeash", at = @At("RETURN"))
    private void onDropLeash(boolean broadcastPacket, boolean dropLeash, CallbackInfo ci) {
        if (((Object)this) instanceof AbstractHorse horse) {
            Hitching.setDropsLeash(horse, true);
        }
    }
}
