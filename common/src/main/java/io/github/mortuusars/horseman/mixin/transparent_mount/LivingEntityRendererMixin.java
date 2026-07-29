package io.github.mortuusars.horseman.mixin.transparent_mount;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.horseman.client.TransparentMount;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("LocalMayUseName")
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(method = "getRenderType",
          at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/model/EntityModel;renderType(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"),
          cancellable = true)
    private void getRenderType(LivingEntityRenderState state, boolean isBodyVisible, boolean forceTransparent, boolean appearGlowing,
                               CallbackInfoReturnable<RenderType> cir, @Local Identifier texture) {
        if (state instanceof TransparentMount.RenderState mountState) {
            float opacity = mountState.horseman$getOpacity();
            if (opacity > 0f || opacity < 1f) {
                cir.setReturnValue(RenderTypes.entityTranslucent(texture));
            }
        }
    }

    @ModifyArg(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
          at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"),
          index = 6)
    private int modifyColor(int colorArgb, @Local(argsOnly = true) LivingEntityRenderState state) {
        if (state instanceof TransparentMount.RenderState mountState) {
            return TransparentMount.applyOpacity(colorArgb, mountState.horseman$getOpacity());
        }
        return colorArgb;
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
          at = @At("HEAD"),
          cancellable = true)
    private void stopRendering(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                               net.minecraft.client.renderer.state.level.CameraRenderState camera, CallbackInfo ci) {
        if (state instanceof TransparentMount.RenderState mountState && mountState.horseman$getOpacity() <= 0f) {
            ci.cancel();
        }
    }
}
