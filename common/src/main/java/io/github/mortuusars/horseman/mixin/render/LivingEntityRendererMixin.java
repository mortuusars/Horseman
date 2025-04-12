package io.github.mortuusars.horseman.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.horseman.client.HorseRenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntityRenderer.class, priority = 950)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {
    protected LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD") )
    void setAlpha(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci, @Share("alpha") LocalIntRef alpha) {
        if (entity instanceof AbstractHorse) {
            alpha.set(HorseRenderUtils.getAlpha(entity));
        }
    }

    @ModifyArg(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"),
            index = 4)
    int setOpacityAndChromaForRender(int color, @Local(argsOnly = true) LivingEntity entity, @Share("alpha") LocalIntRef alpha) {
        if (entity instanceof AbstractHorse) {
            if (HorseRenderUtils.isJeb(entity)) {
                int index = entity.tickCount / 25 + entity.getId();
                int dyesCount = DyeColor.values().length;
                int currentIndex = index % dyesCount;
                int nextIndex = (index + 1) % dyesCount;
                float transition = ((float)(entity.tickCount % 25) + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true)) / 25.0F;
                int currentColor = Sheep.getColor(DyeColor.byId(currentIndex));
                int nextColor = Sheep.getColor(DyeColor.byId(nextIndex));
                color = FastColor.ARGB32.lerp(transition, currentColor, nextColor);
                // increase brightness because the horse texture is a bit dark
                color = FastColor.ARGB32.color(
                        Math.min(FastColor.ARGB32.red(color) * 2, 255),
                        Math.min(FastColor.ARGB32.green(color) * 2, 255),
                        Math.min(FastColor.ARGB32.blue(color) * 2, 255));
            }

            int a = FastColor.ARGB32.alpha(color);
            a = Mth.clamp((int)(a * (alpha.get() / 255f)), 0, 255);
            return FastColor.ARGB32.color(a, color);
        } else {
            return color;
        }
    }

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void getRenderType(T entity, boolean bodyVisible, boolean translucent, boolean glowing,
                               CallbackInfoReturnable<RenderType> cir, @Share("alpha") LocalIntRef alphaRef) {
        if (entity instanceof AbstractHorse && alphaRef.get() != 255) {
            cir.setReturnValue(RenderType.entityTranslucent(getTextureLocation(entity)));
        }
    }
}