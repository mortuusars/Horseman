package io.github.mortuusars.horseman.world.item;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.HorsemanServer;
import io.github.mortuusars.horseman.world.summoning.CallResult;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CopperHornItem extends Item {
    public CopperHornItem(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay,
                                Consumer<Component> tooltip, TooltipFlag flag) {
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
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand usedHand) {
        if (!player.isSecondaryUseActive() || !(target instanceof AbstractHorse horse)) return InteractionResult.PASS;
        if (!horse.getType().is(Horseman.Tags.EntityTypes.SUMMONABLE)) return InteractionResult.PASS;
        if (!horse.isTamed()) return InteractionResult.PASS;
        if (!(player.level() instanceof ServerLevel level)) return InteractionResult.SUCCESS;

        if (horse.getHorsemanBoundData() != null) {
            if (horse.getHorsemanBoundData().isBoundTo(player)) {
                // Update bind just in case
                HorsemanServer.getSummoning().bind(level, horse, player);
                horse.standIfPossible();
                level.playSound(null, horse, SoundEvents.HORSE_AMBIENT, SoundSource.NEUTRAL, 1, 1);
                cooldown(player, stack);
                return InteractionResult.SUCCESS;
            } else {
                player.displayClientMessage(Component.translatable(
                        "gui.horseman.summoning.cannot_bind.already_bound_to_someone_else"), true);
                return InteractionResult.FAIL;
            }
        }

        HorsemanServer.getSummoning().bind(level, horse, player);
        level.sendParticles(ParticleTypes.NOTE, target.getX(), target.getY() + 0.75, target.getZ(), 10, 0.6, 0.6, 0.6, 0.1);

        horse.standIfPossible();
        level.playSound(null, horse, SoundEvents.HORSE_AMBIENT, SoundSource.NEUTRAL, 1, 1);

        player.startUsingItem(usedHand);
        play(player.level(), player, 1.2F);
        cooldown(player, stack);
        player.awardStat(Stats.ITEM_USED.get(this));

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemInHand = player.getItemInHand(usedHand);

        player.startUsingItem(usedHand);

        if (player instanceof ServerPlayer serverPlayer) {
            CallResult callResult = HorsemanServer.getSummoning().call(serverPlayer);
            @Nullable Component message = getCallResultMessage(callResult);
            if (message != null) {
                player.displayClientMessage(message, true);
            }
            play(level, player, getSoundPitchFromCallResult(callResult));
        }

        cooldown(player, itemInHand);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.CONSUME;
    }

    protected void cooldown(Player player, ItemStack stack) {
        int cooldown = Config.Server.COPPER_HORN_COOLDOWN.get();
        if (cooldown < 0) {
            cooldown = 100;
        }
        player.getCooldowns().addCooldown(stack, cooldown);
    }

    protected @Nullable Component getCallResultMessage(CallResult result) {
        if (!Config.Server.COPPER_HORN_ERROR_MESSAGES.get()) {
            return null;
        }

        return switch (result) {
            case SUCCESS -> null;
            case NO_BOUND_HORSE -> Component.translatable("gui.horseman.summoning.cannot_summon.no_bound_horse");
            case HORSE_IS_DEAD -> Component.translatable("gui.horseman.summoning.cannot_summon.dead");
            case TOO_FAR -> Component.translatable("gui.horseman.summoning.cannot_summon.too_far");
            case INVALID_DIMENSION ->
                    Component.translatable("gui.horseman.summoning.cannot_summon.in_other_dimension");
            case NO_SPACE -> Component.translatable("gui.horseman.summoning.cannot_summon.no_space");
            case ERROR_HORSE_IS_NOT_BOUND, ERROR_ENTITY_NOT_CREATED ->
                    Component.translatable("gui.horseman.summoning.cannot_summon.wrong_or_defective_horse");
        };
    }

    protected float getSoundPitchFromCallResult(CallResult result) {
        return switch (result) {
            case SUCCESS -> 1.0F;
            case HORSE_IS_DEAD -> 0.6F;
            case TOO_FAR, INVALID_DIMENSION, NO_BOUND_HORSE, NO_SPACE -> 0.85F;
            case ERROR_HORSE_IS_NOT_BOUND, ERROR_ENTITY_NOT_CREATED -> 0.9F;
        };
    }

    protected void play(Level level, Player player, float pitch) {
        SoundEvent soundEvent = Horseman.SoundEvents.COPPER_HORN.get();
        float volume = Config.Server.COPPER_HORN_SOUND_RANGE.get() / 16.0F;
        level.playSound(null, player, soundEvent, SoundSource.RECORDS, volume, pitch);
        level.gameEvent(GameEvent.INSTRUMENT_PLAY, player.position(), GameEvent.Context.of(player));
    }
}
