package io.github.mortuusars.horseman.mixin.hitching;

import io.github.mortuusars.horseman.world.HitchableHorse;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "checkAndHandleImportantInteractions", at = @At(value = "HEAD"), cancellable = true)
    private void onCheckAndHandleImportantInteractions(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(this instanceof HitchableHorse horse)
                || !horse.horseman$asHorse().isTamed()
                || horse.horseman$asHorse().isBaby()
                || !HitchableHorse.isEnabled()) {
            return;
        }

        ItemStack itemInHand = player.getItemInHand(hand);

        if (itemInHand.getItem() instanceof ShearsItem && !player.isSecondaryUseActive() && HitchableHorse.hasLead(horse)) {
            if (HitchableHorse.isHitched(horse)) {
                horse.horseman$asHorse().removeLeash();
            }
            if (level() instanceof ServerLevel serverLevel) {
                ItemStack leadStack = HitchableHorse.getLead(horse);
                spawnAtLocation(serverLevel, leadStack);
            }
            level().playSound(player, player, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 0.8f, 1f);
            HitchableHorse.setLead(horse, ItemStack.EMPTY);
            if (!level().isClientSide) {
                HitchableHorse.syncHorseDataToTrackingClients(horse);
            }
            cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
            return;
        }

        if (itemInHand.getItem() instanceof LeadItem && player.isSecondaryUseActive()
                && HitchableHorse.isHitchable(horse) && !HitchableHorse.hasLead(horse)) {
            ItemStack leadStack = itemInHand.split(1);
            HitchableHorse.setLead(horse, leadStack);
            player.swing(hand);
            cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
            level().playSound(player, player, SoundEvents.LEAD_TIED, SoundSource.PLAYERS, 0.8f, 1f);
            return;
        }
    }
}
