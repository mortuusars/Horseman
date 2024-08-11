package io.github.mortuusars.horse_please;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatFormatter;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public class HorsePlease {
    public static final String ID = "horse_please";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        Blocks.init();
        BlockEntityTypes.init();
        EntityTypes.init();
        Items.init();
        MenuTypes.init();
        RecipeSerializers.init();
        SoundEvents.init();
        ArgumentTypes.init();
    }

    public static boolean shouldHorseStepDown(AbstractHorse horse) {
        Level level = horse.level();
        BlockPos pos = horse.blockPosition();
        return !horse.onGround()
                && !horse.isJumping()
                && horse.fallDistance > 0f
                && horse.fallDistance < 0.2f
                && !level.getBlockState(pos.below()).getCollisionShape(level, pos.below()).isEmpty()
                || (Config.Common.HORSE_FAST_STEP_DOWN_TWO_BLOCKS.get()
                && !level.getBlockState(pos.below(2)).getCollisionShape(level, pos.below(2)).isEmpty());
    }

    /**
     * Creates resource location in the mod namespace with the given path.
     */
    public static ResourceLocation resource(String path) {
        return new ResourceLocation(ID, path);
    }

    public static class Blocks {
        static void init() {
        }
    }

    public static class BlockEntityTypes {
        static void init() {
        }
    }

    public static class Items {
        static void init() {
        }
    }

    public static class EntityTypes {
        static void init() {
        }
    }

    public static class MenuTypes {
        static void init() {
        }
    }

    public static class RecipeSerializers {
        static void init() {
        }
    }

    public static class SoundEvents {
        private static Supplier<SoundEvent> register(String category, String key) {
            Preconditions.checkState(category != null && !category.isEmpty(), "'category' should not be empty.");
            Preconditions.checkState(key != null && !key.isEmpty(), "'key' should not be empty.");
            String path = category + "." + key;
            return Register.soundEvent(path, () -> SoundEvent.createVariableRangeEvent(HorsePlease.resource(path)));
        }

        static void init() {
        }
    }

    public static class Stats {
        private static final Map<ResourceLocation, StatFormatter> STATS = new HashMap<>();

        private static ResourceLocation register(ResourceLocation location, StatFormatter formatter) {
            STATS.put(location, formatter);
            return location;
        }

        public static void register() {
            STATS.forEach((location, formatter) -> {
                Registry.register(BuiltInRegistries.CUSTOM_STAT, location, location);
                net.minecraft.stats.Stats.CUSTOM.get(location, formatter);
            });
        }
    }

    public static class Advancements {
        public static void register() {
        }
    }

    public static class Tags {
        public static class Items {
        }

        public static class Blocks {
        }
    }

    public static class ArgumentTypes {
        public static void init() {
        }
    }
}
