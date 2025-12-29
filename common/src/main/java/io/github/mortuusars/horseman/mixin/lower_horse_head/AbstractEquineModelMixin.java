package io.github.mortuusars.horseman.mixin.lower_horse_head;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.client.HorseRenderUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.animal.equine.AbstractEquineModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractEquineModel.class, priority = 950)
public abstract class AbstractEquineModelMixin<T extends EquineRenderState> extends EntityModel<T> {
    @Shadow @Final protected ModelPart headParts;

    public AbstractEquineModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/EquineRenderState;)V", at = @At("RETURN"))
    private void onSetupAnim(T state, CallbackInfo ci) {
        if (state instanceof HorseRenderUtils.HorsemanEquineRenderState horsemanState
                && horsemanState.getHorsemanRiddenByPlayerInFirstPerson()) {
            int headXRotOffset = Config.Client.HORSE_HEAD_PITCH_OFFSET.get();
            if (headXRotOffset > 0) {
                this.headParts.xRot = Math.min(this.headParts.xRot + (headXRotOffset / 100f), 1.5f);
            }

            int headYOffset = Config.Client.HORSE_HEAD_Y_OFFSET.get();
            if (headYOffset > 0) {
                this.headParts.y += headYOffset;
            }
        }
    }
}
