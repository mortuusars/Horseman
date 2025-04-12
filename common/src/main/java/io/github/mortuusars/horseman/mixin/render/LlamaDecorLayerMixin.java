package io.github.mortuusars.horseman.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.mortuusars.horseman.client.HorseRenderUtils;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.LlamaDecorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.horse.Llama;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LlamaDecorLayer.class, priority = 950)
public abstract class LlamaDecorLayerMixin {
    @Shadow @Final private LlamaModel<Llama> model;

    @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/horse/Llama;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entityCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
    RenderType makeRenderLayerTranslucent(ResourceLocation location, Operation<RenderType> original, @Local(argsOnly = true) Llama llama, @Share("alpha") LocalIntRef alpha) {
        int a = HorseRenderUtils.getAlpha(llama);
        alpha.set(a);
        if (a == 255) return original.call(location);
        return RenderType.entityTranslucentCull(location);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/horse/Llama;FFFFFF)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/LlamaModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"),
            cancellable = true)
    private void onRender(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Llama livingEntity,
                          float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                          float netHeadYaw, float headPitch, CallbackInfo ci, @Local VertexConsumer vertexConsumer, @Share("alpha") LocalIntRef alpha) {
        int a = Mth.clamp((int)(255 * (alpha.get() / 255f)), 0, 255);
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.color(a, 0xFFFFFF));
        ci.cancel();
    }
}