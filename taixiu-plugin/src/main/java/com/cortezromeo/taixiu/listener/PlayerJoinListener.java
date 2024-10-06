package com.cortezromeo.taixiu.listener;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.manager.BossBarManager;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private TaiXiu plugin;

    public PlayerJoinListener(TaiXiu plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player p = event.getPlayer();
        if (TaiXiu.plugin.getConfig().getBoolean("toggle-settings.auto-toggle") && !DatabaseManager.togglePlayers.contains(p.getName())) {
            DatabaseManager.togglePlayers.add(p.getName());
            BossBarManager.toggleBossBar(p);
        }

    }
}