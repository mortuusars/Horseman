package io.github.mortuusars.horseman.mixin.summoning;

import io.github.mortuusars.horseman.world.summoning.BoundData;
import io.github.mortuusars.horseman.world.summoning.SummonableHorse;
import io.github.mortuusars.horseman.world.item.CopperHornItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal implements SummonableHorse {
    protected AbstractHorseMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    // --

    @Unique
    private @Nullable BoundData horseman$boundData = null;

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public BoundData getHorsemanBoundData() {
        return horseman$boundData;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void setHorsemanBoundData(@Nullable BoundData data) {
        horseman$boundData = data;
    }

    // --

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    protected void onAddAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (horseman$boundData != null) {
            tag.put("HorsemanBoundData", BoundData.CODEC.encodeStart(NbtOps.INSTANCE, horseman$boundData).getOrThrow());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    protected void onReadAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        horseman$boundData = tag.getCompound("HorsemanBoundData")
                .map(compound -> BoundData.CODEC.decode(NbtOps.INSTANCE, compound).getOrThrow().getFirst())
                .orElse(null);
    }

    // --

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        // Allow Copper Horn to process its interaction logic:
        if (player.isSecondaryUseActive() && player.getItemInHand(hand).getItem() instanceof CopperHornItem) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
