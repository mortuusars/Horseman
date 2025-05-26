package io.github.mortuusars.horseman.world.item;

import io.github.mortuusars.horseman.world.calling.CallableHorse;
import io.github.mortuusars.horseman.world.calling.HorseCalling;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        //TODO: Press [Shift] for details
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand usedHand) {
        if (!player.isSecondaryUseActive() || !(target instanceof AbstractHorse horse)) return InteractionResult.PASS;
        if (!horse.isTamed()) return InteractionResult.PASS;

        Optional<? extends Holder<Instrument>> instrumentHolder = this.getInstrument(stack);
        if (instrumentHolder.isEmpty() || instrumentHolder.get().unwrapKey().isEmpty()) {
            player.displayClientMessage(Component.translatable("gui.horseman.calling.cannot_bind.no_instrument"), true);
            return InteractionResult.FAIL;
        }
        ResourceKey<Instrument> instrument = instrumentHolder.get().unwrapKey().orElseThrow();

//        if (callableHorse.horseman_isBound()) {
//            if (callableHorse.horseman_isBoundTo(player)) {
//                player.displayClientMessage(Component.translatable("gui.horseman.calling.already_bound_to_you"), true);
//            } else {
//                player.displayClientMessage(Component.translatable("gui.horseman.calling.already_bound_to_someone_else"), true);
//            }
//            return InteractionResult.FAIL;
//        }

        if (player.level() instanceof ServerLevel level) {
            HorseCalling.bind(level, horse, player, instrument);
            horse.standIfPossible();
            //TODO: effects, sounds? etc
        }

        player.startUsingItem(usedHand);
        Instrument instr = instrumentHolder.get().value();
        play(player.level(), player, instr);

//        player.getCooldowns().addCooldown(this, instr.useDuration());
        player.getCooldowns().addCooldown(this, 10);
        player.awardStat(Stats.ITEM_USED.get(this));

        return InteractionResult.SUCCESS_NO_ITEM_USED;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemInHand = player.getItemInHand(usedHand);
        Optional<? extends Holder<Instrument>> optional = this.getInstrument(itemInHand);
        if (optional.isEmpty()) {
            return InteractionResultHolder.fail(itemInHand);
        }

        Instrument instrument = (Instrument) ((Holder<?>) optional.get()).value();
        player.startUsingItem(usedHand);
        play(level, player, instrument);

        if (level instanceof ServerLevel serverLevel && optional.get().unwrapKey().isPresent()) {
            HorseCalling.call(serverLevel, player, optional.get().unwrapKey().get());
        }

        player.getCooldowns().addCooldown(this, instrument.useDuration());
        player.getCooldowns().addCooldown(this, 10);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.consume(itemInHand);
    }

    //TODO: require finish using to call
//    @Override
//    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
//        return super.finishUsingItem(stack, level, livingEntity);
//    }

    protected Optional<Holder<Instrument>> getInstrument(ItemStack stack) {
        @Nullable Holder<Instrument> holder = stack.get(DataComponents.INSTRUMENT);
        if (holder != null) {
            return Optional.of(holder);
        } else {
            Iterator<Holder<Instrument>> iterator = BuiltInRegistries.INSTRUMENT.getTagOrEmpty(this.instruments).iterator();
            return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
        }
    }

    protected void play(Level level, Player player, Instrument instrument) {
        SoundEvent soundEvent = instrument.soundEvent().value();
        //TODO: configurable range
        float volume = instrument.range() / 16.0F;
        level.playSound(player, player, soundEvent, SoundSource.RECORDS, volume, 1.0F);
        level.gameEvent(GameEvent.INSTRUMENT_PLAY, player.position(), GameEvent.Context.of(player));
    }
}
