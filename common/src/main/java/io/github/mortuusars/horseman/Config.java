package io.github.mortuusars.horseman;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Using ForgeConfigApiPort on fabric allows using forge config in both environments and without extra dependencies on forge.
 */
public class Config {
    public static class Common {
        public static final ForgeConfigSpec SPEC;

        // Movement
        public static final ForgeConfigSpec.BooleanValue ROTATE_HORSE_INSTEAD_OF_PLAYER;
        public static final ForgeConfigSpec.BooleanValue FIX_HORSE_MOVED_WRONGLY;
        public static final ForgeConfigSpec.BooleanValue HORSE_FAST_STEP_DOWN;
        public static final ForgeConfigSpec.BooleanValue HORSE_FAST_STEP_DOWN_TWO_BLOCKS;
        public static final ForgeConfigSpec.BooleanValue INCREASE_HORSE_AIRBORNE_SPEED;
        public static final ForgeConfigSpec.DoubleValue INCREASE_HORSE_AIRBORNE_SPEED_AMOUNT;

        // Hitching
        public static final ForgeConfigSpec.BooleanValue HORSE_HITCH;
        public static final ForgeConfigSpec.BooleanValue HORSE_HITCH_INVENTORY_SLOT;

        // Camera
        public static final ForgeConfigSpec.BooleanValue HORSE_FREE_CAMERA;
        public static final ForgeConfigSpec.DoubleValue HORSE_FREE_CAMERA_ANGLE_THRESHOLD;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

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
                    .comment("Right clicking a fence while riding will leash the horse to it. Default: true")
                    .define("horse_hitch", true);

            HORSE_HITCH_INVENTORY_SLOT = builder
                    .comment("Slot for a lead will be added to horse inventory menu. If disabled, horses can be hitched while riding without a lead. Default: true")
                    .define("horse_hitch_lead_slot", true);

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
        public static final ForgeConfigSpec SPEC;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            SPEC = builder.build();
        }
    }
}
