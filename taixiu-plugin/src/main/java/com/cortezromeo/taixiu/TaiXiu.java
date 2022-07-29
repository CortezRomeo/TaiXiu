package com.cortezromeo.taixiu;

import com.cortezromeo.taixiu.command.TaiXiuAdminCommand;
import com.cortezromeo.taixiu.command.TaiXiuCommand;
import com.cortezromeo.taixiu.file.HeadDatabaseFile;
import com.cortezromeo.taixiu.file.InventoryFile;
import com.cortezromeo.taixiu.file.MessageFile;
import com.cortezromeo.taixiu.listener.JoinListener;
import com.cortezromeo.taixiu.listener.PaneListener;
import com.cortezromeo.taixiu.manager.AutoSaveManager;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import com.cortezromeo.taixiu.manager.TaiXiuManager;
import com.cortezromeo.taixiu.storage.SessionDataStorage;
import com.cortezromeo.taixiu.support.VaultSupport;
import com.cortezromeo.taixiu.task.AutoSaveTask;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

import static com.cortezromeo.taixiu.manager.DebugManager.setDebug;
import static com.cortezromeo.taixiu.util.MessageUtil.log;

public final class TaiXiu extends JavaPlugin {

    private AutoSaveTask autoSaveTask = null;
    public static TaiXiu plugin;
    private static TaiXiuManager manager;

    @Override
    public void onEnable() {

        plugin = this;

        log("&f--------------------------------");
        log("&a▀▀█▀▀  █▀▀█ ▀█▀   ▀▄ ▄▀ ▀█▀  █  █");
        log("&a  █    █▄▄█  █      █    █   █  █");
        log("&a  █    █  █ ▄█▄   ▄▀ ▀▄ ▄█▄  ▀▄▄▀");
        log("");
        log("&fVersion: &b" + getDescription().getVersion());
        log("&fAuthor: &bCortez_Romeo");
        log("&f--------------------------------");

        initFile();
        setDebug(getConfig().getBoolean("debug"));

        initDatabase();
        initCommand();
        initListener();
        initSupport();

        manager = new TaiXiuManager();
        getManager().startTask(getConfig().getInt("task.taiXiuTask.time-per-session"));
        AutoSaveManager.startAutoSave(getConfig().getInt("auto-save-database.time"));

    }

    private void initFile() {

        // config.yml
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(this, "config.yml", configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();

        // message.yml
        MessageFile.setup();
        MessageFile.saveDefault();
        File messageFile = new File(getDataFolder(), "message.yml");
        try {
            ConfigUpdater.update(this, "message.yml", messageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MessageFile.reload();

        // inventory.yml
        if (!new File(getDataFolder() + "/inventory.yml").exists()) {
            InventoryFile.setup();
            InventoryFile.setupLang();
        } else
            InventoryFile.fileExists();
        InventoryFile.reload();

        // headdatabase.yml
        if (!new File(getDataFolder() + "/headdatabase.yml").exists()) {
            HeadDatabaseFile.setup();
        } else
            HeadDatabaseFile.fileExists();
        HeadDatabaseFile.reload();

    }

    private void initCommand() {
        new TaiXiuCommand(this);
        new TaiXiuAdminCommand(this);
    }

    private void initListener() {
        new PaneListener(this);
        new JoinListener(this);
    }

    private void initDatabase() {
        SessionDataStorage.init();
        DatabaseManager.loadAllDatabase();
    }

    private void initSupport() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            log("&cPlugin &bTài Xỉu &ccần thêm plugin &6Vault&c và plugin về &6Economy&c để hoạt động");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            VaultSupport.setup();
        }
    }

    public TaiXiuManager getManager() {
        return manager;
    }

    @Override
    public void onDisable() {

        log("&f--------------------------------");
        log("&c▀▀█▀▀  █▀▀█ ▀█▀   ▀▄ ▄▀ ▀█▀  █  █");
        log("&c  █    █▄▄█  █      █    █   █  █");
        log("&c  █    █  █ ▄█▄   ▄▀ ▀▄ ▄█▄  ▀▄▄▀");
        log("");
        log("&fVersion: &b" + getDescription().getVersion());
        log("&fAuthor: &bCortez_Romeo");
        log("&f--------------------------------");

        DatabaseManager.saveAllDatabase();

    }
}
