package com.cortezromeo.taixiu.support;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.geyserform.BetGeyserForm;
import com.cortezromeo.taixiu.geyserform.InfoGeyserForm;
import com.cortezromeo.taixiu.geyserform.MenuGeyserForm;
import com.cortezromeo.taixiu.geyserform.RuleGeyserForm;
import com.cortezromeo.taixiu.util.MessageUtil;
import com.tcoded.folialib.FoliaLib;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import static com.cortezromeo.taixiu.util.MessageUtil.log;

public class Support {

    public PlayerPointsAPI playerPointsAPI;
    public Economy vaultEconomyAPI;
    public DiscordSupport discordSupport;
    public FoliaLib foliaLib;
    public boolean placeholderAPISupported = false;
    public boolean floodgateSupported = false;
    public boolean playerPointsSupported = false;
    public boolean vaultSupported = false;

    public boolean isPlaceholderAPISupported() {
      return placeholderAPISupported;
    }

    public boolean isPlayerPointsSupported() {
        return playerPointsSupported;
    }

    public boolean isVaultSupported() {
        return vaultSupported;
    }

    public boolean isFloodgateSupported() {
        return floodgateSupported;
    }

    public PlayerPointsAPI getPlayerPointsAPI() {
        return playerPointsAPI;
    }

    public DiscordSupport getDiscordSupport() {
        return discordSupport;
    }

    public FoliaLib getFoliaLib() {
        return foliaLib;
    }

    public boolean isFoliaLibSupported() {
        return foliaLib.isFolia();
    }

    public void setupSupports() {
        // FoliaLib
        foliaLib = new FoliaLib(TaiXiu.plugin);

        // Vault
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            setupVault();
        } else {
            log("&cThe &bTai Xiu &cplugin requires the &6Vault&c plugin and an &6Economy&c plugin to function properly.");
            Bukkit.getPluginManager().disablePlugin(TaiXiu.plugin);
            return;
        }

        // PlayerPoints
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            playerPointsAPI = PlayerPoints.getInstance().getAPI();
            playerPointsSupported = true;
        }

        // PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPISupport().register();
            placeholderAPISupported = true;
        }

        // discordWebhook
        discordSupport = new DiscordSupport(TaiXiu.plugin.getConfig().getString("discord-webhook-settings.webhookURL"));

        // floodgate
        if (Bukkit.getPluginManager().getPlugin("floodgate") != null
                && TaiXiu.plugin.getConfig().getBoolean("floodgate-settings.enabled")) {
            floodgateSupported = true;
            setupGeyserForm();
        }
    }

    public boolean setupVault() {
        if (TaiXiu.plugin.getServer().getPluginManager().getPlugin("Vault") == null)
            return false;

        RegisteredServiceProvider<Economy> rsp = TaiXiu.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        vaultEconomyAPI = rsp.getProvider();
        vaultSupported = true;
        return true;
    }

    public Economy getVault() {
        if (vaultEconomyAPI == null)
            if (!setupVault()) {
                MessageUtil.throwErrorMessage("VAULT IS NOT AVAILABLE IN THE SERVER");
                return null;
            }
        return vaultEconomyAPI;
    }

    public void setupGeyserForm() {
        if (isFloodgateSupported()) {
            InfoGeyserForm.setupValue();
            MenuGeyserForm.setupValue();
            RuleGeyserForm.setupValue();
            BetGeyserForm.setupValue();
        }
    }

}
