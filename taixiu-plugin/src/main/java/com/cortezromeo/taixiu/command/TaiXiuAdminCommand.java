package com.cortezromeo.taixiu.command;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.CurrencyTyppe;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.TaiXiuState;
import com.cortezromeo.taixiu.file.GeyserFormFile;
import com.cortezromeo.taixiu.file.inventory.TaiXiuInfoInventoryFile;
import com.cortezromeo.taixiu.language.Messages;
import com.cortezromeo.taixiu.manager.AutoSaveManager;
import com.cortezromeo.taixiu.manager.BossBarManager;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import com.cortezromeo.taixiu.manager.TaiXiuManager;
import com.cortezromeo.taixiu.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cortezromeo.taixiu.manager.DebugManager.setDebug;
import static com.cortezromeo.taixiu.util.MessageUtil.sendBroadCast;

public class TaiXiuAdminCommand implements CommandExecutor, TabExecutor {
    private TaiXiu plugin;

    public TaiXiuAdminCommand(TaiXiu plugin) {
        this.plugin = plugin;
        plugin.getCommand("taixiuadmin").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (!sender.hasPermission("taixiu.admin")) {
                sendMessage(sender, Messages.NO_PERMISSION);
                return false;
            }
        }

        if (args.length == 1) {
            switch (args[0]) {
                case "changestate":
                    if (TaiXiuManager.getState() == TaiXiuState.PLAYING) {
                        TaiXiuManager.setState(TaiXiuState.PAUSING);
                    } else {
                        TaiXiuManager.setState(TaiXiuState.PLAYING);
                    }
                    sendMessage(sender, Messages.COMMAND_TAIXIUADMIN_CHANGESTATE.replace("%state%", TaiXiuManager.getState().toString()));
                    sendBroadCast(Messages.COMMAND_TAIXIUADMIN_CHANGESTATE_BROADCAST
                            .replace("%playerName%", sender.getName())
                            .replace("%state%", TaiXiuManager.getState().toString()));
                    return false;
                case "reload":
                    TaiXiu.plugin.reloadConfig();
                    Messages.setupValue(TaiXiu.plugin.getConfig().getString("locale"));
                    GeyserFormFile.reload();
                    DatabaseManager.loadLoadingType();
                    BossBarManager.setupValue();
                    TaiXiuInfoInventoryFile.reload();
                    if (TaiXiu.isFloodgateSupported())
                        TaiXiu.setupGeyserForm();
                    setDebug(TaiXiu.plugin.getConfig().getBoolean("debug"));
                    if (AutoSaveManager.getAutoSaveStatus() && !TaiXiu.plugin.getConfig().getBoolean("database.auto-save.enable")) {
                        AutoSaveManager.stopAutoSave();
                    } else {
                        AutoSaveManager.startAutoSave(TaiXiu.plugin.getConfig().getInt("database.auto-save.time"));
                    }
                    AutoSaveManager.reloadTimeAutoSave();

                    sendMessage(sender, Messages.COMMAND_TAIXIUADMIN_RELOAD);
                    return false;
                default:
                    sendMessage(sender, Messages.WRONG_ARGUMENT);
                    return false;
            }
        }

        if (args.length == 2) {
            switch (args[0]) {
                case "settime":
                    try {
                        int time = Integer.parseInt(args[1]);

                        if (time <= 0) {
                            sendMessage(sender, Messages.COMMAND_TAIXIUADMIN_INVALID_INT_INPUT);
                            return false;
                        }

                        TaiXiuManager.setTime(time);
                        sendMessage(sender, Messages.COMMAND_TAIXIUADMIN_SETTIME.replace("%time%", String.valueOf(time)));
                        sendBroadCast(Messages.COMMAND_TAIXIUADMIN_SETTIME_BROADCAST
                                .replace("%playerName%", sender.getName())
                                .replace("%time%", String.valueOf(time)));
                    } catch (Exception e) {
                        sendMessage(sender, Messages.COMMAND_TAIXIUADMIN_INVALID_INT_INPUT);
                    }
                    return false;
                case "setcurrency":
                    try {
                        CurrencyTyppe currencyTyppe = CurrencyTyppe.valueOf(args[1]);
                        if (currencyTyppe == CurrencyTyppe.PLAYERPOINTS)
                            if (TaiXiu.getPlayerPointsAPI() == null) {
                                sendMessage(sender, Messages.COMMAND_TAIXIUADMIN_UNSUPPORTED_CURRENCY.replace("%currency", currencyTyppe.toString()));
                                return false;
                            }
                        TaiXiuManager.setCurrencyType(currencyTyppe);
                        sendMessage(sender, Messages.COMMAND_TAIXIUADMIN_SETCURRENCY
                                .replace("%currency%", String.valueOf(currencyTyppe))
                                .replace("%currencyName%", MessageUtil.getCurrencyName(currencyTyppe)));
                        sendBroadCast(Messages.COMMAND_TAIXIUADMIN_SETCURRENCY_BROADCAST
                                .replace("%playerName%", sender.getName())
                                .replace("%currency%", String.valueOf(currencyTyppe))
                                .replace("%currencyName%", MessageUtil.getCurrencyName(currencyTyppe)));
                    } catch (Exception e) {
                        sendMessage(sender, Messages.COMMAND_TAIXIUADMIN_SETCURRENCY_INVALID);
                    }
                    return false;
                default:
                    sendMessage(sender, Messages.WRONG_ARGUMENT);
                    return false;
            }
        }

        if (args.length == 4) {
            switch (args[0]) {
                case "setresult":
                    if (TaiXiuManager.getSessionData().getResult() != TaiXiuResult.NONE) {
                        sendMessage(sender, "%prefix%&eVui lòng đợi vài giây và xài lại lệnh này!");
                        return false;
                    }
                    try {
                        int dice1 = Integer.parseInt(args[1]);
                        int dice2 = Integer.parseInt(args[2]);
                        int dice3 = Integer.parseInt(args[3]);

                        if (dice1 < 0 || dice2 <0 || dice3 < 0 || dice1 > 6 || dice2 > 6 || dice3 > 6) {
                            sendMessage(sender, Messages.COMMAND_TAIXIUADMIN_INVALID_DICE_INPUT);
                            return false;
                        }

                        TaiXiuManager.resultSeason(TaiXiuManager.getSessionData(), dice1, dice2, dice3);

                        sendMessage(sender, Messages.COMMAND_TAIXIUADMIN_SETRESULT
                                .replace("%dice1%", String.valueOf(dice1))
                                .replace("%dice2%", String.valueOf(dice2))
                                .replace("%dice3%", String.valueOf(dice3)));
                        sendBroadCast(Messages.COMMAND_TAIXIUADMIN_SETRESULT_BROADCAST
                                .replace("%playerName%", sender.getName())
                                .replace("%dice1%", String.valueOf(dice1))
                                .replace("%dice2%", String.valueOf(dice2))
                                .replace("%dice3%", String.valueOf(dice3)));
                    } catch (Exception e) {
                        sendMessage(sender, Messages.COMMAND_TAIXIUADMIN_INVALID_DICE_INPUT);
                    }
                    return false;
                default:
                    sendMessage(sender, Messages.WRONG_ARGUMENT);
                    return false;
            }
        }

        for (String string : Messages.COMMAND_TAIXIUADMIN) {
            string = string.replace("%version%", TaiXiu.plugin.getDescription().getVersion());
            sendMessage(sender, string);
        }
        return false;
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(TaiXiu.nms.addColor(message.replace("%prefix%", Messages.COMMAND_TAIXIUADMIN_PREFIX)));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("taixiu.admin")) {
                commands.add("reload");
                commands.add("changestate");
                commands.add("settime");
                commands.add("setcurrency");
                commands.add("setresult");
            }
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        if (args.length == 2) {
            if (sender.hasPermission("taixiu.admin")) {
                if (args[0].equalsIgnoreCase("setcurrency")) {
                    commands.add("VAULT");
                    commands.add("PLAYERPOINTS");
                }
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }
        }
        Collections.sort(completions);
        return completions;
    }

}
