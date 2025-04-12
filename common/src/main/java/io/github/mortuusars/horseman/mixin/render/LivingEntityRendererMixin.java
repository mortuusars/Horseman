package io.github.mortuusars.horseman.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.horseman.client.HorseRenderUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntityRenderer.class, priority = 950)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {
    protected LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

//    @Inject(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
//            at = @At("TAIL"))
//    void setPlayerPassenger(CallbackInfo ci, @Local(argsOnly = true) LivingEntityRenderState livingEntityRenderState, @Local(argsOnly = true) LivingEntity livingEntity) {
//        if (isRideableEntityRenderState(livingEntityRenderState)) {
//            boolean playerPassenger = livingEntity.hasPassenger(MinecraftClient.getInstance().player);
//            ((ExtendedRideableEntityRenderState) livingEntityRenderState).horsebuff$setId(livingEntity.getId());
//            ((ExtendedRideableEntityRenderState) livingEntityRenderState).horsebuff$setPlayerPassenger(playerPassenger);
//        }
//    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD") )
    void setAlpha(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci, @Share("alpha") LocalIntRef alpha) {
        if (entity instanceof AbstractHorse) {
            alpha.set(HorseRenderUtils.getAlpha(entity));
        }
    }

    @ModifyArg(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"),
            index = 4)
    int setOpacityAndChromaForRender(int color, @Local(argsOnly = true) LivingEntity entity, @Share("alpha") LocalIntRef alpha) {
        if (entity instanceof AbstractHorse) {
//            if (isJeb(livingEntityRenderState) && livingEntityRenderState instanceof ExtendedRideableEntityRenderState extendedRideableEntityRenderState) {
//                // see net/minecraft/client/render/entity/feature/SheepWoolFeatureRenderer
//                int dyeIndex = MathHelper.floor(livingEntityRenderState.age) / 25 + extendedRideableEntityRenderState.horsebuff$getId();
//                int numDyes = DyeColor.values().length;
//                int currentDye = SheepEntity.getRgbColor(DyeColor.byId(dyeIndex % numDyes));
//                int nextDye = SheepEntity.getRgbColor(DyeColor.byId((dyeIndex + 1) % numDyes));
//                float dyeTransitionProgress = ((float) (MathHelper.floor(livingEntityRenderState.age) % 25) + MathHelper.fractionalPart(livingEntityRenderState.age)) / 25.0F;
//                color = ColorHelper.lerp(dyeTransitionProgress, currentDye, nextDye);
//                // increase brightness by a bit because the horse texture is a bit dark
//                color = ColorHelper.getArgb(Math.min(ColorHelper.getRed(color) * 2, 255), Math.min(ColorHelper.getGreen(color) * 2, 255), Math.min(ColorHelper.getBlue(color) * 2, 255));
//            }

            int a = FastColor.ARGB32.alpha(color);
            a = Mth.clamp((int)(a * (alpha.get() / 255f)), 0, 255);
            return FastColor.ARGB32.color(a, color);
        } else {
            return color;
        }
    }

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void getRenderType(T entity, boolean bodyVisible, boolean translucent, boolean glowing,
                               CallbackInfoReturnable<RenderType> cir, @Share("alpha") LocalIntRef alphaRef) {
        if (entity instanceof AbstractHorse && alphaRef.get() != 255) {
            cir.setReturnValue(RenderType.entityTranslucent(getTextureLocation(entity)));
        }
    }
}