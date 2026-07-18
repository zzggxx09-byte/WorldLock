package com.example.singleworldmod;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ModConfig {
    public static Configuration config;
    public static int cooldownTicks = 20; // 20 тіків = 1 секунда

    public static void init(File file) {
        config = new Configuration(file);
        syncConfig();
    }

    public static void syncConfig() {
        cooldownTicks = config.getInt(
                "cooldownTicks",
                Configuration.CATEGORY_GENERAL,
                20,
                1,
                600,
                "Скільки тіків має пройти між ударами, щоб удар зарахувався (20 тіків = 1 секунда)."
        );
        if (config.hasChanged()) {
            config.save();
        }
    }
}
