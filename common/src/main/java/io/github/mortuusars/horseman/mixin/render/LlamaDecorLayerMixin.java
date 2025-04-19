package io.github.mortuusars.horseman.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import io.github.mortuusars.horseman.client.HorseRenderUtils;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.LlamaDecorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.horse.Llama;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = LlamaDecorLayer.class, priority = 950)
public abstract class LlamaDecorLayerMixin {
    @Shadow @Final private LlamaModel<Llama> model;

    @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/horse/Llama;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entityCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
    RenderType makeRenderLayerTranslucent(ResourceLocation location, Operation<RenderType> original, @Local(argsOnly = true) Llama llama, @Share("alpha") LocalDoubleRef alpha) {
        double a = HorseRenderUtils.getAlpha(llama);
        alpha.set(a);
        if (a >= 1.0) return original.call(location);
        return RenderType.entityTranslucent(location);
    }

    @ModifyArg(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/horse/Llama;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/LlamaModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"),
            index = 7)
    float setOpacityForRender(float a, @Share("alpha") LocalDoubleRef alpha) {
        return (float) Mth.clamp(a * alpha.get(), 0.0, 1.0);
    }
}