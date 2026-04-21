package io.github.mortuusars.horseman.world.item;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.HorsemanServer;
import io.github.mortuusars.horseman.world.summoning.HorseSummoningResult;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class CopperHornItem extends Item {
    public CopperHornItem(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay tooltipDisplay,
                                @NonNull Consumer<Component> tooltip, @NonNull TooltipFlag flag) {
        if (Config.Client.COPPER_HORN_SHOW_TOOLTIP_DETAILS.get()) {
            if (Minecraft.getInstance().hasShiftDown()) {
                tooltip.accept(Component.translatable("item.horseman.copper_horn.tooltip.bind"));
                tooltip.accept(Component.translatable("item.horseman.copper_horn.tooltip.summon"));
            } else {
                tooltip.accept(Component.translatable("item.horseman.tooltip.hold_shift_for_details"));
            }
        }
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NonNull ItemStack stack, Player player, @NonNull LivingEntity target, @NonNull InteractionHand usedHand) {
        if (!player.isSecondaryUseActive() || !(target instanceof AbstractHorse horse)) return InteractionResult.PASS;
        if (player.getCooldowns().isOnCooldown(stack)) return InteractionResult.FAIL;
        if (!horse.is(Horseman.Tags.EntityTypes.SUMMONABLE)) return InteractionResult.PASS;
        if (!horse.isTamed()) return InteractionResult.PASS;
        if (!(player.level() instanceof ServerLevel level)) return InteractionResult.SUCCESS;

        if (horse.getHorsemanBoundData() != null) {
            if (!horse.getHorsemanBoundData().isBoundTo(player)) {
                player.sendSystemMessage(Component.translatable(
                        "gui.horseman.summoning.cannot_bind.already_bound_to_another_player"));
                return InteractionResult.FAIL;
            }

            // Update bind just in case
            HorsemanServer.getSummoning().bind(level, horse, player);
            horse.standIfPossible();
            level.playSound(null, horse, SoundEvents.HORSE_AMBIENT, SoundSource.NEUTRAL, 1, 1);
            addCooldown(player, stack);
            return InteractionResult.SUCCESS;
        }

        HorsemanServer.getSummoning().bind(level, horse, player);
        level.sendParticles(ParticleTypes.NOTE, target.getX(), target.getY() + 0.75, target.getZ(), 10, 0.6, 0.6, 0.6, 0.1);

        horse.standIfPossible();
        level.playSound(null, horse, SoundEvents.HORSE_AMBIENT, SoundSource.NEUTRAL, 1, 1);

        player.startUsingItem(usedHand);
        play(player.level(), player, 1.2F);
        addCooldown(player, stack);

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult use(@NonNull Level level, Player player, @NonNull InteractionHand usedHand) {
        ItemStack itemInHand = player.getItemInHand(usedHand);

        player.startUsingItem(usedHand);

        if (player instanceof ServerPlayer serverPlayer) {
            HorseSummoningResult result = HorsemanServer.getSummoning().summonBoundHorseTo(serverPlayer);

            if (Config.Server.COPPER_HORN_ERROR_MESSAGES.get() && result.getMessage() instanceof Component message) {
                player.sendSystemMessage(message);
            }

            if ((result != HorseSummoningResult.SUCCESS && result != HorseSummoningResult.HORSE_IS_DEAD)
                  && Config.Server.COPPER_HORN_FAIL_SOUND.get()) {
                level.playSound(null, player, Horseman.SoundEvents.COPPER_HORN_TOOT_FAIL.get(),
                      SoundSource.PLAYERS, 1f, player.getRandom().nextFloat() * 0.1f + 0.95f);
            } else {
                play(level, player, player.getRandom().nextFloat() * 0.1f + 0.95f);
            }
        }

        addCooldown(player, itemInHand);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.CONSUME;
    }

    protected void addCooldown(Player player, ItemStack stack) {
        player.getCooldowns().addCooldown(stack, Config.Server.COPPER_HORN_COOLDOWN.get());
    }

    protected void play(Level level, Player player, float pitch) {
        float volume = Config.Server.COPPER_HORN_SOUND_RANGE.get() / 16.0F;
        level.playSound(null, player, Horseman.SoundEvents.COPPER_HORN_TOOT.get(), SoundSource.PLAYERS, volume, pitch);
        level.gameEvent(GameEvent.INSTRUMENT_PLAY, player.position(), GameEvent.Context.of(player));
    }
}
