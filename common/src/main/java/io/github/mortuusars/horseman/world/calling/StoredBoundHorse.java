package io.github.mortuusars.horseman.world.calling;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class StoredBoundHorse {
    protected final BoundData boundData;
    protected final CompoundTag tag;
    protected final UUID entityUuid;
    protected final ResourceKey<Level> dimension;
    protected final boolean isDead;

    public StoredBoundHorse(AbstractHorse horse, boolean dead) {
        boundData = horse.getHorsemanBoundData();
        tag = saveToTag(horse);
        entityUuid = horse.getUUID();
        dimension = horse.level().dimension();
        isDead = dead;
    }

    public static CompoundTag saveToTag(AbstractHorse horse) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", EntityType.getKey(horse.getType()).toString());
        horse.saveWithoutId(tag);
        return tag;
    }

    public BoundData getCallableData() {
        return boundData;
    }

    public CompoundTag getTag() {
        return tag;
    }

    public UUID getEntityUuid() {
        return entityUuid;
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public boolean isDead() {
        return isDead;
    }

    // --

    public boolean isInSameDimension(Player player) {
        return isInSameDimension(player.level().dimension());
    }

    public boolean isInSameDimension(ResourceKey<Level> dimension) {
        return getDimension().equals(dimension);
    }
}
