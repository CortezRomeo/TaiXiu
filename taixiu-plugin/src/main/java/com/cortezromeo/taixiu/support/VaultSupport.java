package com.cortezromeo.taixiu.support;

import com.cortezromeo.taixiu.TaiXiu;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import static com.cortezromeo.taixiu.util.MessageUtil.log;

public class VaultSupport {

    public static boolean setup() {
        RegisteredServiceProvider<Economy> rsp = TaiXiu.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        TaiXiu.econ = rsp.getProvider();
        return TaiXiu.econ != null;
    }

    public static Economy getEcon() {
        if (TaiXiu.econ == null)
            if (!setup()) {
                log("&cPlugin &bTài Xỉu &ccần thêm plugin &6Vault&c và plugin về &6Economy&c để hoạt động");
                Bukkit.getPluginManager().disablePlugin(TaiXiu.plugin);
            }
        return TaiXiu.econ;
    }

}
