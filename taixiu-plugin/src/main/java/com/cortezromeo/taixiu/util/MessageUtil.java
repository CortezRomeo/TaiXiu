package com.cortezromeo.taixiu.util;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.file.MessageFile;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MessageUtil {

    public static String getFormatName(@NotNull TaiXiuResult result) {

        FileConfiguration messageF = MessageFile.get();

        if (result.toString().equals("XIU"))
            return messageF.getString("xiu-name");

        if (result.toString().equals("TAI"))
            return messageF.getString("tai-name");

        if (result.toString().equals("SPECIAL"))
            return messageF.getString("special-name");

        if (result.toString().equals("NONE"))
            return messageF.getString("none-name");

        return null;
    }

    public static void sendBoardCast(String message) {

        if (message.equals(""))
            return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (DatabaseManager.togglePlayers.contains(p.getName()))
                sendMessage(p, message);
        }
    }

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(TaiXiu.nms.addColor(message));
    }

    public static void sendMessage(Player player, String message) {

        if (player == null | message.equals(""))
            return;

        player.sendMessage(TaiXiu.nms.addColor(message.replace("%prefix%", MessageFile.get().getString("prefix"))));
    }
}
