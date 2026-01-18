package io.github.mortuusars.horseman.mixin.transparent_mount;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.mortuusars.horseman.client.TransparentMount;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void extractRenderState(Entity entity, EntityRenderState state, float partialTick, CallbackInfo ci) {
        if (Minecraft.getInstance().player != null && entity instanceof LivingEntity livingEntity) {
            ((TransparentMount.RenderState)state).horseman$setOpacity(TransparentMount.getOpacity(livingEntity));
        }
    }

    @ModifyReturnValue(method = "getShadowStrength", at = @At("RETURN"))
    private float getShadowStrength(float original, @Local(argsOnly = true) EntityRenderState renderState) {
        if (renderState instanceof TransparentMount.RenderState state) {
            // Reduce shadow strength, so it doesn't look that jarring when mount is transparent.
            return original * state.horseman$getOpacity();
        }
        return original;
    }
}
