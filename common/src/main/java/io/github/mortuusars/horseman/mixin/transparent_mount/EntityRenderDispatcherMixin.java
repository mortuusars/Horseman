package io.github.mortuusars.horseman.mixin.transparent_mount;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.horseman.client.TransparentMount;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @WrapMethod(method = "submit")
    public void onSubmit(EntityRenderState renderState, CameraRenderState camera, double x, double y, double z,
                         PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Operation<Void> original) {
        // Store opacity to use later in layers, where render state is not available.
        TransparentMount.storedOpacity = renderState instanceof TransparentMount.RenderState state ? state.horseman$getOpacity() : 1f;
        original.call(renderState, camera, x, y, z, poseStack, submitNodeCollector);
        TransparentMount.storedOpacity = 1f;
    }
}
