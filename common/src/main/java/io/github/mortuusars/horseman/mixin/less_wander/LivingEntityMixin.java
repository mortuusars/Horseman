package io.github.mortuusars.horseman.mixin.less_wander;

import io.github.mortuusars.horseman.world.LessWanderingHorse;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "onEquipItem", at = @At("RETURN"))
    private void onEquipItem(EquipmentSlot slot, ItemStack oldItem, ItemStack newItem, CallbackInfo ci) {
        if (LessWanderingHorse.isEnabled() && this instanceof LessWanderingHorse lessWanderingHorse && slot == EquipmentSlot.SADDLE && !newItem.isEmpty()) {
            lessWanderingHorse.horseman$setWanderAnchor(position());
        }
    }

    @Inject(method = "stopRiding", at = @At("HEAD"))
    private void onStopRiding(CallbackInfo ci) {
        if (LessWanderingHorse.isEnabled() && getVehicle() instanceof LessWanderingHorse horse) {
            horse.horseman$setWanderAnchor(getVehicle().position());
        }
    }
}
