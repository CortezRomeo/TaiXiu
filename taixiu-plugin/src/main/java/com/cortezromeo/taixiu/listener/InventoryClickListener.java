package com.cortezromeo.taixiu.listener;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.inventory.TaiXiuInventoryBase;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryClickListener implements Listener {
    private TaiXiu plugin;

    public InventoryClickListener(TaiXiu plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof TaiXiuInventoryBase) {
            ((TaiXiuInventoryBase) holder).handleMenu(event);
        }
    }
}