package com.cortezromeo.taixiu.util;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.file.MessageFile;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class MessageUtil {

    public static String formatMoney(long money) {
        DecimalFormat formatter = new DecimalFormat(TaiXiu.plugin.getConfig().getString("format-money"));
        return formatter.format(money);
    }

    public static String getFormatName(@NotNull TaiXiuResult result) {
        FileConfiguration messageF = MessageFile.get();

        if (result == TaiXiuResult.XIU)
            return messageF.getString("xiu-name");
        if (result == TaiXiuResult.TAI)
            return messageF.getString("tai-name");
        if (result == TaiXiuResult.SPECIAL)
            return messageF.getString("special-name");
        if (result == TaiXiuResult.NONE)
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

    public static void thowErrorMessage(String message) {
        Bukkit.getLogger().severe(message);    
        log("&4&l[TAI XIU ERROR] &c&lNếu lỗi này ảnh hưởng đến trải nghiệm của người chơi, hãy liên hệ mình qua discord: Cortez_Romeo");
    }

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(TaiXiu.nms.addColor(message));
    }

    public static void sendMessage(Player player, String message) {
        if (player == null | message.equals(""))
            return;

        message = message.replace("%prefix%" , MessageFile.get().getString("prefix"));

        if (!TaiXiu.PAPISupport())
            player.sendMessage(TaiXiu.nms.addColor(message));
        else
            player.sendMessage(TaiXiu.nms.addColor(PlaceholderAPI.setPlaceholders(player, message)));
    }
}
