package io.github.mortuusars.horseman;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Using ForgeConfigApiPort on fabric allows using forge config in both environments and without extra dependencies on forge.
 */
public class Config {
    public static class Server {
        public static final ModConfigSpec SPEC;

        public static final ModConfigSpec.BooleanValue HORSE_IN_BOAT;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            HORSE_IN_BOAT = builder
                    .comment("Horses are able to fit in boats. Default: true.")
                    .define("horse_in_boat", true);

            SPEC = builder.build();
        }
    }

    public static class Common {
        public static final ModConfigSpec SPEC;

        // Movement
        public static final ModConfigSpec.BooleanValue ROTATE_HORSE_INSTEAD_OF_PLAYER;
        public static final ModConfigSpec.BooleanValue FIX_HORSE_MOVED_WRONGLY;
        public static final ModConfigSpec.BooleanValue HORSE_FAST_STEP_DOWN;
        public static final ModConfigSpec.BooleanValue HORSE_FAST_STEP_DOWN_TWO_BLOCKS;
        public static final ModConfigSpec.BooleanValue INCREASE_HORSE_AIRBORNE_SPEED;
        public static final ModConfigSpec.DoubleValue INCREASE_HORSE_AIRBORNE_SPEED_AMOUNT;

        // Hitching
        public static final ModConfigSpec.BooleanValue HORSE_HITCH;
        public static final ModConfigSpec.BooleanValue HORSE_HITCH_REQUIRES_LEAD;
        public static final ModConfigSpec.BooleanValue HORSE_HITCH_INVENTORY_SLOT;

        public static final ModConfigSpec.BooleanValue HORSE_SHEARS_REMOVE_CHEST;

        // Camera
        public static final ModConfigSpec.BooleanValue HORSE_FREE_CAMERA;
        public static final ModConfigSpec.DoubleValue HORSE_FREE_CAMERA_ANGLE_THRESHOLD;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            builder.push("movement");

            ROTATE_HORSE_INSTEAD_OF_PLAYER = builder
                    .comment("When mounting a horse, rotate it to match player looking direction, instead of rotating the player. Default: true")
                    .define("rotate_horse_instead_of_player", true);

            FIX_HORSE_MOVED_WRONGLY = builder
                    .comment("Fix horse jitter and reset back when riding up blocks (especially stairs) Mojang bug: MC-100830. Default: true")
                    .define("fix_horse_moved_wrongly", true);

            HORSE_FAST_STEP_DOWN = builder
                    .comment("Make horse step down one block faster by adding downwards velocity.",
                            "Reduces slowdown when riding off a block, making riding much smoother. Default: true")
                    .define("horse_fast_step_down", true);

            HORSE_FAST_STEP_DOWN_TWO_BLOCKS = builder
                    .comment("Makes 'horse_fast_step_down' work when stepping down two blocks.",
                             "Makes fast step down work properly for steep staircases. Default: false")
                    .define("horse_fast_step_down_two_blocks", false);

            INCREASE_HORSE_AIRBORNE_SPEED = builder
                    .comment("Make horse airborne speed faster. Improves jumping distance (horizontal) and speed walking off heights. Default: true")
                    .define("increase_horse_airborne_speed", true);

            INCREASE_HORSE_AIRBORNE_SPEED_AMOUNT = builder
                    .comment("Controls how much 'increase_horse_airborne_speed' increases over vanilla.",
                            "0 - vanilla speed",
                            "1 - full speed (same as running on the ground)",
                            "Note: there is still small initial slowdown when running off a block (start falling), after which speed increases to proper value.",
                            "Default: 0.5")
                    .defineInRange("increase_horse_airborne_speed_amount", 0.5, 0.0, 1.0);

            builder.pop();


            builder.push("hitching");

            HORSE_HITCH = builder
                    .comment("Right-clicking a fence while riding will leash the horse to it. Default: true")
                    .define("horse_hitch", true);

            HORSE_HITCH_REQUIRES_LEAD = builder
                    .comment("Hitching requires Lead item to be present on a horse. If disabled, 'horse_hitch_lead_slot' will be disabled as well. Default: true")
                    .define("horse_hitch_lead_required", true);

            HORSE_HITCH_INVENTORY_SLOT = builder
                    .comment("Slot for a lead will be added to horse inventory menu. Default: true",
                            "If disabled, Lead should be added by Sneak+Right-Clicking a Horse with an item. Default: true")
                    .define("horse_hitch_lead_slot", true);

            builder.pop();

            builder.push("misc");

            HORSE_SHEARS_REMOVE_CHEST = builder
                    .comment("Right-clicking a Mule, Donkey or Llama that has chest with shears will remove the chest and drop its items. Default: true")
                    .define("shears_remove_chest", true);

            builder.pop();

            builder.push("free_camera");

            HORSE_FREE_CAMERA = builder
                    .comment("While horse is stationary - allow moving camera freely, without rotating the horse. Default: true")
                    .define("horse_stationary_free_camera", true);

            HORSE_FREE_CAMERA_ANGLE_THRESHOLD = builder
                    .comment("Threshold in degrees after which horse will follow player rotation. Default: 65")
                    .defineInRange("horse_stationary_free_camera_angle_threshold", 65.0, 0.0, 180.0);

            builder.pop();

            SPEC = builder.build();
        }
    }

    public static class Client {
        public static final ModConfigSpec SPEC;

        public static final ModConfigSpec.IntValue HORSE_HEAD_PITCH_OFFSET;
        public static final ModConfigSpec.IntValue HORSE_HEAD_Y_OFFSET;

        public static final ModConfigSpec.BooleanValue HORSE_HITCH_RENDER_LEAD_WITHOUT_SLOT;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            HORSE_HEAD_PITCH_OFFSET = builder
                    .comment("Offset to horse model head pitch while riding. Lowers the head so it's not blocking the view. Default: 20")
                    .defineInRange("horse_model_head_offset", 20, 0, 45);

            HORSE_HEAD_Y_OFFSET = builder
                    .comment("Offset to horse model head y position while riding. Lowers the head so it's not blocking the view. Default: true")
                    .defineInRange("horse_model_y_offset", 2, 0, 4);

            HORSE_HITCH_RENDER_LEAD_WITHOUT_SLOT = builder
                    .comment("If Lead slot is disabled, but lead is still required for hitching, indication of whether the Lead is equipped will be rendered in Horse inventory screen. Default: true")
                    .define("render_lead_indication_without_slot", true);

            SPEC = builder.build();
        }
    }
}
