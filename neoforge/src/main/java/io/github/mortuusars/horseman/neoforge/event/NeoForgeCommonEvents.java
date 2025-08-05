package io.github.mortuusars.horseman.neoforge.event;

import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.HorsemanServer;
import io.github.mortuusars.horseman.network.neoforge.PacketsImpl;
import io.github.mortuusars.horseman.network.packet.C2SPackets;
import io.github.mortuusars.horseman.network.packet.CommonPackets;
import io.github.mortuusars.horseman.network.packet.Packet;
import io.github.mortuusars.horseman.network.packet.S2CPackets;
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
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Horseman.ID)
public class NeoForgeCommonEvents {
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

    @SubscribeEvent
    public static void serverStarted(ServerStartedEvent event) {
        HorsemanServer.serverStarted(event.getServer());
    }

    @SubscribeEvent
    public static void serverStopped(ServerStoppedEvent event) {
        HorsemanServer.serverStopped(event.getServer());
    }

    @SubscribeEvent
    public static void entityJoinLevel(EntityJoinLevelEvent event) {
        try {
            if (event.getLevel() instanceof ServerLevel serverLevel
                    && event.getEntity() instanceof AbstractHorse horse
                    && HorsemanServer.getSummoning().onHorseLoaded(serverLevel, horse)) {
                event.setCanceled(true);
            }
        } catch (Exception e) {
            Horseman.LOGGER.warn("Cannot handle Horseman's entityJoinLevel event. {}. But it shouldn't be a big deal.", e.getMessage());
        }
    }

    @SubscribeEvent
    public static void entityLeaveLevel(EntityLeaveLevelEvent event) {
        try {
            if (event.getLevel() instanceof ServerLevel serverLevel
                    && event.getEntity() instanceof AbstractHorse horse) {
                HorsemanServer.getSummoning().onHorseUnloaded(serverLevel, horse);
            }
        } catch (Exception e) {
            Horseman.LOGGER.warn("Cannot handle Horseman's entityLeaveLevel event. {}. But it shouldn't be a big deal.", e.getMessage());
        }
    }
}
