package io.github.mortuusars.horseman.forge.mixin.realistic_horse_genetics.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import io.github.mortuusars.horseman.client.HorseRenderUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import sekelsta.horse_colors.client.renderer.HorseSaddleLayer;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;

@Mixin(value = HorseSaddleLayer.class, priority = 950)
public abstract class HorseSaddleLayerMixin {
    @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILsekelsta/horse_colors/entity/AbstractHorseGenetic;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entityCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
    RenderType makeRenderLayerTranslucent(ResourceLocation location, Operation<RenderType> original, @Local(argsOnly = true) AbstractHorseGenetic horse, @Share("alpha") LocalDoubleRef alpha) {
        double a = HorseRenderUtils.getAlpha(horse);
        alpha.set(a);
        if (a <= 1.0) return original.call(location);
        return RenderType.entityTranslucent(location);
    }

    @ModifyArg(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILsekelsta/horse_colors/entity/AbstractHorseGenetic;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lsekelsta/horse_colors/client/renderer/HorseGeneticModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"),
            index = 7)
    float setOpacityForRender(float a, @Share("alpha") LocalDoubleRef alpha) {
        return (float) Mth.clamp(a * alpha.get(), 0.0, 1.0);
    }
}