package com.cortezromeo.taixiu;

import com.cortezromeo.taixiu.api.server.VersionSupport;
import com.cortezromeo.taixiu.command.TaiXiuAdminCommand;
import com.cortezromeo.taixiu.command.TaiXiuCommand;
import com.cortezromeo.taixiu.file.GeyserFormFile;
import com.cortezromeo.taixiu.file.inventory.TaiXiuInfoInventoryFile;
import com.cortezromeo.taixiu.geyserform.BetGeyserForm;
import com.cortezromeo.taixiu.geyserform.InfoGeyserForm;
import com.cortezromeo.taixiu.geyserform.MenuGeyserForm;
import com.cortezromeo.taixiu.geyserform.RuleGeyserForm;
import com.cortezromeo.taixiu.language.Messages;
import com.cortezromeo.taixiu.language.Vietnamese;
import com.cortezromeo.taixiu.listener.InventoryClickListener;
import com.cortezromeo.taixiu.listener.PlayerJoinListener;
import com.cortezromeo.taixiu.listener.PlayerQuitListener;
import com.cortezromeo.taixiu.manager.AutoSaveManager;
import com.cortezromeo.taixiu.manager.BossBarManager;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import com.cortezromeo.taixiu.manager.TaiXiuManager;
import com.cortezromeo.taixiu.storage.SessionDataStorage;
import com.cortezromeo.taixiu.support.DiscordSupport;
import com.cortezromeo.taixiu.support.PAPISupport;
import com.cortezromeo.taixiu.support.VaultSupport;
import com.cortezromeo.taixiu.support.version.cross.CrossVersionSupport;
import com.tchristofferson.configupdater.ConfigUpdater;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
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
    public static VersionSupport nms;
    public static Economy econ;
    private static boolean papiSupport = false;
    private static boolean floodgateSupport = false;
    private static DiscordSupport discordSupport;
    private static PlayerPointsAPI playerPointsAPI;

    @Override
    public void onLoad() {
        plugin = this;
        nms = new CrossVersionSupport(plugin);
    }
    @Override
    public void onEnable() {
        initFile();
        initLanguages();
        setDebug(getConfig().getBoolean("debug"));
        initSupport();
        initDatabase();
        initCommand();
        initListener();

        TaiXiuManager.startTask(getConfig().getInt("task.taiXiuTask.time-per-session"));
        AutoSaveManager.startAutoSave(getConfig().getInt("database.auto-save.time"));
        BossBarManager.setupValue();

        if (isFloodgateSupported())
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
        log("&eKhởi chạy plugin trên phiên bản: " + Bukkit.getServer().getClass().getName().split("\\.")[3]);
        log("");
        log("&fSupport:");
        log((papiSupport ? "&2[YES] &aPlaceholderAPI" : "&4[NO] &cPlaceholderAPI"));
        log((floodgateSupport ? "&2[YES] &aFloodgate API (Forms and Cumulus)" : "&4[NO] &cFloodgate API (Forms and Cumulus)"));
        if (!getConfig().getBoolean("floodgate-settings.enabled"))
            log("  &e&oquyền sử dụng Floodgate API đã bị tắt trong config.yml");
        log((playerPointsAPI != null ? "&2[YES] &aPlayerPoints" : "&4[NO] &cPlayerPoints"));
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
        File inventoryFolder = new File(getDataFolder() + "/inventories");
        if (!inventoryFolder.exists())
            inventoryFolder.mkdirs();

        File languageFolder = new File(getDataFolder() + "/languages");
        if (!languageFolder.exists())
            languageFolder.mkdirs();

        // config.yml
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(this, "config.yml", configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();

        // inventories/sessioninfoinventory.yml
        String taiXiuInfoInventoryFileName = "sessioninfoinventory.yml";
        TaiXiuInfoInventoryFile.setup();
        TaiXiuInfoInventoryFile.saveDefault();
        File taiXiuInfoInventoryFile = new File(getDataFolder() + "/inventories/sessioninfoinventory.yml");
        try {
            ConfigUpdater.update(this, taiXiuInfoInventoryFileName, taiXiuInfoInventoryFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TaiXiuInfoInventoryFile.reload();

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

    public void initLanguages() {
        // messages_vi.yml
        String vietnameseFileName = "messages_vi.yml";
        Vietnamese.setup();
        Vietnamese.saveDefault();
        File vietnameseFile = new File(getDataFolder(), "/languages/messages_vi.yml");
        try {
            ConfigUpdater.update(this, vietnameseFileName, vietnameseFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Vietnamese.reload();
        Messages.setupValue(getConfig().getString("locale"));
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
        new InventoryClickListener(this);
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
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

        // discordWebhook
        discordSupport = new DiscordSupport();

        // playerpoints
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            playerPointsAPI = PlayerPoints.getInstance().getAPI();
        }
    }

    public static void setupGeyserForm() {
        if (isFloodgateSupported()) {
            InfoGeyserForm.setupValue();
            MenuGeyserForm.setupValue();
            RuleGeyserForm.setupValue();
            BetGeyserForm.setupValue();
        }
    }

    public static boolean isPapiSupported() {
        return papiSupport;
    }

    public static boolean isFloodgateSupported() {
        return floodgateSupport;
    }

    public static DiscordSupport getDiscordSupport() {
        return discordSupport;
    }

    public static PlayerPointsAPI getPlayerPointsAPI() {
        return playerPointsAPI;
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
            if (BossBarManager.bossBarPlayers.containsKey(p)) {
                BossBar bossBar = BossBarManager.bossBarPlayers.get(p);
                if (bossBar != null)
                    bossBar.removeAll();
            }
        }
    }
}
