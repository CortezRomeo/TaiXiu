package com.cortezromeo.taixiu.command;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.geyserform.MenuGeyserForm;
import com.cortezromeo.taixiu.inventory.TaiXiuInfoInventory;
import com.cortezromeo.taixiu.language.Messages;
import com.cortezromeo.taixiu.manager.BossBarManager;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import com.cortezromeo.taixiu.manager.TaiXiuManager;
import com.cortezromeo.taixiu.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cortezromeo.taixiu.util.MessageUtil.sendMessage;

public class TaiXiuCommand implements CommandExecutor, TabExecutor {

    private TaiXiu plugin;

    public TaiXiuCommand(TaiXiu plugin) {
        this.plugin = plugin;
        plugin.getCommand("taixiu").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Console uses /taixiuad");
            return false;
        } else {
            if (!sender.hasPermission("taixiu.use")) {
                sendMessage((Player) sender, Messages.NO_PERMISSION);
                return false;
            }
        }

        Player p = (Player) sender;
        FileConfiguration cfg = TaiXiu.plugin.getConfig();

        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "rules":
                case "rule":
                case "luatchoi":
                    for (String string : Messages.COMMAND_LUATCHOI) {
                        string = string.replace("%minBet%", MessageUtil.getFormatMoneyDisplay(cfg.getLong("bet-settings.min-bet")))
                                .replace("%currencyName%", MessageUtil.getCurrencyName(TaiXiuManager.getSessionData().getCurrencyType()))
                                .replace("%currencySymbol%", MessageUtil.getCurrencySymbol(TaiXiuManager.getSessionData().getCurrencyType()))
                                .replace("%maxBet%", MessageUtil.getFormatMoneyDisplay(cfg.getLong("bet-settings.max-bet")));
                        sendMessage(p, string);
                    }
                    return false;
                case "info":
                case "thongtin":
                    new TaiXiuInfoInventory(p, TaiXiuManager.getSessionData()).open();
                    return false;
                case "toggle":
                    List<String> togglePlayers = DatabaseManager.togglePlayers;
                    if (togglePlayers.contains(p.getName())) {
                        togglePlayers.remove(p.getName());
                        sendMessage(p, Messages.TOGGLE_OFF);
                    } else {
                        togglePlayers.add(p.getName());
                        sendMessage(p, Messages.TOGGLE_ON);
                    }
                    BossBarManager.toggleBossBar(p);
                    return false;
                default:
                    sendMessage(p, Messages.WRONG_ARGUMENT);
                    return false;
            }
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "info":
                case "thongtin":
                    Long session;

                    try {
                        session = Long.parseLong(args[1]);
                    } catch (Exception e) {
                        sendMessage(p, Messages.WRONG_LONG_INPUT);
                        return false;
                    }

                    try {
                        if (DatabaseManager.checkExistsFileData(session)) {
                            DatabaseManager.loadSessionData(session);
                            new TaiXiuInfoInventory(p, DatabaseManager.getSessionData(session)).open();
                        } else {
                            if (!DatabaseManager.taiXiuData.containsKey(session)) {
                                sendMessage(p, Messages.INVALID_SESSION.replace("%session%", String.valueOf(session)));
                            }
                        }
                    } catch (Exception e) {
                        MessageUtil.throwErrorMessage("<taixiucommand.java<case<thongtin>>>" + e);
                    }

                    return false;
                default:
                    sendMessage(p, Messages.WRONG_ARGUMENT);
                    return false;
            }
        }

        if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "bet":
                case "cuoc":
                    long money;
                    TaiXiuResult result;
                    args[1] = String.valueOf(args[1]);

                    if (args[1].equals("1") || args[1].equalsIgnoreCase("xỉu") || args[1].equalsIgnoreCase("xiu"))
                        result = TaiXiuResult.XIU;
                    else if (args[1].equals("2") || args[1].equalsIgnoreCase("tài") || args[1].equalsIgnoreCase("tai"))
                        result = TaiXiuResult.TAI;
                    else {
                        sendMessage(p, Messages.INVALID_BET.replace("%bet%", args[1]));
                        return false;
                    }

                    try {
                        money = Long.parseLong(args[2]);
                    } catch (Exception e) {
                        sendMessage(p, Messages.INVALID_CURRENCY
                                .replace("%currencyName%", MessageUtil.getCurrencyName(TaiXiuManager.getSessionData().getCurrencyType()))
                                .replace("%currencySymbol%", MessageUtil.getCurrencySymbol(TaiXiuManager.getSessionData().getCurrencyType())));
                        return false;
                    }
                    TaiXiuManager.playerBet(p, money, result);
                    return false;
                default:
                    sendMessage(p, Messages.WRONG_ARGUMENT);
                    return false;
            }
        }

        if (TaiXiu.support.isFloodgateSupported() && FloodgateApi.getInstance().isFloodgateId(p.getUniqueId())) {
            MenuGeyserForm.openForm(p);
        } else {
            for (String string : Messages.COMMAND_TAIXIU) {
                string = string.replace("%version%", TaiXiu.plugin.getDescription().getVersion());
                sendMessage(p, string);
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            if (Messages.locale.equals("vi")) {
                commands.add("luatchoi");
                commands.add("cuoc");
                commands.add("thongtin");
            } else {
                commands.add("bet");
                commands.add("rules");
                commands.add("info");
            }
            commands.add("toggle");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("cuoc") || args[0].equalsIgnoreCase("bet")) {
                if (Messages.locale.equals("vi")) {
                    commands.add("xiu");
                    commands.add("tai");
                } else {
                    commands.add("low");
                    commands.add("high");
                }
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }

}
