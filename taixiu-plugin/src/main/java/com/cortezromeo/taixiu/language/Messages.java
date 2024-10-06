package com.cortezromeo.taixiu.language;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Messages {

    public static String PREFIX;
    public static String XIU_NAME;
    public static String TAI_NAME;
    public static String SPECIAL_NAME;
    public static String NONE_NAME;
    public static String NO_PERMISSION;
    public static String REQUEST_LOADING;
    public static String WRONG_ARGUMENT;
    public static String WRONG_LONG_INPUT;
    public static String INVALID_SESSION;
    public static String TOGGLE_ON;
    public static String TOGGLE_OFF;
    public static String PLAYER_BET;
    public static String BROADCAST_PLAYER_BET;
    public static String INVALID_BET;
    public static String MIN_BET;
    public static String MAX_BET;
    public static String LATE_BET;
    public static String ALREADY_BET;
    public static String INVALID_CURRENCY;
    public static String NOT_ENOUGH_CURRENCY;
    public static String NOT_ENOUGH_PLAYER;
    public static List<String> SESSION_RESULT = new ArrayList<>();
    public static String RESULT_PLAYER_FORMAT_INVALID;
    public static String RESULT_PLAYER_FORMAT_VALID;
    public static String RESULT_PLAYER_FORMAT_VALID_SPECIAL;
    public static String RESULT_PLAYER_FORMAT_PLAYER_DELIM;
    public static String SESSION_SPECIAL_WIN;
    public static String SESSION_PLAYER_WIN;
    public static String SESSION_PLAYER_WIN_WITH_TAX;
    public static String SESSION_PLAYER_LOSE;
    public static String ALREADY_ON_LAST_PAGE;
    public static List<String> COMMAND_TAIXIU = new ArrayList<>();
    public static List<String> COMMAND_LUATCHOI = new ArrayList<>();
    public static List<String> COMMAND_TAIXIUADMIN = new ArrayList<>();
    public static String COMMAND_TAIXIUADMIN_PREFIX;
    public static String COMMAND_TAIXIUADMIN_RELOAD;
    public static String COMMAND_TAIXIUADMIN_CHANGESTATE;
    public static String COMMAND_TAIXIUADMIN_CHANGESTATE_BROADCAST;
    public static String COMMAND_TAIXIUADMIN_SETTIME;
    public static String COMMAND_TAIXIUADMIN_SETTIME_BROADCAST;
    public static String COMMAND_TAIXIUADMIN_INVALID_INT_INPUT;
    public static String COMMAND_TAIXIUADMIN_SETRESULT;
    public static String COMMAND_TAIXIUADMIN_SETRESULT_BROADCAST;
    public static String COMMAND_TAIXIUADMIN_INVALID_DICE_INPUT;
    public static String COMMAND_TAIXIUADMIN_SETCURRENCY;
    public static String COMMAND_TAIXIUADMIN_SETCURRENCY_INVALID;
    public static String COMMAND_TAIXIUADMIN_UNSUPPORTED_CURRENCY;
    public static String COMMAND_TAIXIUADMIN_SETCURRENCY_BROADCAST;

    public static void setupValue(String locale) {
        locale = locale.toLowerCase();
        File messageFile = new File(TaiXiu.plugin.getDataFolder() + "/languages/messages_" + locale + ".yml");
        FileConfiguration fileConfiguration;
        if (!messageFile.exists()) {
            fileConfiguration = Vietnamese.get();
            MessageUtil.log("&c--------------------------------------");
            MessageUtil.log("    &4LỖI");
            MessageUtil.log("&eLocale &c&l" + locale + "&e không hợp lệ!");
            MessageUtil.log("&eVui lòng kiểm tra config.yml.");
            MessageUtil.log("&eHệ thống sẽ tự động sử dụng ngôn ngữ &b&lVietnamese&e.");
            MessageUtil.log("&c--------------------------------------");
        } else {
            fileConfiguration = YamlConfiguration.loadConfiguration(messageFile);
        }

        PREFIX = fileConfiguration.getString("messages.prefix");
        XIU_NAME = fileConfiguration.getString("messages.xiu-name");
        TAI_NAME = fileConfiguration.getString("messages.tai-name");
        SPECIAL_NAME = fileConfiguration.getString("messages.special-name");
        NONE_NAME = fileConfiguration.getString("messages.none-name");
        NO_PERMISSION = fileConfiguration.getString("messages.no-permission");
        REQUEST_LOADING = fileConfiguration.getString("messages.request-loading");
        WRONG_ARGUMENT = fileConfiguration.getString("messages.wrong-argument");
        WRONG_LONG_INPUT = fileConfiguration.getString("messages.wrong-long-input");
        INVALID_SESSION = fileConfiguration.getString("messages.invalid-session");
        TOGGLE_ON = fileConfiguration.getString("messages.toggle-on");
        TOGGLE_OFF = fileConfiguration.getString("messages.toggle-off");
        PLAYER_BET = fileConfiguration.getString("messages.player-bet");
        BROADCAST_PLAYER_BET = fileConfiguration.getString("messages.broadcast-player-bet");
        INVALID_BET = fileConfiguration.getString("messages.invalid-bet");
        MIN_BET = fileConfiguration.getString("messages.min-bet");
        MAX_BET = fileConfiguration.getString("messages.max-bet");
        LATE_BET = fileConfiguration.getString("messages.late-bet");
        ALREADY_BET = fileConfiguration.getString("messages.already-bet");
        INVALID_CURRENCY = fileConfiguration.getString("messages.invalid-bet");
        NOT_ENOUGH_CURRENCY = fileConfiguration.getString("messages.not-enough-currency");
        NOT_ENOUGH_PLAYER = fileConfiguration.getString("messages.not-enough-player");
        SESSION_RESULT = fileConfiguration.getStringList("messages.session-result");
        RESULT_PLAYER_FORMAT_INVALID = fileConfiguration.getString("messages.result-player-format.invalid");
        RESULT_PLAYER_FORMAT_VALID = fileConfiguration.getString("messages.result-player-format.valid");
        RESULT_PLAYER_FORMAT_VALID_SPECIAL = fileConfiguration.getString("messages.result-player-format.valid-special");
        RESULT_PLAYER_FORMAT_PLAYER_DELIM = fileConfiguration.getString("messages.result-player-format.playerName-delim");
        SESSION_SPECIAL_WIN = fileConfiguration.getString("messages.session-special-win");
        SESSION_PLAYER_WIN = fileConfiguration.getString("messages.session-player-win");
        SESSION_PLAYER_WIN_WITH_TAX = fileConfiguration.getString("messages.session-player-win-with-tax");
        SESSION_PLAYER_LOSE = fileConfiguration.getString("messages.session-player-lose");
        ALREADY_ON_LAST_PAGE = fileConfiguration.getString("messages.already-on-last-page");
        COMMAND_TAIXIU = fileConfiguration.getStringList("messages.command.taixiu.message");
        COMMAND_LUATCHOI = fileConfiguration.getStringList("messages.command.luatchoi.message");
        COMMAND_TAIXIUADMIN = fileConfiguration.getStringList("messages.command.taixiuadmin.message");
        COMMAND_TAIXIUADMIN_PREFIX = fileConfiguration.getString("messages.command.taixiuadmin.prefix");
        COMMAND_TAIXIUADMIN_RELOAD = fileConfiguration.getString("messages.command.taixiuadmin.reload");
        COMMAND_TAIXIUADMIN_CHANGESTATE = fileConfiguration.getString("messages.command.taixiuadmin.changestate");
        COMMAND_TAIXIUADMIN_CHANGESTATE_BROADCAST = fileConfiguration.getString("messages.command.taixiuadmin.changestate-broadcast");
        COMMAND_TAIXIUADMIN_SETTIME = fileConfiguration.getString("messages.command.taixiuadmin.settime");
        COMMAND_TAIXIUADMIN_SETTIME_BROADCAST = fileConfiguration.getString("messages.command.taixiuadmin.settime-broadcast");
        COMMAND_TAIXIUADMIN_INVALID_INT_INPUT = fileConfiguration.getString("messages.command.taixiuadmin.invalid-int-input");
        COMMAND_TAIXIUADMIN_SETRESULT = fileConfiguration.getString("messages.command.taixiuadmin.setresult");
        COMMAND_TAIXIUADMIN_SETRESULT_BROADCAST = fileConfiguration.getString("messages.command.taixiuadmin.setresult-broadcast");
        COMMAND_TAIXIUADMIN_INVALID_DICE_INPUT = fileConfiguration.getString("messages.command.taixiuadmin.invalid-dice-input");
        COMMAND_TAIXIUADMIN_SETCURRENCY = fileConfiguration.getString("messages.command.taixiuadmin.setcurrency");
        COMMAND_TAIXIUADMIN_SETCURRENCY_INVALID = fileConfiguration.getString("messages.command.taixiuadmin.invalid-currency-input");
        COMMAND_TAIXIUADMIN_UNSUPPORTED_CURRENCY = fileConfiguration.getString("messages.command.taixiuadmin.unsupported-currency");
        COMMAND_TAIXIUADMIN_SETCURRENCY_BROADCAST = fileConfiguration.getString("messages.command.taixiuadmin.setcurrency-broadcast");

    }

}
