package io.github.mortuusars.horse_please;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Using ForgeConfigApiPort on fabric allows using forge config in both environments and without extra dependencies on forge.
 */
public class Config {
    public static class Common {
        public static final ForgeConfigSpec SPEC;

        public static final ForgeConfigSpec.BooleanValue ROTATE_HORSE_INSTEAD_OF_PLAYER;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            ROTATE_HORSE_INSTEAD_OF_PLAYER = builder
                    .comment("When mounting a horse, rotate it to match player looking direction, instead of rotating the player to match horse direction. Default: true")
                    .define("RotateHorseInsteadOfPlayer", true);

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
