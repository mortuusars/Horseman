package io.github.mortuusars.horseman.mixin.transparent_mount;

import io.github.mortuusars.horseman.client.TransparentMount;
import net.minecraft.client.renderer.entity.layers.HorseMarkingLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HorseMarkingLayer.class)
public class HorseMarkingLayerMixin {
    @ModifyArg(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HorseRenderState;FF)V",
          at = @At(value = "INVOKE",
          target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"),
          index = 6)
    private int modifyColor(int colorArgb) {
        return TransparentMount.applyOpacity(colorArgb, TransparentMount.storedOpacity);
    }
}
