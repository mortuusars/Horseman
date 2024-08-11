package io.github.mortuusars.horse_please.integration.kubejs;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.script.ScriptType;
import io.github.mortuusars.horse_please.integration.kubejs.event.ExposureJSEvents;
import io.github.mortuusars.horse_please.integration.kubejs.event.FrameAddedEventJS;
import io.github.mortuusars.horse_please.integration.kubejs.event.ModifyFrameDataEventJS;
import io.github.mortuusars.horse_please.integration.kubejs.event.ShutterOpeningEventJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HorsePleaseJSPlugin extends KubeJSPlugin {
    @Override
    public void init() {
        subscribeToEvents();
    }

    @ExpectPlatform
    public static void subscribeToEvents() {
        throw new AssertionError();
    }

    @Override
    public void registerEvents() {
        ExposureJSEvents.register();
    }

    public static boolean fireShutterOpeningEvent(Player player, ItemStack cameraStack, int lightLevel, boolean shouldFlashFire) {
        EventResult result = ExposureJSEvents.SHUTTER_OPENING.post(player.level().isClientSide ? ScriptType.CLIENT : ScriptType.SERVER,
                new ShutterOpeningEventJS(player, cameraStack, lightLevel, shouldFlashFire));
        return result.interruptTrue() || result.interruptFalse() || result.interruptDefault();
    }

    public static void fireModifyFrameDataEvent(ServerPlayer player, ItemStack cameraStack, CompoundTag frame, List<Entity> entitiesInFrame) {
        ExposureJSEvents.MODIFY_FRAME_DATA.post(ScriptType.SERVER, new ModifyFrameDataEventJS(player, cameraStack, frame, entitiesInFrame));
    }

    public static void fireFrameAddedEvent(ServerPlayer player, ItemStack cameraStack, CompoundTag frame) {
        ExposureJSEvents.FRAME_ADDED.post(ScriptType.SERVER, new FrameAddedEventJS(player, cameraStack, frame));
    }
}
