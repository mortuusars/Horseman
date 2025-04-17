package io.github.mortuusars.horseman.fabric.mixin.break_speed;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.mortuusars.horseman.Config;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Disable break speed debuff for not being grounded (while mounted)
@Mixin(value = Player.class, priority = 950)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyReturnValue(method = "getDestroySpeed", at = @At("RETURN"))
    private float getDestroySpeed(float original) {
        if (!this.onGround() && this.getRootVehicle() instanceof AbstractHorse && Config.Common.MOUNTED_BLOCK_BREAK_SPEED_MODIFIER.get() != 0)
            return original * Config.Common.MOUNTED_BLOCK_BREAK_SPEED_MODIFIER.get().floatValue();
        return original;
    }
}