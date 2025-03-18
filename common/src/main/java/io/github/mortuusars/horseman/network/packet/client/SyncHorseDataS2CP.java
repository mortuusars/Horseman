package io.github.mortuusars.horseman.network.packet.client;

import io.github.mortuusars.horseman.Horseman;
import io.github.mortuusars.horseman.network.PacketDirection;
import io.github.mortuusars.horseman.network.handler.ClientPacketsHandler;
import io.github.mortuusars.horseman.network.packet.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record SyncHorseDataS2CP(int entityId, List<ItemStack> inventory, ItemStack leadStack, boolean isHitched) implements IPacket {
    public static final ResourceLocation ID = Horseman.resource("sync_horse_data");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public FriendlyByteBuf toBuffer(FriendlyByteBuf buffer) {
        buffer.writeVarInt(entityId);
        buffer.writeVarInt(inventory.size());
        for (ItemStack itemStack : inventory) {
            buffer.writeItem(itemStack);
        }
        buffer.writeItem(leadStack);
        buffer.writeBoolean(isHitched);
        return buffer;
    }

    public static SyncHorseDataS2CP fromBuffer(FriendlyByteBuf buffer) {
        int entityId = buffer.readVarInt();
        int inventorySize = buffer.readVarInt();
        List<ItemStack> inventory = new ArrayList<>();
        for (int i = 0; i < inventorySize; i++) {
            inventory.add(buffer.readItem());
        }
        return new SyncHorseDataS2CP(entityId, inventory, buffer.readItem(), buffer.readBoolean());
    }

    @Override
    public boolean handle(PacketDirection direction, @Nullable Player player) {
        ClientPacketsHandler.syncHorseData(this);
        return true;
    }
}
