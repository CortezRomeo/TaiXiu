package com.cortezromeo.taixiu.util;

import com.cortezromeo.taixiu.TaiXiu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandUtil {

    private static final String MATCH = "(?ium)^(tell:|message:|player:|op:|console:|)(.*)$";

    public static void dispatchCommand(Player player, String command) {
        TaiXiu.support.getFoliaLib().getScheduler().runAtEntity(player, task -> {
            final String type = command.replaceAll(MATCH, "$1").replace(":", "").toLowerCase();
            final String cmd = command.replaceAll(MATCH, "$2").replaceAll("(?ium)([{]Player[}])", player.getName());
            switch (type) {
                case "op":
                    if (player.isOp()) {
                        player.performCommand(cmd);
                    } else {
                        player.setOp(true);
                        player.performCommand(cmd);
                        player.setOp(false);
                    }
                    break;
                case "":
                case "player":
                    player.performCommand(cmd);
                    break;
                case "tell":
                case "message":
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', cmd));
                    break;
                case "console":
                default:
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    break;
            }
        });
    }
}
