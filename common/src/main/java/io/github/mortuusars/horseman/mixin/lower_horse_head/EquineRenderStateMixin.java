package io.github.mortuusars.horseman.mixin.lower_horse_head;

import io.github.mortuusars.horseman.client.HorseRenderUtils;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EquineRenderState.class)
public abstract class EquineRenderStateMixin implements HorseRenderUtils.HorsemanEquineRenderState {
    @Unique
    private boolean horseman$riddenByPlayerInFirstPerson = false;

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public boolean getHorsemanRiddenByPlayerInFirstPerson() {
        return horseman$riddenByPlayerInFirstPerson;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void setHorsemanRiddenByPlayerInFirstPerson(boolean value) {
        horseman$riddenByPlayerInFirstPerson = value;
    }
}
