package io.github.mortuusars.horseman.mixin.hitching;

import io.github.mortuusars.horseman.world.HitchableHorse;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Leashable.class)
public interface LeashableMixin {
    /**
     * I haven't found a better way than overwriting whole method.
     * @author Horseman - mortuusars
     * @reason Prevent Lead dropping when horse is hitched.
     */
    @Overwrite
    private static <E extends Entity & Leashable> void dropLeash(E entity, boolean broadcastPacket, boolean dropItem) {
        @Nullable HitchableHorse horse = entity instanceof HitchableHorse ? ((HitchableHorse) entity) : null;
        boolean isHitched = horse != null && HitchableHorse.isHitched(horse);

        if (isHitched) {
            dropItem = false;
        }

        Leashable.LeashData leashData = entity.getLeashData();
        if (leashData != null && leashData.leashHolder != null) {
            entity.setLeashData(null);

            if (horse != null) {
                HitchableHorse.setHitched(horse, false);
                if (!entity.level().isClientSide) {
                    HitchableHorse.syncHorseDataToTrackingClients(horse);
                }
            }

            if (!entity.level().isClientSide && dropItem) {
                entity.spawnAtLocation(Items.LEAD);
            }

            if (broadcastPacket && entity.level() instanceof ServerLevel serverLevel) {
                serverLevel.getChunkSource().broadcast(entity, new ClientboundSetEntityLinkPacket(entity, null));
            }
        }
    }
}
