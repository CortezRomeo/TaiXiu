package com.cortezromeo.taixiu.support;

import com.cortezromeo.taixiu.TaiXiu;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultSupport {

    public static Economy econ;

    public static boolean setup() {
        RegisteredServiceProvider<Economy> rsp = TaiXiu.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

}
