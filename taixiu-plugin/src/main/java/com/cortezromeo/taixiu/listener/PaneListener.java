package com.cortezromeo.taixiu.listener;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.inventory.page.PagedPane;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * Listens for click events for the {@link PagedPane}
 */
public class PaneListener implements Listener {
    private TaiXiu plugin;

    public PaneListener(TaiXiu plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof PagedPane) {
            ((PagedPane) holder).onClick(event);
        }
    }
}