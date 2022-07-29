package com.cortezromeo.taixiu.file;

import com.cortezromeo.taixiu.TaiXiu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class HeadDatabaseFile {

    private static File file;
    private static FileConfiguration headDatabaseFile;

    public static void setup() {
        file = new File(TaiXiu.plugin.getDataFolder() + "/headdatabase.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        headDatabaseFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return headDatabaseFile;
    }

    public static void fileExists() {
        file = new File(TaiXiu.plugin.getDataFolder() + "/headdatabase.yml");
        headDatabaseFile = YamlConfiguration.loadConfiguration(file);
    }

    public static void save() {
        try {
            headDatabaseFile.save(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        headDatabaseFile = YamlConfiguration.loadConfiguration(file);
    }

}
