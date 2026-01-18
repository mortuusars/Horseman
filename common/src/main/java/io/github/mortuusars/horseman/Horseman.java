package io.github.mortuusars.horseman;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import io.github.mortuusars.horseman.advancement.HorseSummonedTrigger;
import io.github.mortuusars.horseman.world.item.CopperHornItem;
import io.github.mortuusars.horseman.world.item.crafting.recipe.ComponentTransferringRecipe;
import io.github.mortuusars.horseman.world.item.crafting.recipe.serializer.ComponentTransferringRecipeSerializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatFormatter;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
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

    /**
     * Creates resource location in the mod namespace with the given path.
     */
    public static Identifier resource(String path) {
        return Identifier.fromNamespaceAndPath(ID, path);
    }

    public static class EntityAttributes {
        public static final Identifier MOUNTED_STEP_HEIGHT = Horseman.resource("mounted-step-height");
        public static final Identifier MOUNTED_BREAK_SPEED = Horseman.resource("mounted-break-speed");
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
                CopperHornItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

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
        public static final Supplier<RecipeSerializer<ComponentTransferringRecipe>> COMPONENT_TRANSFERRING = Register.recipeSerializer(
                "component_transferring", () -> new ComponentTransferringRecipeSerializer<>(
                        "component_transferring", "source", ComponentTransferringRecipe::new));

        static void init() {
        }
    }

    public static class SoundEvents {
        public static final Supplier<SoundEvent> COPPER_HORN_TOOT = register("item", "copper_horn.toot");
        public static final Supplier<SoundEvent> COPPER_HORN_TOOT_FAIL = register("item", "copper_horn.toot_fail");

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
        private static final Map<Identifier, StatFormatter> STATS = new HashMap<>();

        private static Identifier register(Identifier location, StatFormatter formatter) {
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
        public static final Supplier<HorseSummonedTrigger> HORSE_SUMMONED = Register.criterionTrigger("horse_summoned", HorseSummonedTrigger::new);

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
            public static final TagKey<EntityType<?>> FORBIDS_HORSES = TagKey.create(Registries.ENTITY_TYPE, resource("forbids_horses"));
            public static final TagKey<EntityType<?>> SUMMONABLE = TagKey.create(Registries.ENTITY_TYPE, resource("summonable"));
        }
    }

    public static class ArgumentTypes {
        public static void init() {
        }
    }
}
