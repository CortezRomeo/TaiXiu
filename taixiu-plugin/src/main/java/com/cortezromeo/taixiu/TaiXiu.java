package com.cortezromeo.taixiu;

import com.cortezromeo.taixiu.api.server.VersionSupport;
import com.cortezromeo.taixiu.command.TaiXiuAdminCommand;
import com.cortezromeo.taixiu.command.TaiXiuCommand;
import com.cortezromeo.taixiu.file.GeyserFormFile;
import com.cortezromeo.taixiu.file.inventory.TaiXiuInfoInventoryFile;
import com.cortezromeo.taixiu.language.English;
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
import com.cortezromeo.taixiu.support.Support;
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
    public static VersionSupport nms;
    public static Support support;

    @Override
    public void onLoad() {
        plugin = this;
        nms = new CrossVersionSupport(plugin);
    }
    @Override
    public void onEnable() {
        support = new Support();
        support.setupSupports();

        initFile();
        initLanguages();
        setDebug(getConfig().getBoolean("debug"));
        initDatabase();
        initCommand();
        initListener();

        TaiXiuManager.startTask(getConfig().getInt("task.taiXiuTask.time-per-session"));
        AutoSaveManager.startAutoSave(getConfig().getInt("database.auto-save.time"));
        BossBarManager.setupValue();

        log("&f--------------------------------");
        log("&2  _____           _    __  __  _         ");
        log("&2 |_   _|   __ _  (_)   \\ \\/ / (_)  _   _ ");
        log("&2   | |    / _  | | |    \\  /  | | | | | |");
        log("&2   | |   | (_| | | |    /  \\  | | | |_| |");
        log("&2   |_|    \\____| |_|   /_/\\_\\ |_|  \\____|");
        log("");
        log("&fVersion: &b" + getDescription().getVersion());
        log("&fAuthor: &bCortez_Romeo");
        log("&eRunning version: " + Bukkit.getServer().getClass().getName().split("\\.")[3]);
        if (support.isFoliaLibSupported())
            log("      &2&lFOLIA SUPPORTED");
        log("");
        log("&fSupport:");
        log((support.isVaultSupported() ? "&2[SUPPORTED] &aVault" : "&4[UNSUPPORTED] &cVault"));
        log((support.isPlaceholderAPISupported() ? "&2[SUPPORTED] &aPlaceholderAPI" : "&4[UNSUPPORTED] &cPlaceholderAPI"));
        log((support.isFloodgateSupported() ? "&2[SUPPORTED] &aFloodgate API (Forms and Cumulus)" : "&4[UNSUPPORTED] &cFloodgate API (Forms and Cumulus)"));
        if (!getConfig().getBoolean("floodgate-settings.enabled"))
            log("  &e&oquyền sử dụng Floodgate API đã bị tắt trong config.yml");
        log((support.isPlayerPointsSupported() ? "&2[SUPPORTED] &aPlayerPoints" : "&4[UNSUPPORTED] &cPlayerPoints"));
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

        // messages_en.yml
        String englishFileName = "messages_en.yml";
        English.setup();
        English.saveDefault();
        File englishFile = new File(getDataFolder(), "/languages/messages_en.yml");
        try {
            ConfigUpdater.update(this, englishFileName, englishFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        English.reload();

        // load locale from config.yml
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
