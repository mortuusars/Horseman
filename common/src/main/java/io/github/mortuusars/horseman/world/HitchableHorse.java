package io.github.mortuusars.horseman.world;

import io.github.mortuusars.horseman.Config;
import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.network.Packets;
import io.github.mortuusars.horseman.network.packet.client.SyncHorseDataS2CP;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public interface HitchableHorse {
    ItemStack horseman$getLead();
    void horseman$setLead(ItemStack stack);
    boolean horseman$isHitched();
    void horseman$setHitched(boolean hitched);
    Container horseman$getLeadAccess();

    default boolean horseman$hasLead() {
        return !horseman$getLead().isEmpty();
    }

    default AbstractHorse horseman$asHorse() {
        return ((AbstractHorse) this);
    }

    // --

    static boolean isEnabled() {
        return Config.Server.HORSE_HITCH.get();
    }

    static boolean requiresLead() {
        return Config.Server.HORSE_HITCH_REQUIRES_LEAD.get();
    }

    /**
     * Basically every horse that is saddleable supports hitching. In vanilla the only exception is LLama.
     * Technically we can also use horse#isSaddleable here, but that would hard-limit it.
     * Maybe some mod adds non-saddleable horse that would benefit from hitching.
     */
    static boolean isHitchable(HitchableHorse horse) {
        return horse.horseman$asHorse().isTamed()
                && !horse.horseman$asHorse().isBaby()
                && !horse.horseman$asHorse().getType().is(Horseman.Tags.EntityTypes.CANNOT_BE_HITCHED);
    }

    // --

    static boolean canHitch(HitchableHorse horse) {
        return isEnabled() && isHitchable(horse) && !horse.horseman$asHorse().isLeashed()
                && (!requiresLead() || hasLead(horse));
    }

    static boolean isHitched(HitchableHorse horse) {
        return horse.horseman$isHitched();
    }

    static void setHitched(HitchableHorse horse, boolean hitched) {
        horse.horseman$setHitched(hitched);
    }

    // --

    static ItemStack getLead(HitchableHorse horse) {
        return horse.horseman$getLead();
    }

    static void setLead(HitchableHorse horse, ItemStack leadStack) {
        horse.horseman$setLead(leadStack);
    }

    static boolean hasLead(HitchableHorse horse) {
        return horse.horseman$hasLead();
    }

    // -- Lead in inventory

    static boolean shouldHaveLeadSlot(HitchableHorse horse) {
        return isEnabled() && requiresLead() && Config.Server.HORSE_HITCH_INVENTORY_SLOT.get() && isHitchable(horse);
    }

    static boolean mayPlaceInLeadSlot(HitchableHorse horse, ItemStack stack) {
        return stack.is(Items.LEAD);
    }

    static boolean isLeadSlotActive(HitchableHorse horse) {
        return !isHitched(horse);
    }

    // --

    static void syncHorseDataToClient(HitchableHorse horse, ServerPlayer player) {
        Packets.sendToClient(new SyncHorseDataS2CP(horse.horseman$asHorse().getId(), getLead(horse), isHitched(horse)), player);
    }

    static void syncHorseDataToTrackingClients(HitchableHorse horse) {
        Packets.sendToPlayersTrackingEntity(horse.horseman$asHorse(), new SyncHorseDataS2CP(
                horse.horseman$asHorse().getId(), getLead(horse), isHitched(horse)));
    }
}
