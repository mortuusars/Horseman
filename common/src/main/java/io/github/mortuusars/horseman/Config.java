package io.github.mortuusars.horseman;

import io.github.mortuusars.horseman.world.LeavesCollisionMode;
import io.github.mortuusars.horseman.world.summoning.SummonDimensionHandling;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Collections;
import java.util.List;

/**
 * Using ForgeConfigApiPort on fabric allows using forge config in both environments and without extra dependencies on forge.
 */
public class Config {
    public static class Server {
        public static final ModConfigSpec SPEC;

        // Movement
        public static final ModConfigSpec.DoubleValue HORSE_STEP_HEIGHT_MODIFIER;
        public static final ModConfigSpec.BooleanValue HORSE_FAST_STEP_DOWN;
        public static final ModConfigSpec.BooleanValue HORSE_FAST_STEP_DOWN_TWO_BLOCKS;
        public static final ModConfigSpec.BooleanValue INCREASE_HORSE_AIRBORNE_SPEED;
        public static final ModConfigSpec.DoubleValue INCREASE_HORSE_AIRBORNE_SPEED_AMOUNT;
        public static final ModConfigSpec.BooleanValue ROTATE_HORSE_INSTEAD_OF_PLAYER;
        public static final ModConfigSpec.IntValue SADDLED_HORSE_WANDER_RADIUS;
        // public static final ModConfigSpec.BooleanValue HORSE_PREVENT_REARING_WHEN_RIDING;

        // Hitching
        public static final ModConfigSpec.BooleanValue HORSE_HITCH;
        public static final ModConfigSpec.BooleanValue HORSE_HITCH_REQUIRES_LEAD;
        public static final ModConfigSpec.BooleanValue HORSE_HITCH_INVENTORY_SLOT;

        // Summoning
        public static final ModConfigSpec.BooleanValue COPPER_HORN_ENABLED;
        public static final ModConfigSpec.IntValue COPPER_HORN_SOUND_RANGE;
        public static final ModConfigSpec.IntValue COPPER_HORN_COOLDOWN;
        public static final ModConfigSpec.BooleanValue COPPER_HORN_FAIL_SOUND;
        public static final ModConfigSpec.IntValue HORSE_SUMMONING_MAX_DISTANCE;
        public static final ModConfigSpec.DoubleValue HORSE_SUMMONING_MAX_WALKING_DISTANCE;
        public static final ModConfigSpec.DoubleValue HORSE_SUMMONING_WALK_MOVEMENT_SPEED;
        public static final ModConfigSpec.EnumValue<SummonDimensionHandling> HORSE_SUMMONING_DIMENSION_HANDLING;
        public static final ModConfigSpec.ConfigValue<List<? extends String>> HORSE_SUMMONING_DIMENSIONS;
        public static final ModConfigSpec.BooleanValue COPPER_HORN_ERROR_MESSAGES;

        // Free Camera
        public static final ModConfigSpec.BooleanValue HORSE_FREE_CAMERA;
        public static final ModConfigSpec.DoubleValue HORSE_FREE_CAMERA_ANGLE_THRESHOLD;

        // Misc
        public static final ModConfigSpec.BooleanValue HORSE_ALLOW_TAMING_WITH_ITEM_IN_HAND;
        public static final ModConfigSpec.BooleanValue HORSE_SHEARS_REMOVE_CHEST;
        public static final ModConfigSpec.BooleanValue HORSE_CREATIVE_TAMING;
        public static final ModConfigSpec.BooleanValue HORSE_IN_BOAT;
        public static final ModConfigSpec.DoubleValue MOUNTED_BLOCK_BREAK_SPEED_MODIFIER;
        public static final ModConfigSpec.EnumValue<LeavesCollisionMode> LEAVES_COLLISION_MODE_WHEN_MOUNTED;
        public static final ModConfigSpec.BooleanValue DISMOUNT_TOWARDS_VIEW_DIRECTION;
        public static final ModConfigSpec.BooleanValue FIX_RUNNING_BACK_AFTER_DISMOUNT;
        public static final ModConfigSpec.BooleanValue LEATHER_HORSE_ARMOR_POWDER_SNOW;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            {
                builder.push("movement");

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

                ROTATE_HORSE_INSTEAD_OF_PLAYER = builder
                      .comment("When mounting a horse, rotate it to match player looking direction, instead of rotating the player. Default: true")
                      .define("rotate_horse_instead_of_player", true);

                SADDLED_HORSE_WANDER_RADIUS = builder
                      .comment("Max distance (in blocks) from last dismount position that saddled horse can wander to. Set to -1 for vanilla behavior. Default: 16")
                      .defineInRange("saddled_horse_wander_radius", 16, -1, 64);

                // Removed for the time being as I'm not sure if it is needed that much. Horse doesn't seem to be rearing when riding anyway.
//                HORSE_PREVENT_REARING_WHEN_RIDING = builder
//                      .comment("Prevents rearing (horse stopping and standing up) when it's being ridden. Default: true.")
//                      .define("ridden_horse_prevent_rearing", true);

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
                builder.push("summoning");

                COPPER_HORN_ENABLED = builder
                      .comment("Copper Horn item is enabled. If set to false, item will not be craftable and usable.", "Default: true")
                      .define("copper_horn_enabled", true);

                COPPER_HORN_SOUND_RANGE = builder
                      .comment("Range in blocks where Copper Horn tooting sound can be heard by other players. Goat Horn has range of 256.")
                      .defineInRange("copper_horn_sound_range", 256, 16, 256);

                COPPER_HORN_COOLDOWN = builder
                      .comment("Cooldown in ticks after using a Copper Horn.")
                      .defineInRange("copper_horn_cooldown", 200, 1, Integer.MAX_VALUE);

                COPPER_HORN_FAIL_SOUND = builder
                      .comment("Copper Horn will play a different sound if summoning has failed. Set to 'false' to play usual sound regardless of outcome.", "Default: true")
                      .define("copper_horn_fail_sound", true);

                COPPER_HORN_ERROR_MESSAGES = builder
                      .comment("When summoning/binding a horse with Copper Horn fails, error message will show above the hotbar.")
                      .define("copper_horn_error_messages", true);

                HORSE_SUMMONING_MAX_DISTANCE = builder
                      .comment("Furthest distance in blocks from a player where a horse can still be summoned.",
                            "Set to -1 to allow any distance.")
                      .defineInRange("max_summoning_distance", -1, -1, Integer.MAX_VALUE);

                HORSE_SUMMONING_MAX_WALKING_DISTANCE = builder
                      .comment("Furthest distance in blocks from a player from where a horse will walk instead of teleporting.",
                            "Set to '0' to always teleport.",
                            "Default: 32")
                      .defineInRange("max_walking_distance", 32.0, 0.0, 1024.0);

                HORSE_SUMMONING_WALK_MOVEMENT_SPEED = builder
                      .comment("Horse movement speed when it walks to the player when summoned.")
                      .defineInRange("walk_movement_speed", 2, 0.1, 2.5);

                HORSE_SUMMONING_DIMENSION_HANDLING = builder
                      .comment("Summoning behavior depending on dimension.",
                            "ANY: can summon in any dimension.",
                            "SAME: can only summon when horse is in the same dimension as the player.",
                            "WHITELIST: can only summon when the player is in one of the dimensions defined in 'dimensions' list.",
                            "BLACKLIST: can only summon when the player is NOT in one of the dimensions defined in 'dimensions' list.",
                            "Default: ANY")
                      .defineEnum("dimension_handling", SummonDimensionHandling.ANY);

                HORSE_SUMMONING_DIMENSIONS = builder
                      .comment("Dimensions whitelist or blacklist, depending on the 'dimension_handling' setting.",
                            "Format: [\"minecraft:overworld\", \"minecraft:the_end\"]",
                            "Default: [] (empty)")
                      .defineListAllowEmpty("dimensions", Collections.emptyList(), () -> "modid:dimension_id", o -> {
                          if (o instanceof String str) {
                              try {
                                  Identifier.parse(str);
                                  return true;
                              } catch (Exception ignored) {
                              }
                          }
                          return false;
                      });

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

                HORSE_ALLOW_TAMING_WITH_ITEM_IN_HAND = builder
                      .comment("Taming a horse does not require an empty hand. Default: false")
                      .define("allow_taming_horse_with_item_in_hand", false);

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

                LEAVES_COLLISION_MODE_WHEN_MOUNTED = builder
                      .comment("Controls collision of leaves block when riding a horse.",
                            "FULL: full collision, same as in vanilla.",
                            "IGNORE_LOWEST_BLOCK: lowest block of leaves will have no collision.",
                            "IGNORE_ALL: no collision in all leaves blocks.",
                            "Default: IGNORE_LOWEST_BLOCK (enough to make riding through forests viable).")
                      .defineEnum("leaves_collision_mode_when_mounted", LeavesCollisionMode.IGNORE_LOWEST_BLOCK);

                DISMOUNT_TOWARDS_VIEW_DIRECTION = builder
                      .comment("Dismounting from a horse will place a player towards their view direction. Default: true")
                      .define("dismount_towards_view_direction", true);

                FIX_RUNNING_BACK_AFTER_DISMOUNT = builder
                      .comment("Fixes horse running back after player dismounts it (occurs when horse was leashed before mounting). Default: true")
                      .define("fix_running_back_after_dismount", true);

                LEATHER_HORSE_ARMOR_POWDER_SNOW = builder
                      .comment("Horse with Leather Horse Armor equipped can walk on a Powder Snow like a player with Leather Boots. Default: true")
                      .define("leather_horse_armor_powder_snow", true);

                builder.pop();
            }

            SPEC = builder.build();
        }
    }

    public static class Common {
        public static final ModConfigSpec SPEC;

        public static final ModConfigSpec.BooleanValue HORSE_STATS_TOOLTIP;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            HORSE_STATS_TOOLTIP = builder
                  .comment("Tooltip with horse stats will be shown when looking at a horse when holding a food item that animal likes.")
                  .define("horse_stats_tooltip", true);

            SPEC = builder.build();
        }
    }

    public static class Client {
        public static final ModConfigSpec SPEC;

        public static final ModConfigSpec.BooleanValue IMPROVED_MOUNT_GUI;

        public static final ModConfigSpec.BooleanValue FIX_MOUNTED_CAMERA_LAG;

        public static final ModConfigSpec.BooleanValue PREVENT_JUMPING_IN_WATER;

        public static final ModConfigSpec.IntValue HORSE_HEAD_PITCH_OFFSET;
        public static final ModConfigSpec.IntValue HORSE_HEAD_Y_OFFSET;

        public static final ModConfigSpec.BooleanValue HORSE_HITCH_RENDER_LEAD_WITHOUT_SLOT;

        // Inventory Switch
        public static final ModConfigSpec.BooleanValue INVENTORY_SWITCH_ENABLED;
        public static final ModConfigSpec.IntValue INVENTORY_SWITCH_PLAYER_BUTTON_X;
        public static final ModConfigSpec.IntValue INVENTORY_SWITCH_PLAYER_BUTTON_Y;
        public static final ModConfigSpec.IntValue INVENTORY_SWITCH_HORSE_BUTTON_X;
        public static final ModConfigSpec.IntValue INVENTORY_SWITCH_HORSE_BUTTON_Y;

        // Transparent Mount
        public static final ModConfigSpec.BooleanValue TRANSPARENT_MOUNT_ENABLED;
        public static final ModConfigSpec.BooleanValue TRANSPARENT_MOUNT_ONLY_HORSES;
        public static final ModConfigSpec.DoubleValue TRANSPARENT_MOUNT_MAX_OPACITY;
        public static final ModConfigSpec.IntValue TRANSPARENT_MOUNT_START_ANGLE;
        public static final ModConfigSpec.IntValue TRANSPARENT_MOUNT_END_ANGLE;

        public static final ModConfigSpec.BooleanValue COPPER_HORN_SHOW_TOOLTIP_DETAILS;

        // Integration
        public static final ModConfigSpec.BooleanValue SHOW_JEI_INFORMATION;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            IMPROVED_MOUNT_GUI = builder
                  .comment("Adjusts gui to render hunger bar, xp bar and xp level. Makes horse jump bar render only when jumping.", "Default: true")
                  .define("improved_mount_gui", true);

            FIX_MOUNTED_CAMERA_LAG = builder
                  .comment("Fixes small delay when moving camera horizontally while mounted - MC-259512.", "Default: true")
                  .define("fix_mounted_camera_lag", true);

            PREVENT_JUMPING_IN_WATER = builder
                  .comment("Prevents horse jump meter from filling up when mount is in the water.", "Default: true")
                  .define("prevent_jumping_in_water", true);

            HORSE_HEAD_PITCH_OFFSET = builder
                  .comment("Offset to horse model head pitch while riding. Lowers the head so it's not blocking the view.")
                  .defineInRange("horse_model_head_offset", 20, 0, 45);

            HORSE_HEAD_Y_OFFSET = builder
                  .comment("Offset to horse model head y position while riding. Lowers the head so it's not blocking the view.", "Default: true")
                  .defineInRange("horse_model_y_offset", 2, 0, 4);

            HORSE_HITCH_RENDER_LEAD_WITHOUT_SLOT = builder
                  .comment("If Lead slot is disabled, but lead is still required for hitching, indication of whether the Lead is equipped will be rendered in Horse inventory screen.", "Default: true")
                  .define("render_lead_indication_without_slot", true);

            {
                builder.push("switch_inventory");

                INVENTORY_SWITCH_ENABLED = builder
                      .comment("Adds button and hotkey to switch between player and horse inventory.", "Default: true")
                      .define("enabled", true);

                INVENTORY_SWITCH_PLAYER_BUTTON_X = builder
                      .comment("X position of the button in player's inventory.")
                      .defineInRange("player_button_position_x", 176, Integer.MIN_VALUE, Integer.MAX_VALUE);
                INVENTORY_SWITCH_PLAYER_BUTTON_Y = builder
                      .comment("Y position of the button in player's inventory.")
                      .defineInRange("player_button_position_y", 9, Integer.MIN_VALUE, Integer.MAX_VALUE);

                INVENTORY_SWITCH_HORSE_BUTTON_X = builder
                      .comment("X position of the button in mount's inventory.")
                      .defineInRange("horse_button_position_x", -14, Integer.MIN_VALUE, Integer.MAX_VALUE);
                INVENTORY_SWITCH_HORSE_BUTTON_Y = builder
                      .comment("Y position of the button in mount's inventory.")
                      .defineInRange("horse_button_position_y", 9, Integer.MIN_VALUE, Integer.MAX_VALUE);

                builder.pop();
            }

            {
                builder.push("transparent_horse");

                TRANSPARENT_MOUNT_ENABLED = builder
                        .comment("Makes ridden mounts transparent depending on player's look angle.", "Default: true")
                        .define("enabled", true);

                TRANSPARENT_MOUNT_ONLY_HORSES = builder
                      .comment("If enabled, only horse-like mobs will be affected by the feature.", "Default: false")
                      .define("only_horses", false);

                TRANSPARENT_MOUNT_MAX_OPACITY = builder
                        .comment("Maximum opacity (at the end angle). 0 - fully transparent. 1 - fully opaque.")
                        .defineInRange("max_opacity", 0.08, 0.0, 1.0);

                TRANSPARENT_MOUNT_START_ANGLE = builder
                        .comment("Angle at which the horse will start to become transparent.")
                        .defineInRange("start_angle", 30, -90, 90);
                TRANSPARENT_MOUNT_END_ANGLE = builder
                        .comment("Angle at which the horse will reach maximum transparency.")
                        .defineInRange("end_angle", 70, -90, 90);

                builder.pop();
            }

            {
                builder.push("integration");

                SHOW_JEI_INFORMATION = builder
                      .comment("Useful information about some items will be shown in JEI description category.", "Default: true")
                      .define("jei_information", true);

                builder.pop();
            }

            COPPER_HORN_SHOW_TOOLTIP_DETAILS = builder
                  .comment("Copper Horn tooltip will show details.", "Default: true")
                  .define("copper_horn_details_tooltip", true);

            SPEC = builder.build();
        }
    }
}
