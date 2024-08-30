package io.github.mortuusars.horseman.mixin;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HorseModel.class, priority = 950)
public abstract class HorseModelMixin extends AgeableListModel<AbstractHorse> {
    @Shadow @Final protected ModelPart headParts;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/animal/horse/AbstractHorse;FFFFF)V", at = @At("RETURN"))
    private void onSetupAnim(AbstractHorse entity, float limbSwing, float limbSwingAmount,
                             float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON
                && Minecraft.getInstance().player != null && entity.hasPassenger(Minecraft.getInstance().player)) {
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
