package io.github.mortuusars.horse_please;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Using ForgeConfigApiPort on fabric allows using forge config in both environments and without extra dependencies on forge.
 */
public class Config {
    public static class Common {
        public static final ForgeConfigSpec SPEC;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

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
