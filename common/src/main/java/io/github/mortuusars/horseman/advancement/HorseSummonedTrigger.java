package io.github.mortuusars.horseman.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HorseSummonedTrigger extends SimpleCriterionTrigger<HorseSummonedTrigger.TriggerInstance> {
    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, AbstractHorse horse) {
        this.trigger(player, triggerInstance ->
                triggerInstance.matches(player, horse));
    }

    public record TriggerInstance(Optional<Holder<LootItemCondition>> player,
                                  Optional<EntityPredicate> horse) implements SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        EntityPredicate.CODEC.optionalFieldOf("horse").forGetter(TriggerInstance::horse))
                .apply(instance, TriggerInstance::new));

        public boolean matches(ServerPlayer player, AbstractHorse horse) {
            return (this.horse.isEmpty() || this.horse.get().matches(player.level(), horse.position(), horse));
        }
    }
}