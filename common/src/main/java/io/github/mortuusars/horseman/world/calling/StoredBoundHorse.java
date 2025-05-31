package io.github.mortuusars.horseman.world.calling;

import io.github.mortuusars.horseman.Horseman;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StoredBoundHorse {
    protected final @Nullable BoundData boundData;
    protected final CompoundTag tag;
    protected final UUID entityUuid;
    protected final ResourceKey<Level> dimension;
    protected final boolean isDead;

    public StoredBoundHorse(@Nullable BoundData boundData, CompoundTag tag, UUID entityUuid, ResourceKey<Level> dimension, boolean isDead) {
        this.boundData = boundData;
        this.tag = tag;
        this.entityUuid = entityUuid;
        this.dimension = dimension;
        this.isDead = isDead;
    }

    public StoredBoundHorse(AbstractHorse horse) {
        this(horse.getHorsemanBoundData(), saveToTag(horse), horse.getUUID(), horse.level().dimension(), horse.isDeadOrDying());
    }

    public static CompoundTag saveToTag(AbstractHorse horse) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", EntityType.getKey(horse.getType()).toString());
        horse.saveWithoutId(tag);
        return tag;
    }

    public @Nullable BoundData getBoundData() {
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

    // --

    public CompoundTag save(CompoundTag tag) {
        if (this.boundData != null) {
            tag.put("BoundData", this.boundData.save(new CompoundTag()));
        }
        tag.put("Tag", this.tag);
        tag.putUUID("EntityUUID", this.entityUuid);
        tag.putString("Dimension", this.dimension.location().toString());
        tag.putBoolean("IsDead", this.isDead);
        return tag;
    }

    public static @Nullable StoredBoundHorse load(CompoundTag tag) {
        try {
            @Nullable BoundData boundData = BoundData.load(tag.getCompound("BoundData"));
            CompoundTag storedTag = tag.getCompound("Tag");
            UUID entityUUID = tag.getUUID("EntityUUID");
            ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("Dimension")));
            boolean isDead = tag.getBoolean("IsDead");
            return new StoredBoundHorse(boundData, storedTag, entityUUID, dimension, isDead);
        } catch (Exception e) {
            Horseman.LOGGER.error("Failed to load StoredBoundHorse: ", e);
            return null;
        }
    }
}
