package com.cortezromeo.taixiu.language;

import com.cortezromeo.taixiu.TaiXiu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Vietnamese {
    private static File file;
    private static FileConfiguration messageFile;

    public static void setup() {
        file = new File(TaiXiu.plugin.getDataFolder() + "/languages/messages_vi.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        messageFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return messageFile;
    }

    public static void saveDefault() {
        try {
            if (!file.exists()) {
                TaiXiu.plugin.saveResource("messages_vi.yml", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        messageFile = YamlConfiguration.loadConfiguration(file);
    }
}
