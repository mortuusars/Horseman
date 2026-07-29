package io.github.mortuusars.horseman.mixin.lower_horse_head;

import io.github.mortuusars.horseman.client.RiddenEquineRenderState;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("deprecation")
@Mixin(AbstractHorseRenderer.class)
public abstract class AbstractHorseRendererMixin <T extends AbstractHorse, S extends EquineRenderState, M extends EntityModel<? super S>>
        extends AgeableMobRenderer<T, S, M> {
    public AbstractHorseRendererMixin(EntityRendererProvider.Context context, M adultModel, M babyModel, float scale) {
        super(context, adultModel, babyModel, scale);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/equine/AbstractHorse;Lnet/minecraft/client/renderer/entity/state/EquineRenderState;F)V",
            at = @At("RETURN"))
    private void extractRenderState(T entity, S state, float partialTicks, CallbackInfo ci) {
        if (state instanceof RiddenEquineRenderState riddenState) {
            boolean riddenByPlayerInFirstPerson = Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON
                    && Minecraft.getInstance().player != null && entity.hasPassenger(Minecraft.getInstance().player);
            riddenState.horseman$setRiddenByPlayerInFirstPerson(riddenByPlayerInFirstPerson);
        }
    }
}
