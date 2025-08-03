package io.github.mortuusars.horseman.world.summoning;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public record StoredBoundHorse(Optional<BoundData> boundData, CompoundTag tag, UUID uuid,
                               Vec3 position, ResourceKey<Level> dimension, boolean isDead) {

    public static final Codec<StoredBoundHorse> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BoundData.CODEC.optionalFieldOf("bound_data").forGetter(StoredBoundHorse::boundData),
            CompoundTag.CODEC.fieldOf("tag").forGetter(StoredBoundHorse::tag),
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(StoredBoundHorse::uuid),
            Vec3.CODEC.fieldOf("position").forGetter(StoredBoundHorse::position),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(StoredBoundHorse::dimension),
            Codec.BOOL.optionalFieldOf("is_dead", false).forGetter(StoredBoundHorse::isDead)
    ).apply(instance, StoredBoundHorse::new));

    public StoredBoundHorse(AbstractHorse horse) {
        this(Optional.ofNullable(horse.getHorsemanBoundData()), saveToTag(horse), horse.getUUID(),
                horse.position(), horse.level().dimension(), horse.isDeadOrDying());
    }

    public static CompoundTag saveToTag(AbstractHorse horse) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", EntityType.getKey(horse.getType()).toString());
        horse.saveWithoutId(tag);
        return tag;
    }

    // --

    public boolean isInSameDimension(Player player) {
        return isInSameDimension(player.level().dimension());
    }

    public boolean isInSameDimension(ResourceKey<Level> dimension) {
        return dimension().equals(dimension);
    }

    // --

//    public CompoundTag save(CompoundTag tag) {
//        if (this.boundData != null) {
//            tag.put("BoundData", this.boundData.save(new CompoundTag()));
//        }
//        tag.put("Tag", this.tag);
//        tag.putUUID("EntityUUID", this.entityUuid);
//        tag.putDouble("PosX", this.position.x);
//        tag.putDouble("PosY", this.position.y);
//        tag.putDouble("PosZ", this.position.z);
//        tag.putString("Dimension", this.dimension.location().toString());
//        tag.putBoolean("IsDead", this.isDead);
//        return tag;
//    }
//
//    public static @Nullable StoredBoundHorse load(CompoundTag tag) {
//        try {
//            @Nullable BoundData boundData = BoundData.load(tag.getCompound("BoundData"));
//            CompoundTag storedTag = tag.getCompound("Tag");
//            UUID entityUUID = tag.getUUID("EntityUUID");
//            Vec3 position = new Vec3(tag.getDouble("PosX"), tag.getDouble("PosY"), tag.getDouble("PosZ"));
//            ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("Dimension")));
//            boolean isDead = tag.getBoolean("IsDead");
//            return new StoredBoundHorse(boundData, storedTag, entityUUID, position, dimension, isDead);
//        } catch (Exception e) {
//            Horseman.LOGGER.error("Failed to load StoredBoundHorse: ", e);
//            return null;
//        }
//    }
}
