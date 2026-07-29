package io.github.mortuusars.horseman.mixin.shears_remove_chest;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.mortuusars.horseman.Config;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements Targeting, EquipmentUser, Leashable {
    @Shadow
    protected abstract void shearItem(Player player, InteractionHand hand, ItemStack heldItem, EquipmentSlot slot, ItemStack itemStackToShear);

    protected MobMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @SuppressWarnings("LocalMayUseName")
    @ModifyReturnValue(method = "attemptToShearEquipment", at = @At("RETURN"))
    private boolean attemptToShearEquipment(boolean original,
                                            @Local(argsOnly = true) Player player,
                                            @Local(argsOnly = true) InteractionHand hand,
                                            @Local(argsOnly = true) ItemStack heldItem) {
        if (original) return true;
        if (!Config.Server.HORSE_SHEARS_REMOVE_CHEST.get()) return false;
        Mob mob = (Mob) (Object) (this);
        if (!(mob instanceof AbstractChestedHorse chestedHorse)) return false;
        if (!chestedHorse.hasChest()) return false;

        heldItem.hurtAndBreak(1, player, hand.asEquipmentSlot());

        if (level() instanceof ServerLevel serverLevel) {
            Vec3 offset = mob.getAttachments().getAverage(EntityAttachment.PASSENGER);
            for (int i = 0; i < chestedHorse.inventory.getContainerSize(); i++) {
                spawnAtLocation(serverLevel, chestedHorse.inventory.getItem(i), offset);
                chestedHorse.inventory.setItem(i, ItemStack.EMPTY);
            }
            spawnAtLocation(serverLevel, new ItemStack(Items.CHEST), offset);
            chestedHorse.setChest(false);
            chestedHorse.createInventory();
        }

        gameEvent(GameEvent.SHEAR, player);
        playSound(SoundEvents.SHEARS_SNIP);
        return true;
    }
}
