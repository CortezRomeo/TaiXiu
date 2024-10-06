package com.cortezromeo.taixiu.util;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.CurrencyTyppe;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.language.Messages;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class MessageUtil {

    public static String getFormatMoneyDisplay(long money) {
        DecimalFormat formatter = new DecimalFormat(TaiXiu.plugin.getConfig().getString("format-money"));
        return formatter.format(money);
    }

    public static String getFormatResultName(@NotNull TaiXiuResult result) {
        if (result == TaiXiuResult.XIU)
            return Messages.XIU_NAME;
        if (result == TaiXiuResult.TAI)
            return Messages.TAI_NAME;
        if (result == TaiXiuResult.SPECIAL)
            return Messages.SPECIAL_NAME;
        if (result == TaiXiuResult.NONE)
            return Messages.NONE_NAME;
        return null;
    }

    public static String getCurrencyName(CurrencyTyppe currencyTyppe) {
        return TaiXiu.plugin.getConfig().getString("currency-settings.display-settings." + currencyTyppe.toString() + ".name");
    }

    public static String getCurrencySymbol(CurrencyTyppe currencyTyppe) {
        return TaiXiu.plugin.getConfig().getString("currency-settings.display-settings." + currencyTyppe.toString() + ".symbol");
    }

    public static void throwErrorMessage(String message) {
        Bukkit.getLogger().severe(message);
        log("&4&l[TAI XIU ERROR] &c&lNếu lỗi này ảnh hưởng đến trải nghiệm người chơi, liên hệ mình qua discord: Cortez_Romeo");
    }

    public static void sendBroadCast(String message) {
        if (message.equals(""))
            return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            sendMessage(p, message);
        }
    }

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(TaiXiu.nms.addColor(message));
    }

    public static void sendMessage(CommandSender sender, String message) {
        message = message.replace("%prefix%" , Messages.PREFIX);
        sender.sendMessage(TaiXiu.nms.addColor(message));
    }

    public static void sendMessage(Player player, String message) {
        if (player == null | message.equals(""))
            return;

        message = message.replace("%prefix%" , Messages.PREFIX);

        if (!TaiXiu.isPapiSupported())
            player.sendMessage(TaiXiu.nms.addColor(message));
        else
            player.sendMessage(TaiXiu.nms.addColor(PlaceholderAPI.setPlaceholders(player, message)));
    }

    // only use for testing plugin
    public static void devMessage(String message) {
        log("[DEV] " + message);
    }

    public static void devMessage(Player player, String message) {
        player.sendMessage("[DEV] " + message);
    }
}
