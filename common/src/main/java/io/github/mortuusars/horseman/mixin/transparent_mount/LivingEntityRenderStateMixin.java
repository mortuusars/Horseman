package io.github.mortuusars.horseman.mixin.transparent_mount;

import io.github.mortuusars.horseman.client.TransparentMount;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public abstract class LivingEntityRenderStateMixin implements TransparentMount.RenderState {
    @Unique private float horseman$opacity = 1f;

    @Override
    public float horseman$getOpacity() {
        return horseman$opacity;
    }

    @Override
    public void horseman$setOpacity(float opacity) {
        horseman$opacity = Mth.clamp(opacity, 0, 1);
    }
}
