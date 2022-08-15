package com.cortezromeo.taixiu.api.server;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class VersionSupport {
    private final Plugin plugin;

    public VersionSupport(Plugin plugin) {
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public abstract ItemStack createItemStack(String material, int amount, short data);

    public abstract ItemStack getHeadItem(String headValue);

    public abstract ItemStack addCustomData(ItemStack i, String data);

    public abstract String getCustomData(ItemStack i);

    public abstract String addColor(String textToTranslate);

    public abstract String stripColor(String textToStrip);

}
