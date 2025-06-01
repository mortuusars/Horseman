package io.github.mortuusars.horseman;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import io.github.mortuusars.horseman.advancement.HorseCalledTrigger;
import io.github.mortuusars.horseman.world.item.CopperHornItem;
import io.github.mortuusars.horseman.world.item.crafting.recipe.ComponentTransferringRecipe;
import io.github.mortuusars.horseman.world.item.crafting.recipe.serializer.ComponentTransferringRecipeSerializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatFormatter;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Horseman {
    public static final String ID = "horseman";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        Blocks.init();
        BlockEntityTypes.init();
        EntityTypes.init();
        Items.init();
        MenuTypes.init();
        RecipeSerializers.init();
        CriteriaTriggers.init();
        SoundEvents.init();
        ArgumentTypes.init();
    }

    public static void initServer(MinecraftServer server) {
        HorsemanServer.init(server);
    }

    /**
     * Creates resource location in the mod namespace with the given path.
     */
    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    public static class EntityAttributes {
        public static final ResourceLocation MOUNTED_STEP_HEIGHT = Horseman.resource("mounted-step-height");
        public static final ResourceLocation MOUNTED_BREAK_SPEED = Horseman.resource("mounted-break-speed");
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
        public static final Supplier<CopperHornItem> COPPER_HORN = Register.item("copper_horn",
                () -> new CopperHornItem(new Item.Properties().stacksTo(1), InstrumentTags.GOAT_HORNS));

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
        public static final Supplier<RecipeSerializer<?>> COMPONENT_TRANSFERRING = Register.recipeSerializer(
                "component_transferring", () -> new ComponentTransferringRecipeSerializer<>(
                        "component_transferring", "source", ComponentTransferringRecipe::new));

        static void init() {
        }
    }

    public static class SoundEvents {
        private static Supplier<SoundEvent> register(String category, String key) {
            Preconditions.checkState(category != null && !category.isEmpty(), "'category' should not be empty.");
            Preconditions.checkState(key != null && !key.isEmpty(), "'key' should not be empty.");
            String path = category + "." + key;
            return Register.soundEvent(path, () -> SoundEvent.createVariableRangeEvent(Horseman.resource(path)));
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

    public static class CriteriaTriggers {
        public static Supplier<HorseCalledTrigger> HORSE_CALLED = Register.criterionTrigger("horse_called", HorseCalledTrigger::new);

        public static void init() {
        }
    }

    public static class Tags {
        public static class Items {
        }

        public static class Blocks {
        }

        public static class EntityTypes {
            public static final TagKey<EntityType<?>> CANNOT_BE_HITCHED = TagKey.create(Registries.ENTITY_TYPE, resource("cannot_be_hitched"));
            public static final TagKey<EntityType<?>> CANNOT_SWIM = TagKey.create(Registries.ENTITY_TYPE, resource("cannot_swim"));
            public static final TagKey<EntityType<?>> FORBIDS_HORSES = TagKey.create(Registries.ENTITY_TYPE, resource("forbids_horses"));
            public static final TagKey<EntityType<?>> CALLABLE = TagKey.create(Registries.ENTITY_TYPE, resource("callable"));
        }
    }

    public static class ArgumentTypes {
        public static void init() {
        }
    }
}
