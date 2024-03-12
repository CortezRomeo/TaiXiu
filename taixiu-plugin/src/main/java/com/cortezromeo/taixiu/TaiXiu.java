package com.cortezromeo.taixiu;

import com.cortezromeo.taixiu.api.server.VersionSupport;
import com.cortezromeo.taixiu.command.TaiXiuAdminCommand;
import com.cortezromeo.taixiu.command.TaiXiuCommand;
import com.cortezromeo.taixiu.file.GeyserFormFile;
import com.cortezromeo.taixiu.file.InventoryFile;
import com.cortezromeo.taixiu.file.MessageFile;
import com.cortezromeo.taixiu.geyserform.BetGeyserForm;
import com.cortezromeo.taixiu.geyserform.InfoGeyserForm;
import com.cortezromeo.taixiu.geyserform.MenuGeyserForm;
import com.cortezromeo.taixiu.geyserform.RuleGeyserForm;
import com.cortezromeo.taixiu.listener.JoinListener;
import com.cortezromeo.taixiu.listener.PaneListener;
import com.cortezromeo.taixiu.listener.QuitListener;
import com.cortezromeo.taixiu.manager.*;
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
    private static boolean papiSupport = false;
    private static boolean floodgateSupport = false;
    private static DiscordManager discordManager;

    @Override
    public void onLoad() {
        plugin = this;
        nms = new CrossVersionSupport(plugin);
    }
    @Override
    public void onEnable() {
        initFile();
        setDebug(getConfig().getBoolean("debug"));

        initSupport();
        initDatabase();
        initCommand();
        initListener();

        TaiXiuManager.startTask(getConfig().getInt("task.taiXiuTask.time-per-session"));
        AutoSaveManager.startAutoSave(getConfig().getInt("database.auto-save.time"));
        BossBarManager.setupValue();

        if (floodgateSupport())
            setupGeyserForm();

        log("&f--------------------------------");
        log("&2  _____           _    __  __  _         ");
        log("&2 |_   _|   __ _  (_)   \\ \\/ / (_)  _   _ ");
        log("&2   | |    / _  | | |    \\  /  | | | | | |");
        log("&2   | |   | (_| | | |    /  \\  | | | |_| |");
        log("&2   |_|    \\____| |_|   /_/\\_\\ |_|  \\____|");
        log("");
        log("&fVersion: &b" + getDescription().getVersion());
        log("&fAuthor: &bCortez_Romeo");
        log("&eKhởi chạy plugin trên phiên bản: " + version);
        log("");
        log("&fSupport:");
        log((papiSupport ? "&2[YES] &aPlaceholderAPI" : "&4[NO] &cPlaceholderAPI"));
        log((floodgateSupport ? "&2[YES] &aFloodgate API (Forms and Cumulus)" : "&4[NO] &cFloodgate API (Forms and Cumulus)"));
        if (!getConfig().getBoolean("floodgate-settings.enabled"))
            log("  &e&oquyền sử dụng Floodgate API đã bị tắt trong config.yml");
        log((discordManager != null ? "&2[YES] &aDiscordSRV" : "&4[NO] &cDiscordSRV"));
        log("");
        log("&f--------------------------------");

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (getConfig().getBoolean("toggle-settings.auto-toggle")) {
                if (!DatabaseManager.togglePlayers.contains(p.getName())) {
                    DatabaseManager.togglePlayers.add(p.getName());
                    BossBarManager.toggleBossBar(p);
                }
            }
        }

        if (Metrics.isEnabled())
            new Metrics(this, 21630);
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

        // geyserform.yml
        if (!new File(getDataFolder() + "/geyserform.yml").exists()) {
            GeyserFormFile.setup();
            GeyserFormFile.setupLang();
        } else
            GeyserFormFile.fileExists();
        GeyserFormFile.reload();

        // discordsrv-result-message.json
        File resultJsonFile = new File(getDataFolder(), "discordsrv-result-message.json");
        if (!resultJsonFile.exists()) saveResource(resultJsonFile.getName(), false);

        // discordsrv-playerbet-message.json
        File playerBetJsonFile = new File(getDataFolder(), "discordsrv-playerbet-message.json");
        if (!playerBetJsonFile.exists()) saveResource(playerBetJsonFile.getName(), false);
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
        // vault
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            log("&cPlugin &bTài Xỉu &ccần thêm plugin &6Vault&c và plugin về &6Economy&c để hoạt động");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            VaultSupport.setup();
        }

        // papi
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPISupport().register();
            papiSupport = true;
        }

        // floodgate
        if (Bukkit.getPluginManager().getPlugin("floodgate") != null
                && getConfig().getBoolean("floodgate-settings.enabled")) {
            floodgateSupport = true;
        }

        if (Bukkit.getPluginManager().getPlugin("DiscordSRV") != null
                && getConfig().getBoolean("discordsrv-settings.enabled")) {
            discordManager = new DiscordManager(this);
        }
    }

    public static void setupGeyserForm() {
        if (floodgateSupport()) {
            InfoGeyserForm.setupValue();
            MenuGeyserForm.setupValue();
            RuleGeyserForm.setupValue();
            BetGeyserForm.setupValue();
        }
    }

    public static boolean PAPISupport() {
        return papiSupport;
    }

    public static boolean floodgateSupport() {
        return floodgateSupport;
    }

    public static DiscordManager getDiscordManager() {
        return discordManager;
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
        log("&c  _____           _    __  __  _         ");
        log("&c |_   _|   __ _  (_)   \\ \\/ / (_)  _   _ ");
        log("&c   | |    / _  | | |    \\  /  | | | | | |");
        log("&c   | |   | (_| | | |    /  \\  | | | |_| |");
        log("&c   |_|    \\____| |_|   /_/\\_\\ |_|  \\____|");
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
