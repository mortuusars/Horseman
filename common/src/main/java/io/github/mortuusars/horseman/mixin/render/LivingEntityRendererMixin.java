package io.github.mortuusars.horseman.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.mortuusars.horseman.client.HorseRenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntityRenderer.class, priority = 950)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {
    protected LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"))
    void setAlpha(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci, @Share("alpha") LocalDoubleRef alpha) {
        if (entity instanceof AbstractHorse) {
            alpha.set(HorseRenderUtils.getAlpha(entity));
        }
    }

    @Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"))
    private void onRender(EntityModel<?> instance, PoseStack poseStack, VertexConsumer vertexConsumer,
                          int packedLight, int packedOverlay, float r, float g, float b, float a, @Local(argsOnly = true) T entity, @Share("alpha") LocalDoubleRef alpha) {
        if (entity instanceof AbstractHorse) {
            if (HorseRenderUtils.isJeb(entity)) {
                int index = entity.tickCount / 25 + entity.getId();
                int dyesCount = DyeColor.values().length;
                int currentIndex = index % dyesCount;
                int nextIndex = (index + 1) % dyesCount;
                float transition = ((float) (entity.tickCount % 25) + Minecraft.getInstance().getDeltaFrameTime()) / 25.0F;
                float[] currentColors = Sheep.getColorArray(DyeColor.byId(currentIndex));
                float[] nextColors = Sheep.getColorArray(DyeColor.byId(nextIndex));

                r = currentColors[0] * (1.0F - transition) + nextColors[0] * transition;
                g = currentColors[1] * (1.0F - transition) + nextColors[1] * transition;
                b = currentColors[2] * (1.0F - transition) + nextColors[2] * transition;

                // increase brightness because the horse texture is a bit dark
                r = Mth.clamp(r * 2, 0, 1);
                g = Mth.clamp(g * 2, 0, 1);
                b = Mth.clamp(b * 2, 0, 1);
            }

            a = (float) Mth.clamp(a * alpha.get(), 0.0, 1.0);
        }

        instance.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, r, g, b, a);
    }

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void getRenderType(T entity, boolean bodyVisible, boolean translucent, boolean glowing,
                               CallbackInfoReturnable<RenderType> cir, @Share("alpha") LocalDoubleRef alphaRef) {
        if (entity instanceof AbstractHorse && alphaRef.get() != 1.0) {
            cir.setReturnValue(RenderType.entityTranslucent(getTextureLocation(entity)));
        }
    }
}