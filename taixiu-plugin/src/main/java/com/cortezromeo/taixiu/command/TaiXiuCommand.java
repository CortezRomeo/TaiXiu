package com.cortezromeo.taixiu.command;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.event.PlayerBetEvent;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.file.MessageFile;
import com.cortezromeo.taixiu.inventory.page.TaiXiuInfoPagedPane;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import com.cortezromeo.taixiu.manager.TaiXiuManager;
import com.cortezromeo.taixiu.support.VaultSupport;
import com.cortezromeo.taixiu.util.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

import static com.cortezromeo.taixiu.manager.DebugManager.debug;
import static com.cortezromeo.taixiu.util.MessageUtil.sendMessage;

public class TaiXiuCommand implements CommandExecutor {

    private TaiXiu plugin;

    public TaiXiuCommand(TaiXiu plugin) {
        this.plugin = plugin;
        plugin.getCommand("taixiu").setExecutor((CommandExecutor) this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration messageF = MessageFile.get();

        if (!(sender instanceof Player)) {
            sender.sendMessage("Console sử dụng lệnh /taixiuad");
            return false;
        } else {
            if (!sender.hasPermission("taixiu.use") && !sender.hasPermission("taixiu.admin")) {
                sendMessage((Player) sender, messageF.getString("no-permission"));
                return false;
            }
        }

        Player p = (Player) sender;
        String pName = p.getName();
        Economy econ = VaultSupport.econ;
        TaiXiuManager manager = TaiXiu.plugin.getManager();
        ISession data = manager.getSessionData();
        FileConfiguration cfg = TaiXiu.plugin.getConfig();

        if (args.length == 1) {
            switch (args[0]) {
                case "luatchoi":
                    for (String string : messageF.getStringList("luatchoi")) {

                        string = string.replace("%minBet%", String.valueOf(cfg.getLong("bet-settings.min-bet")));
                        string = string.replace("%maxBet%", String.valueOf(cfg.getLong("bet-settings.max-bet")));

                        sendMessage(p, string);
                    }
                    return false;
                case "thongtin":
                    TaiXiuInfoPagedPane.openInventory(p, manager.getSessionData().getSession());
                    return false;
                case "toggle":

                    List<String> togglePlayers = DatabaseManager.togglePlayers;
                    if (togglePlayers.contains(p.getName())) {
                        togglePlayers.remove(p.getName());
                        sendMessage(p, messageF.getString("toggle-off"));
                    } else {
                        togglePlayers.add(p.getName());
                        sendMessage(p, messageF.getString("toggle-on"));
                    }

                    return false;
                default:
                    sendMessage(p, messageF.getString("wrong-argument"));
                    return false;
            }

        }

        if (args.length == 2) {
            switch (args[0]) {
                case "thongtin":
                    Long session;

                    try {
                        session = Long.parseLong(args[1]);
                    } catch (Exception e) {
                        sendMessage(p, messageF.getString("wrong-long-input"));
                        return false;
                    }

                    if (!DatabaseManager.taiXiuData.containsKey(session)) {
                        sendMessage(p, messageF.getString("invalid-session").replace("%session%", String.valueOf(session)));
                        return false;
                    }

                    TaiXiuInfoPagedPane.openInventory(p, session);
                    return false;
                default:
                    sendMessage(p, messageF.getString("wrong-argument"));
                    return false;
            }
        }

        if (args.length == 3) {
            switch (args[0]) {
                case "cuoc":
                    if (data.getXiuPlayers().containsKey(pName) || data.getTaiPlayers().containsKey(pName)) {
                        sendMessage(p, messageF.getString("have-bet-before")
                                .replaceAll("%bet%", MessageUtil.getFormatName((data.getXiuPlayers().containsKey(pName)
                                        ? TaiXiuResult.XIU
                                        : TaiXiuResult.TAI)))
                                .replaceAll("%money%", (data.getXiuPlayers().containsKey(pName)
                                        ? String.valueOf(data.getXiuPlayers().get(pName))
                                        : String.valueOf(data.getTaiPlayers().get(pName)))));
                        return false;
                    }

                    int configDisableTime = cfg.getInt("bet-settings.disable-while-remaining");
                    if (manager.getTime() < configDisableTime) {
                        sendMessage(p, messageF.getString("late-bet")
                                .replaceAll("%time%", String.valueOf(manager.getTime()))
                                .replaceAll("%configDisableTime%", String.valueOf(configDisableTime)));
                        return false;
                    }

                    long money;
                    TaiXiuResult result;
                    args[1] = String.valueOf(args[1]);

                    if (args[1].equals("1") || args[1].equalsIgnoreCase("xỉu") || args[1].equalsIgnoreCase("xiu"))
                        result = TaiXiuResult.XIU;
                    else if (args[1].equals("2") || args[1].equalsIgnoreCase("tài") || args[1].equalsIgnoreCase("tai"))
                        result = TaiXiuResult.TAI;
                    else {
                        sendMessage(p, messageF.getString("invalid-bet").replace("%bet%", args[1]));
                        return false;
                    }

                    try {
                        money = Long.parseLong(args[2]);
                    } catch (Exception e) {
                        sendMessage(p, messageF.getString("invalid-money"));
                        return false;
                    }

                    if (econ.getBalance(p) < money) {
                        sendMessage(p, messageF.getString("not-enough-money"));
                        return false;
                    }

                    long minBet = cfg.getLong("bet-settings.min-bet");
                    if (money < minBet) {
                        sendMessage(p, messageF.getString("min-bet").replace("%minBet%", String.valueOf(minBet)));
                        return false;
                    }

                    long maxBet = cfg.getLong("bet-settings.max-bet");
                    if (money > maxBet) {
                        sendMessage(p, messageF.getString("max-bet").replace("%maxBet%", String.valueOf(maxBet)));
                        return false;
                    }

                    econ.withdrawPlayer(p, money);

                    if (result == TaiXiuResult.XIU)
                        data.addXiuPlayer(pName, money);

                    if (result == TaiXiuResult.TAI)
                        data.addTaiPlayer(pName, money);

                    sendMessage(p, messageF.getString("player-bet")
                            .replaceAll("%bet%", MessageUtil.getFormatName(result))
                            .replaceAll("%money%", String.valueOf(money))
                            .replaceAll("%session%", String.valueOf(data.getSession()))
                            .replaceAll("%time%", String.valueOf(manager.getTime())));

                    PlayerBetEvent event = new PlayerBetEvent(p, result, money);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                    debug("PLAYER BETTED " +
                            ">>> name: " + pName + " " +
                            "| bet: " + result.toString() + " " +
                            "| money: " + money + " " +
                            "| session: " + data.getSession());

                    return false;
                default:
                    sendMessage(p, messageF.getString("wrong-argument"));
                    return false;
            }
        }

        for (String string : messageF.getStringList("command-taixiu")) {
            string = string.replace("%version%", TaiXiu.plugin.getDescription().getVersion());
            sendMessage(p, string);
        }
        return false;
    }
}
