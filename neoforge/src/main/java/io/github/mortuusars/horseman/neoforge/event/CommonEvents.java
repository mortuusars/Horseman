package io.github.mortuusars.horseman.neoforge.event;

import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.network.neoforge.PacketsImpl;
import io.github.mortuusars.horseman.network.packet.C2SPackets;
import io.github.mortuusars.horseman.network.packet.CommonPackets;
import io.github.mortuusars.horseman.network.packet.Packet;
import io.github.mortuusars.horseman.network.packet.S2CPackets;
import io.github.mortuusars.horseman.world.calling.HorseCalling;
import io.github.mortuusars.horseman.world.item.CopperHornItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class CommonEvents {
    @EventBusSubscriber(modid = Horseman.ID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SuppressWarnings("unchecked")
        @SubscribeEvent
        public static void registerPackets(RegisterPayloadHandlersEvent event) {
            PayloadRegistrar registrar = event.registrar("1");
            // This monstrosity is to avoid having to define packets for forge and fabric separately.
            for (CustomPacketPayload.TypeAndCodec<? extends FriendlyByteBuf, ? extends CustomPacketPayload> definition : S2CPackets.getDefinitions()) {
                registrar.playToClient((CustomPacketPayload.Type<Packet>) definition.type(),
                        (StreamCodec<FriendlyByteBuf, Packet>) definition.codec(), PacketsImpl::handle);
            }

            for (CustomPacketPayload.TypeAndCodec<? extends FriendlyByteBuf, ? extends CustomPacketPayload> definition : C2SPackets.getDefinitions()) {
                registrar.playToServer((CustomPacketPayload.Type<Packet>) definition.type(),
                        (StreamCodec<FriendlyByteBuf, Packet>) definition.codec(), PacketsImpl::handle);
            }

            for (CustomPacketPayload.TypeAndCodec<? extends FriendlyByteBuf, ? extends CustomPacketPayload> definition : CommonPackets.getDefinitions()) {
                registrar.playBidirectional((CustomPacketPayload.Type<Packet>) definition.type(),
                        (StreamCodec<FriendlyByteBuf, Packet>) definition.codec(), PacketsImpl::handle);
            }
        }

        @SubscribeEvent
        public static void buildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
                event.getParameters().holders()
                        .lookup(Registries.INSTRUMENT)
                        .flatMap(registryLookup -> registryLookup.get(InstrumentTags.GOAT_HORNS))
                        .ifPresent(named -> named.stream()
                                .map(holder -> CopperHornItem.create(Horseman.Items.COPPER_HORN.get(), holder))
                                .forEach(itemStack -> event.accept(itemStack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS)));
            }
        }
    }

    @EventBusSubscriber(modid = Horseman.ID, bus = EventBusSubscriber.Bus.GAME)
    public static class GameBus {
        @SubscribeEvent
        public static void entityJoinLevel(EntityJoinLevelEvent event) {
            if (event.getLevel() instanceof ServerLevel serverLevel
                    && event.getEntity() instanceof AbstractHorse horse
                    && HorseCalling.horseLoaded(serverLevel, horse)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void entityLeaveLevel(EntityLeaveLevelEvent event) {
            if (event.getLevel() instanceof ServerLevel serverLevel
                    && event.getEntity() instanceof AbstractHorse horse) {
                HorseCalling.horseUnloaded(serverLevel, horse);
            }
        }
    }
}
