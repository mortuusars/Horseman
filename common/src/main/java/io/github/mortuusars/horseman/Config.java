package io.github.mortuusars.horseman;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Using ForgeConfigApiPort on fabric allows using forge config in both environments and without extra dependencies on forge.
 */
public class Config {
    public static class Server {
        public static final ModConfigSpec SPEC;

        // Movement
        public static final ModConfigSpec.BooleanValue FIX_HORSE_MOVED_WRONGLY;
        public static final ModConfigSpec.DoubleValue HORSE_STEP_HEIGHT_MODIFIER;
        public static final ModConfigSpec.BooleanValue HORSE_FAST_STEP_DOWN;
        public static final ModConfigSpec.BooleanValue HORSE_FAST_STEP_DOWN_TWO_BLOCKS;
        public static final ModConfigSpec.BooleanValue INCREASE_HORSE_AIRBORNE_SPEED;
        public static final ModConfigSpec.DoubleValue INCREASE_HORSE_AIRBORNE_SPEED_AMOUNT;
        public static final ModConfigSpec.BooleanValue HORSE_SWIM_WHEN_RIDDEN;
        public static final ModConfigSpec.BooleanValue ROTATE_HORSE_INSTEAD_OF_PLAYER;
        public static final ModConfigSpec.IntValue SADDLED_HORSE_WANDER_RADIUS;
        public static final ModConfigSpec.BooleanValue HORSE_PREVENT_REARING_WHEN_RIDING;

        // Hitching
        public static final ModConfigSpec.BooleanValue HORSE_HITCH;
        public static final ModConfigSpec.BooleanValue HORSE_HITCH_REQUIRES_LEAD;
        public static final ModConfigSpec.BooleanValue HORSE_HITCH_INVENTORY_SLOT;

        // Free Camera
        public static final ModConfigSpec.BooleanValue HORSE_FREE_CAMERA;
        public static final ModConfigSpec.DoubleValue HORSE_FREE_CAMERA_ANGLE_THRESHOLD;

        // Misc
        public static final ModConfigSpec.BooleanValue HORSE_SHEARS_REMOVE_CHEST;
        public static final ModConfigSpec.BooleanValue HORSE_CREATIVE_TAMING;
        public static final ModConfigSpec.BooleanValue HORSE_IN_BOAT;
        public static final ModConfigSpec.DoubleValue MOUNTED_BLOCK_BREAK_SPEED_MODIFIER;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            {
                builder.push("movement");

                FIX_HORSE_MOVED_WRONGLY = builder
                        .comment("Fix horse jitter and reset back when riding up blocks (especially stairs) Mojang bug: MC-100830. Default: true")
                        .define("fix_horse_moved_wrongly", true);

                HORSE_STEP_HEIGHT_MODIFIER = builder
                        .comment("Additional step height added to horses. If set to 1 - horse will be able to step up two blocks. Set to 0 for vanilla behavior. Default 0.1.")
                        .defineInRange("horse_step_height_modifier", 0.1, -1.0, 10);

                HORSE_FAST_STEP_DOWN = builder
                        .comment("Make horse step down one block faster by adding downwards velocity.",
                                "Reduces slowdown when riding off a block, making riding much smoother. Default: true")
                        .define("horse_fast_step_down", true);

                HORSE_FAST_STEP_DOWN_TWO_BLOCKS = builder
                        .comment("Makes 'horse_fast_step_down' work when stepping down two blocks.",
                                "Makes fast step down work better for steep staircases, but may introduce some unwanted behavior elsewhere. Default: false")
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

                HORSE_SWIM_WHEN_RIDDEN = builder
                        .comment("Horse-type mobs are able to swim when ridden by holding a jump key. Exact types that cannot swim can be controlled by '#horseman:cannot_swim' entity tag. Default: true.")
                        .define("horse_swim_when_ridden", true);

                ROTATE_HORSE_INSTEAD_OF_PLAYER = builder
                        .comment("When mounting a horse, rotate it to match player looking direction, instead of rotating the player. Default: true")
                        .define("rotate_horse_instead_of_player", true);

                SADDLED_HORSE_WANDER_RADIUS = builder
                        .comment("Max distance (in blocks) from last dismount position that saddled horse can wander to. Set to -1 for vanilla behavior. Default: 16")
                        .defineInRange("saddled_horse_wander_radius", 16, -1, 64);

                HORSE_PREVENT_REARING_WHEN_RIDING = builder
                        .comment("Prevents rearing (horse stopping and standing up) when it's being ridden. Default: true.")
                        .define("ridden_horse_prevent_rearing", true);

                builder.pop();
            }

            {
                builder.push("hitching");

                HORSE_HITCH = builder
                        .comment("Right-clicking a fence while riding will leash the horse to it. Default: true")
                        .define("enabled", true);

                HORSE_HITCH_REQUIRES_LEAD = builder
                        .comment("Hitching requires Lead item to be present on a horse. If disabled, 'horse_hitch_lead_slot' will be disabled as well. Default: true")
                        .define("requires_lead", true);

                HORSE_HITCH_INVENTORY_SLOT = builder
                        .comment("Slot for a lead will be added to horse inventory menu. Default: true",
                                "If disabled, Lead should be added by Sneak+Right-Clicking a Horse with an item. Default: true")
                        .define("lead_slot", true);

                builder.pop();
            }

            {
                builder.push("free_camera");

                HORSE_FREE_CAMERA = builder
                        .comment("While horse is stationary - allow moving camera freely, without rotating the horse. Default: true")
                        .define("enabled", true);

                HORSE_FREE_CAMERA_ANGLE_THRESHOLD = builder
                        .comment("Threshold in degrees after which horse will follow player rotation. Default: 100")
                        .defineInRange("angle_threshold", 100.0, 0.0, 180.0);

                builder.pop();
            }

            {
                builder.push("misc");

                MOUNTED_BLOCK_BREAK_SPEED_MODIFIER = builder
                        .comment("Additional block breaking speed added when mounted. Set to 0 for vanilla behavior. Default 5 (regular breaking speed).")
                        .defineInRange("mounted_block_break_speed_modifier", 5.0, 0.0, 5.0);

                HORSE_IN_BOAT = builder
                        .comment("Horse-type mobs are able to fit in boats. Default: true.")
                        .define("horse_fits_in_boat", true);

                HORSE_SHEARS_REMOVE_CHEST = builder
                        .comment("Right-clicking a Mule, Donkey or Llama that has chest with shears will remove the chest and drop its items. Default: true")
                        .define("shears_remove_chest", true);

                HORSE_CREATIVE_TAMING = builder
                        .comment("Using Saddle on untamed horse-type mob while in creative mode tames it instantly. Default: true.")
                        .define("horse_creative_taming", true);

                builder.pop();
            }

            SPEC = builder.build();
        }
    }

    public static class Client {
        public static final ModConfigSpec SPEC;

        public static final ModConfigSpec.BooleanValue IMPROVED_MOUNT_GUI;

        public static final ModConfigSpec.BooleanValue PREVENT_JUMPING_IN_WATER;

        public static final ModConfigSpec.IntValue HORSE_HEAD_PITCH_OFFSET;
        public static final ModConfigSpec.IntValue HORSE_HEAD_Y_OFFSET;

        public static final ModConfigSpec.BooleanValue HORSE_HITCH_RENDER_LEAD_WITHOUT_SLOT;

        public static final ModConfigSpec.BooleanValue INVENTORY_TOGGLE_ENABLED;
        public static final ModConfigSpec.IntValue INVENTORY_TOGGLE_PLAYER_BUTTON_X;
        public static final ModConfigSpec.IntValue INVENTORY_TOGGLE_PLAYER_BUTTON_Y;
        public static final ModConfigSpec.IntValue INVENTORY_TOGGLE_HORSE_BUTTON_X;
        public static final ModConfigSpec.IntValue INVENTORY_TOGGLE_HORSE_BUTTON_Y;

        public static final ModConfigSpec.BooleanValue TRANSPARENT_HORSE_ENABLED;
        public static final ModConfigSpec.IntValue TRANSPARENT_HORSE_MAX_TRANSPARENCY;
        public static final ModConfigSpec.IntValue TRANSPARENT_HORSE_START_ANGLE;
        public static final ModConfigSpec.IntValue TRANSPARENT_HORSE_END_ANGLE;

        public static final ModConfigSpec.BooleanValue JEB_HORSE;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            IMPROVED_MOUNT_GUI = builder
                    .comment("Adjusts gui to render hunger bar, xp bar and xp level. Makes horse jump bar render only when jumping. Default: true")
                    .define("improved_mount_gui", true);

            PREVENT_JUMPING_IN_WATER = builder
                    .comment("Prevents horse jump meter from filling up when mount is in the water. Default: true")
                    .define("prevent_jumping_in_water", true);

            HORSE_HEAD_PITCH_OFFSET = builder
                    .comment("Offset to horse model head pitch while riding. Lowers the head so it's not blocking the view. Default: 20")
                    .defineInRange("horse_model_head_offset", 20, 0, 45);

            HORSE_HEAD_Y_OFFSET = builder
                    .comment("Offset to horse model head y position while riding. Lowers the head so it's not blocking the view. Default: true")
                    .defineInRange("horse_model_y_offset", 2, 0, 4);

            HORSE_HITCH_RENDER_LEAD_WITHOUT_SLOT = builder
                    .comment("If Lead slot is disabled, but lead is still required for hitching, indication of whether the Lead is equipped will be rendered in Horse inventory screen. Default: true")
                    .define("render_lead_indication_without_slot", true);

            {
                builder.push("switch_inventory");

                INVENTORY_TOGGLE_ENABLED = builder
                        .comment("Adds button and hotkey to switch between player and horse inventory. Default: true")
                        .define("enabled", true);

                INVENTORY_TOGGLE_PLAYER_BUTTON_X = builder
                        .comment("X position of the button in player's inventory. Default: -14.")
                        .defineInRange("player_button_position_x", -14, Integer.MIN_VALUE, Integer.MAX_VALUE);
                INVENTORY_TOGGLE_PLAYER_BUTTON_Y = builder
                        .comment("Y position of the button in player's inventory. Default: 9.")
                        .defineInRange("player_button_position_y", 9, Integer.MIN_VALUE, Integer.MAX_VALUE);

                INVENTORY_TOGGLE_HORSE_BUTTON_X = builder
                        .comment("X position of the button in mount's inventory. Default: -14.")
                        .defineInRange("horse_button_position_x", -14, Integer.MIN_VALUE, Integer.MAX_VALUE);
                INVENTORY_TOGGLE_HORSE_BUTTON_Y = builder
                        .comment("Y position of the button in mount's inventory. Default: 9.")
                        .defineInRange("horse_button_position_y", 9, Integer.MIN_VALUE, Integer.MAX_VALUE);

                builder.pop();
            }

            {
                builder.push("transparent_horse");

                TRANSPARENT_HORSE_ENABLED = builder
                        .comment("Makes horse translucent depending on player's look angle. Default: true")
                        .define("enabled", true);

                TRANSPARENT_HORSE_MAX_TRANSPARENCY = builder
                        .comment("Maximum transparency (at the end angle). Default: 20.")
                        .defineInRange("max_transparency", 20, 0, 255);

                TRANSPARENT_HORSE_START_ANGLE = builder
                        .comment("Angle at which the horse will start to become transparent. Default: 30.")
                        .defineInRange("start_angle", 30, -90, 90);
                TRANSPARENT_HORSE_END_ANGLE = builder
                        .comment("Angle at which the horse will reach maximum transparency. Default: 70.")
                        .defineInRange("end_angle", 70, -90, 90);

                builder.pop();
            }

            JEB_HORSE = builder
                    .comment("Makes horse-type mobs that named 'jeb_' render with rainbow effect, like sheep. Default: true.")
                    .define("jeb_horse", true);

            SPEC = builder.build();
        }
    }
}
