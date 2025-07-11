package io.github.mortuusars.horseman.mixin.hitching;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.github.mortuusars.horseman.world.HitchableHorse;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Leashable.class)
public interface LeashableMixin {
    @Inject(method = "dropLeash(Lnet/minecraft/world/entity/Entity;ZZ)V", at = @At("HEAD"))
    private static <E extends Entity & Leashable> void onDropLeash(E entity, boolean broadcastPacket, boolean dropItem,
                                                       CallbackInfo ci, @Share("preventDrop") LocalBooleanRef preventDrop) {
        if (!(entity instanceof HitchableHorse horse)) return;

        boolean hitched = HitchableHorse.isHitched(horse);

        HitchableHorse.setHitched(horse, false);
        if (!entity.level().isClientSide) {
            HitchableHorse.syncHorseDataToTrackingClients(horse);
        }

        preventDrop.set(hitched);
    }

    @WrapWithCondition(method = "dropLeash(Lnet/minecraft/world/entity/Entity;ZZ)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private static boolean preventLeadDrop(Entity entity, ItemLike item, @Share("preventDrop") LocalBooleanRef preventDrop) {
        return !preventDrop.get();
    }
}
