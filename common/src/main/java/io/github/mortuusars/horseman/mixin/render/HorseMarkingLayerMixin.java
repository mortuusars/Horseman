package io.github.mortuusars.horseman.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.mortuusars.horseman.client.HorseRenderUtils;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HorseMarkingLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.horse.Horse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = HorseMarkingLayer.class, priority = 960)
public abstract class HorseMarkingLayerMixin extends RenderLayer<Horse, HorseModel<Horse>> {
    public HorseMarkingLayerMixin(RenderLayerParent<Horse, HorseModel<Horse>> renderer) {
        super(renderer);
    }

    @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/horse/Horse;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entityTranslucent(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
    RenderType makeRenderLayerTranslucent(ResourceLocation location, Operation<RenderType> original, @Local(argsOnly = true) Horse horse, @Share("alpha") LocalFloatRef alpha) {
        float a = HorseRenderUtils.getAlpha(horse);
        alpha.set(a);
        if (a < 1.0) {
            return RenderType.entityTranslucent(location);
        }
        return original.call(location);
    }

    @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/horse/Horse;FFFFFF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HorseModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private void renderWithOpacity(HorseModel<?> instance, PoseStack poseStack, VertexConsumer vertexConsumer,
                                int packedLight, int packedOverlay, Operation<Void> original, @Share("alpha") LocalFloatRef alpha) {
        float aVal = alpha.get();
        if (aVal < 1.0f) {
            int a = Mth.clamp((int)(255 * aVal), 0, 255);
            getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.color(a, 0xFFFFFF));
        } else {
            original.call(instance, poseStack, vertexConsumer, packedLight, packedOverlay);
        }
    }
}