package com.cortezromeo.taixiu.listener;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private TaiXiu plugin;

    public PlayerQuitListener(TaiXiu plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Player p = event.getPlayer();
        if (DatabaseManager.togglePlayers.contains(p.getName())) {
            DatabaseManager.togglePlayers.remove(p.getName());
        }

    }
}