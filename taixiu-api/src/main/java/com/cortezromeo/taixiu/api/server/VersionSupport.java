package com.cortezromeo.taixiu.api.server;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class VersionSupport {

    private static String name;

    private Plugin plugin;

    public VersionSupport(Plugin plugin, String versionName) {
        name = versionName;
        this.plugin = plugin;
    }

    public static String getName() {
        return name;
    }

    public abstract int getVersion();

    public Plugin getPlugin() {
        return plugin;
    }

    public abstract ItemStack createItemStack(String material, int amount, short data);

    public abstract ItemStack getHeadItem();

    public abstract ItemStack addCustomData(ItemStack i, String data);

    public abstract String getCustomData(ItemStack i);

    public abstract String addColor(String textToTranslate);

    public abstract String stripColor(String textToStrip);

}
