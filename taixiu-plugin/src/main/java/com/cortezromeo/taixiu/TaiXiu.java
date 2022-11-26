package com.cortezromeo.taixiu;

import com.cortezromeo.taixiu.api.server.VersionSupport;
import com.cortezromeo.taixiu.command.TaiXiuAdminCommand;
import com.cortezromeo.taixiu.command.TaiXiuCommand;
import com.cortezromeo.taixiu.file.InventoryFile;
import com.cortezromeo.taixiu.file.MessageFile;
import com.cortezromeo.taixiu.listener.JoinListener;
import com.cortezromeo.taixiu.listener.PaneListener;
import com.cortezromeo.taixiu.listener.QuitListener;
import com.cortezromeo.taixiu.manager.AutoSaveManager;
import com.cortezromeo.taixiu.manager.BossBarManager;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import com.cortezromeo.taixiu.manager.TaiXiuManager;
import com.cortezromeo.taixiu.storage.SessionDataStorage;
import com.cortezromeo.taixiu.support.PAPISupport;
import com.cortezromeo.taixiu.support.VaultSupport;
import com.cortezromeo.taixiu.support.version.cross.CrossVersionSupport;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

import static com.cortezromeo.taixiu.manager.DebugManager.setDebug;
import static com.cortezromeo.taixiu.util.MessageUtil.log;

public final class TaiXiu extends JavaPlugin {

    public static TaiXiu plugin;
    private static final String version = Bukkit.getServer().getClass().getName().split("\\.")[3];
    public static VersionSupport nms;
    private boolean serverSoftwareSupport = true;
    private static boolean papiSupport = false;

    @Override
    public void onLoad() {

        plugin = this;
        nms = new CrossVersionSupport(plugin);

    }
    @Override
    public void onEnable() {

        if (!serverSoftwareSupport) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        log("&f--------------------------------");
        log("&a▀▀█▀▀  █▀▀█ ▀█▀   ▀▄ ▄▀ ▀█▀  █  █");
        log("&a  █    █▄▄█  █      █    █   █  █");
        log("&a  █    █  █ ▄█▄   ▄▀ ▀▄ ▄█▄  ▀▄▄▀");
        log("");
        log("&fVersion: &b" + getDescription().getVersion());
        log("&fAuthor: &bCortez_Romeo");
        log("&eKhởi chạy plugin trên phiên bản: " + version);
        log("&f--------------------------------");

        initFile();
        setDebug(getConfig().getBoolean("debug"));

        initDatabase();
        initCommand();
        initListener();
        initSupport();

        TaiXiuManager.startTask(getConfig().getInt("task.taiXiuTask.time-per-session"));
        AutoSaveManager.startAutoSave(getConfig().getInt("database.auto-save.time"));
        BossBarManager.setupValue();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (getConfig().getBoolean("toggle-settings.auto-toggle")) {
                if (!DatabaseManager.togglePlayers.contains(p.getName())) {
                    DatabaseManager.togglePlayers.add(p.getName());
                    BossBarManager.toggleBossBar(p);
                }
            }
        }
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
        String messageFileName = getForCurrentVersion("message.yml", "messagev13.yml");
        MessageFile.setup();
        MessageFile.saveDefault();
        File messageFile = new File(getDataFolder(), "message.yml");
        try {
            ConfigUpdater.update(this, messageFileName, messageFile);
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
    }

    private void initDatabase() {
        DatabaseManager.loadLoadingType();
        SessionDataStorage.init();
    }

    private void initCommand() {
        new TaiXiuCommand(this);
        new TaiXiuAdminCommand(this);
    }

    private void initListener() {
        new PaneListener(this);
        new JoinListener(this);
        new QuitListener(this);
    }

    private void initSupport() {

        // papi
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPISupport().register();
            papiSupport = true;
        }

        // vault
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            log("&cPlugin &bTài Xỉu &ccần thêm plugin &6Vault&c và plugin về &6Economy&c để hoạt động");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            VaultSupport.setup();
        }
    }

    public static boolean PAPISupport() {
        return papiSupport;
    }

    public static String getServerVersion() {
        return version;
    }

    public static String getForCurrentVersion(String v12, String v13) {
        switch (getServerVersion()) {
            case "v1_9_R1":
            case "v1_9_R2":
            case "v1_10_R1":
            case "v1_11_R1":
            case "v1_12_R1":
                return v12;
        }
        return v13;
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

        DatabaseManager.saveDatabase();

        for (Player p : Bukkit.getOnlinePlayers()) {
            BossBar bossBar = BossBarManager.bossBarPlayers.get(p);
            if (bossBar != null)
                bossBar.removeAll();
        }


    }
}
