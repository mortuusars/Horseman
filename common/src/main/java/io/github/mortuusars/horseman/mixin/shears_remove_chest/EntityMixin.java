package io.github.mortuusars.horseman.mixin.shears_remove_chest;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.mortuusars.horseman.Config;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract void gameEvent(Holder<GameEvent> gameEvent, @Nullable Entity entity);

    @Shadow
    public abstract void playSound(SoundEvent sound);

    @Shadow
    private EntityDimensions dimensions;

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract @Nullable ItemEntity spawnAtLocation(ServerLevel level, ItemStack stack, Vec3 offset);

    @ModifyReturnValue(method = "attemptToShearEquipment", at = @At("RETURN"))
    private boolean attemptToShearEquipment(boolean original,
                                            @Local(argsOnly = true) Player player,
                                            @Local(argsOnly = true) InteractionHand hand,
                                            @Local(argsOnly = true) ItemStack stack,
                                            @Local(argsOnly = true) Mob mob) {
        if (original) return true;
        if (!Config.Server.HORSE_SHEARS_REMOVE_CHEST.get()) return false;
        if (!(mob instanceof AbstractChestedHorse chestedHorse)) return false;
        if (!chestedHorse.hasChest()) return false;

        stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
        Vec3 offset = dimensions.attachments().getAverage(EntityAttachment.PASSENGER);
        gameEvent(GameEvent.SHEAR, player);
        playSound(SoundEvents.SHEARS_SNIP);
        if (level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < chestedHorse.inventory.getContainerSize(); i++) {
                spawnAtLocation(serverLevel, chestedHorse.inventory.getItem(i), offset);
                chestedHorse.inventory.setItem(i, ItemStack.EMPTY);
            }
            spawnAtLocation(serverLevel, new ItemStack(Items.CHEST), offset);
            chestedHorse.setChest(false);
            chestedHorse.createInventory();

            CriteriaTriggers.PLAYER_SHEARED_EQUIPMENT.trigger((ServerPlayer) player, stack, mob);
        }

        return true;

    }
}
