package io.github.mortuusars.horseman.mixin.lower_horse_head;

import io.github.mortuusars.horseman.client.RiddenEquineRenderState;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EquineRenderState.class)
public abstract class EquineRenderStateMixin implements RiddenEquineRenderState {
    @Unique
    private boolean horseman$riddenByPlayerInFirstPerson = false;

    @Override
    public boolean horseman$getRiddenByPlayerInFirstPerson() {
        return horseman$riddenByPlayerInFirstPerson;
    }

    @Override
    public void horseman$setRiddenByPlayerInFirstPerson(boolean value) {
        horseman$riddenByPlayerInFirstPerson = value;
    }
}
