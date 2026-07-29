package io.github.mortuusars.horseman.mixin.transparent_mount;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.horseman.client.TransparentMount;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HorseMarkingLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HorseMarkingLayer.class)
public abstract class HorseMarkingLayerMixin extends RenderLayer<HorseRenderState, HorseModel> {
    public HorseMarkingLayerMixin(RenderLayerParent<HorseRenderState, HorseModel> renderer) {
        super(renderer);
    }

    @WrapOperation(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HorseRenderState;FF)V",
          at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"))
    private <S> void onSubmit(OrderedSubmitNodeCollector instance, Model<? super HorseRenderState> model, S state,
                          PoseStack poseStack, RenderType renderType, int lightCoords, int overlayCoords,
                          int outlineColor, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, Operation<Void> original) {
        if (TransparentMount.storedOpacity > 0f && TransparentMount.storedOpacity < 1f && state instanceof HorseRenderState s) {
            instance.submitModel(getParentModel(), s, poseStack, renderType, lightCoords, overlayCoords,
                  TransparentMount.applyOpacity(0xFFFFFFFF, TransparentMount.storedOpacity), null, outlineColor,
                  crumblingOverlay);
        } else {
            original.call(instance, model, state, poseStack, renderType, lightCoords, overlayCoords, outlineColor, crumblingOverlay);
        }
    }
}
