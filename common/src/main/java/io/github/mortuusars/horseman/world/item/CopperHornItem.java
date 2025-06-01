package io.github.mortuusars.horseman.world.item;

import com.mojang.datafixers.util.Pair;
import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.HorsemanServer;
import io.github.mortuusars.horseman.world.calling.CallResult;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class CopperHornItem extends InstrumentItem {
    protected final TagKey<Instrument> instruments;

    public CopperHornItem(Properties properties, TagKey<Instrument> instruments) {
        super(properties, instruments);
        this.instruments = instruments;
    }

    public static ItemStack create(Item item, Holder<Instrument> instrument) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.set(DataComponents.INSTRUMENT, instrument);
        return itemStack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltip, tooltipFlag);
        if (Config.Client.COPPER_HORN_SHOW_TOOLTIP_DETAILS.get()) {
            if (Screen.hasShiftDown()) {
                tooltip.add(Component.translatable("item.horseman.copper_horn.tooltip.bind"));
                tooltip.add(Component.translatable("item.horseman.copper_horn.tooltip.call"));
            } else {
                tooltip.add(Component.translatable("item.horseman.tooltip.hold_shift_for_details"));
            }
        }
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand usedHand) {
        if (!player.isSecondaryUseActive() || !(target instanceof AbstractHorse horse)) return InteractionResult.PASS;
        if (!horse.getType().is(Horseman.Tags.EntityTypes.CALLABLE)) return InteractionResult.PASS;
        if (!horse.isTamed()) return InteractionResult.PASS;
        if (!(player.level() instanceof ServerLevel level)) return InteractionResult.SUCCESS;

        @Nullable Pair<ResourceKey<Instrument>, Instrument> instrumentData = getInstrumentData(stack);
        if (instrumentData == null) {
            player.displayClientMessage(Component.translatable("gui.horseman.calling.cannot_bind.no_instrument"), true);
            return InteractionResult.FAIL;
        }

        ResourceKey<Instrument> instrumentKey = instrumentData.getFirst();
        Instrument instrument = instrumentData.getSecond();

        if (horse.getHorsemanBoundData() != null) {
            if (horse.getHorsemanBoundData().isBoundTo(player)) {
                if (horse.getHorsemanBoundData().instrument().equals(instrumentKey)) {
                    // Update bind just in case
                    HorsemanServer.horseCalling().bind(level, horse, player, instrumentKey);
                    player.displayClientMessage(Component.translatable(
                            "gui.horseman.calling.cannot_bind.already_bound_to_you"), true);
                    return InteractionResult.FAIL;
                }
            } else {
                player.displayClientMessage(Component.translatable(
                        "gui.horseman.calling.cannot_bind.already_bound_to_someone_else"), true);
                return InteractionResult.FAIL;
            }
        }

        HorsemanServer.horseCalling().bind(level, horse, player, instrumentKey);
        level.sendParticles(ParticleTypes.NOTE, target.getX(), target.getY() + 0.75, target.getZ(), 10, 0.6, 0.6, 0.6, 0.1);

        horse.standIfPossible();
        level.playSound(null, horse, SoundEvents.HORSE_AMBIENT, SoundSource.NEUTRAL, 1, 1);

        player.startUsingItem(usedHand);
        playInstrument(player.level(), player, instrument, 1.2F);
        cooldown(player, instrument);
        player.awardStat(Stats.ITEM_USED.get(this));

        return InteractionResult.SUCCESS_NO_ITEM_USED;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemInHand = player.getItemInHand(usedHand);

        @Nullable Pair<ResourceKey<Instrument>, Instrument> instrumentData = getInstrumentData(itemInHand);
        if (instrumentData == null) return InteractionResultHolder.fail(itemInHand);
        ResourceKey<Instrument> instrumentKey = instrumentData.getFirst();
        Instrument instrument = instrumentData.getSecond();

        player.startUsingItem(usedHand);

        if (level instanceof ServerLevel serverLevel) {
            CallResult callResult = HorsemanServer.horseCalling().call(serverLevel, player, instrumentKey);
            @Nullable Component message = getCallResultMessage(callResult);
            if (message != null) {
                player.displayClientMessage(message, true);
            }
            playInstrument(level, player, instrument, getSoundPitchFromCallResult(callResult));
        }

        cooldown(player, instrument);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.consume(itemInHand);
    }

    protected void cooldown(Player player, Instrument instrument) {
        int cooldown = Config.Server.COPPER_HORN_COOLDOWN.get();
        if (cooldown < 0) {
            cooldown = instrument.useDuration();
        }
        player.getCooldowns().addCooldown(this, cooldown);
    }

    protected @Nullable Component getCallResultMessage(CallResult result) {
        return switch (result) {
            case SUCCESS -> null;
            case NO_BOUND_HORSE -> Component.translatable("gui.horseman.calling.cannot_call.no_bound_horse");
            case HORSE_IS_DEAD -> Component.translatable("gui.horseman.calling.cannot_call.dead");
            case TOO_FAR -> Component.translatable("gui.horseman.calling.cannot_call.too_far");
            case INVALID_DIMENSION ->
                    Component.translatable("gui.horseman.calling.cannot_call.in_other_dimension");
            case NO_SPACE -> Component.translatable("gui.horseman.calling.cannot_call.no_space");
            case ERROR_HORSE_IS_NOT_BOUND, ERROR_ENTITY_NOT_CREATED ->
                    Component.translatable("gui.horseman.calling.cannot_call.wrong_or_defective_horse");
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

    // --

    protected boolean hasInstrument(ItemStack stack) {
        return getInstrumentData(stack) != null;
    }

    protected void playInstrument(Level level, Player player, Instrument instrument, float pitch) {
        SoundEvent soundEvent = instrument.soundEvent().value();
        float volume = Config.Server.COPPER_HORN_SOUND_RANGE.get() / 16.0F;
        level.playSound(null, player, soundEvent, SoundSource.RECORDS, volume, pitch);
        level.gameEvent(GameEvent.INSTRUMENT_PLAY, player.position(), GameEvent.Context.of(player));
    }

    protected Optional<Holder<Instrument>> getInstrument(ItemStack stack) {
        @Nullable Holder<Instrument> holder = stack.get(DataComponents.INSTRUMENT);
        if (holder != null) {
            return Optional.of(holder);
        } else {
            Iterator<Holder<Instrument>> iterator = BuiltInRegistries.INSTRUMENT.getTagOrEmpty(this.instruments).iterator();
            return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
        }
    }

    protected @Nullable Pair<ResourceKey<Instrument>, Instrument> getInstrumentData(ItemStack stack) {
        return getInstrument(stack).map(holder -> {
            if (holder.unwrapKey().isEmpty()) return null;
            return Pair.of(holder.unwrapKey().orElseThrow(), holder.value());
        }).orElse(null);
    }
}
