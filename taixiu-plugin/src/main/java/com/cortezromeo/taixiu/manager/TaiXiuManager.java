package com.cortezromeo.taixiu.manager;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.CurrencyTyppe;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.TaiXiuState;
import com.cortezromeo.taixiu.api.event.PlayerBetEvent;
import com.cortezromeo.taixiu.api.event.SessionResultEvent;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.enums.SoundType;
import com.cortezromeo.taixiu.language.Messages;
import com.cortezromeo.taixiu.support.DiscordSupport;
import com.cortezromeo.taixiu.support.VaultSupport;
import com.cortezromeo.taixiu.task.TaiXiuTask;
import com.cortezromeo.taixiu.util.MessageUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.cortezromeo.taixiu.manager.DebugManager.debug;
import static com.cortezromeo.taixiu.util.MessageUtil.sendBroadCast;
import static com.cortezromeo.taixiu.util.MessageUtil.sendMessage;

public class TaiXiuManager {

    private static TaiXiuTask taiXiuTask = null;

    public static TaiXiuTask getTaiXiuTask() {
        return taiXiuTask;
    }

    public static void startTask(int time) {
        taiXiuTask = new TaiXiuTask(time);
    }

    public static TaiXiuState getState() {
        return getTaiXiuTask().getState();
    }

    public static void setState(TaiXiuState state) {
        getTaiXiuTask().setState(state);
    }

    public static int getTime() {
        return getTaiXiuTask().getTime();
    }

    public static void setTime(int time) {
        getTaiXiuTask().setTime(time);
        BossBarManager.timePerSession = time;
    }

    public static void setCurrencyType(CurrencyTyppe currencyType) {
        getTaiXiuTask().getSession().setCurrencyType(currencyType);
    }

    public static ISession getSessionData() {
        return getTaiXiuTask().getSession();
    }

    public static void setSessionData(ISession sessionData) {
        getTaiXiuTask().setSession(sessionData.getSession());
    }

    public static ISession getSessionData(long session) {
        if (DatabaseManager.getSessionData(session) != null)
            return DatabaseManager.getSessionData(session);

        return null;
    }

    public static void playerBet(Player player, long money, TaiXiuResult result) {
        String pName = player.getName();
        Economy econ = VaultSupport.getEcon();
        ISession data = getSessionData();
        FileConfiguration cfg = TaiXiu.plugin.getConfig();

        if (getSessionData().getXiuPlayers().containsKey(pName) || data.getTaiPlayers().containsKey(pName)) {
            sendMessage(player, Messages.ALREADY_BET
                    .replace("%bet%", MessageUtil.getFormatResultName((data.getXiuPlayers().containsKey(pName)
                            ? TaiXiuResult.XIU
                            : TaiXiuResult.TAI)))
                    .replace("%currencyName%", MessageUtil.getCurrencyName(data.getCurrencyType()))
                    .replace("%currencySymbol%", MessageUtil.getCurrencySymbol(data.getCurrencyType()))
                    .replace("%money%", (data.getXiuPlayers().containsKey(pName)
                            ? MessageUtil.getFormatMoneyDisplay(data.getXiuPlayers().get(pName))
                            : MessageUtil.getFormatMoneyDisplay(data.getTaiPlayers().get(pName)))));
            return;
        }

        int configDisableTime = cfg.getInt("bet-settings.disable-while-remaining");
        if (TaiXiuManager.getTime() <= configDisableTime) {
            sendMessage(player, Messages.LATE_BET
                    .replace("%time%", String.valueOf(TaiXiuManager.getTime()))
                    .replace("%configDisableTime%", String.valueOf(configDisableTime)));
            return;
        }

        boolean notEnougCurrency = false;
        if (data.getCurrencyType() == CurrencyTyppe.VAULT) {
            if (econ.getBalance(player) < money)
                notEnougCurrency = true;
        } else if (data.getCurrencyType() == CurrencyTyppe.PLAYERPOINTS)
            if (TaiXiu.getPlayerPointsAPI().look(player.getUniqueId()) < (int) money)
                notEnougCurrency = true;

        if (notEnougCurrency) {
            sendMessage(player, Messages.NOT_ENOUGH_CURRENCY
                    .replace("%currencyName%", MessageUtil.getCurrencyName(data.getCurrencyType()))
                    .replace("%currencySymbol%", MessageUtil.getCurrencySymbol(data.getCurrencyType())));
            return;
        }

        long minBet = cfg.getLong("bet-settings.min-bet");
        if (money < minBet) {
            sendMessage(player, Messages.MIN_BET
                    .replace("%currencyName%", MessageUtil.getCurrencyName(data.getCurrencyType()))
                    .replace("%currencySymbol%", MessageUtil.getCurrencySymbol(data.getCurrencyType()))
                    .replace("%minBet%", MessageUtil.getFormatMoneyDisplay(minBet)));
            return;
        }

        long maxBet = cfg.getLong("bet-settings.max-bet");
        if (money > maxBet) {
            sendMessage(player, Messages.MAX_BET
                    .replace("%currencyName%", MessageUtil.getCurrencyName(data.getCurrencyType()))
                    .replace("%currencySymbol%", MessageUtil.getCurrencySymbol(data.getCurrencyType()))
                    .replace("%maxBet%", MessageUtil.getFormatMoneyDisplay(maxBet)));
            return;
        }

        if (data.getCurrencyType() == CurrencyTyppe.VAULT)
            econ.withdrawPlayer(player, money);
        else if (data.getCurrencyType() == CurrencyTyppe.PLAYERPOINTS)
            TaiXiu.getPlayerPointsAPI().take(player.getUniqueId(), (int) money);

        if (result == TaiXiuResult.XIU)
            data.addXiuPlayer(pName, money);

        if (result == TaiXiuResult.TAI)
            data.addTaiPlayer(pName, money);

        sendMessage(player, Messages.PLAYER_BET
                .replace("%bet%", MessageUtil.getFormatResultName(result))
                .replace("%money%", MessageUtil.getFormatMoneyDisplay(money))
                .replace("%session%", String.valueOf(data.getSession()))
                .replace("%currencyName%", MessageUtil.getCurrencyName(data.getCurrencyType()))
                .replace("%currencySymbol%", MessageUtil.getCurrencySymbol(data.getCurrencyType()))
                .replace("%time%", String.valueOf(TaiXiuManager.getTime())));

        String messageBroadcastPlayerBet = Messages.BROADCAST_PLAYER_BET
                .replace("%prefix%", Messages.PREFIX)
                .replace("%player%", player.getName())
                .replace("%bet%", MessageUtil.getFormatResultName(result))
                .replace("%currencyName%", MessageUtil.getCurrencyName(data.getCurrencyType()))
                .replace("%currencySymbol%", MessageUtil.getCurrencySymbol(data.getCurrencyType()))
                .replace("%money%", MessageUtil.getFormatMoneyDisplay(money));

        if (!TaiXiu.isPapiSupported())
            Bukkit.broadcastMessage(TaiXiu.nms.addColor(messageBroadcastPlayerBet));
        else
            Bukkit.broadcastMessage(TaiXiu.nms.addColor(PlaceholderAPI.setPlaceholders(player, messageBroadcastPlayerBet)));

        PlayerBetEvent event = new PlayerBetEvent(player, result, money);
        Bukkit.getServer().getPluginManager().callEvent(event);

        debug("PLAYER BETTED",
                "Name: " + pName + " " +
                        "| Bet: " + result.toString() + " " +
                        "| Money: " + money + " " +
                        "| Session: " + data.getSession());

        // discord web hook
        if (TaiXiu.getDiscordSupport() != null) {
            try {
                TaiXiu.getDiscordSupport().sendMessage(TaiXiu.getDiscordSupport().getPlayerBetMessageFromJSON(TaiXiu.plugin.getDataFolder() + "/discordsrv-playerbet-message.json", data, player, result, money));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void resultSeason(@NotNull ISession session, int dice1, int dice2, int dice3) {
        debug("RESULTING SESSION", "Session number " + session.getSession());

        if (session.getResult() != TaiXiuResult.NONE) {
            debug("RESULTING SESSION [CANCELED]", "Session number " + session.getSession() + " không thể trao kết quả vì đã có kết quả");
            return;
        }

        FileConfiguration cfg = TaiXiu.plugin.getConfig();

        if (dice1 == 0)
            dice1 = ThreadLocalRandom.current().nextInt(1, 6 + 1);
        session.setDice1(dice1);
        if (dice2 == 0)
            dice2 = ThreadLocalRandom.current().nextInt(1, 6 + 1);
        session.setDice2(dice2);
        if (dice3 == 0)
            dice3 = ThreadLocalRandom.current().nextInt(1, 6 + 1);
        session.setDice3(dice3);

        int total = dice1 + dice2 + dice3;

        if (total == 3 && cfg.getBoolean("bet-settings.disable-special")) {
            dice3++;
            total++;
            session.setDice3(dice3);
        }

        if (total == 18 && cfg.getBoolean("bet-settings.disable-special")) {
            dice3--;
            total--;
            session.setDice3(dice3);
        }

        if (total >= 4 && total <= 10) {
            session.setResult(TaiXiuResult.XIU);
        } else if (total >= 11 && total <= 17) {
            session.setResult(TaiXiuResult.TAI);
        } else {
            session.setResult(TaiXiuResult.SPECIAL);
        }

        try {
            for (String string : Messages.SESSION_RESULT) {
                string = string.replace("%session%", String.valueOf(session.getSession()));
                string = string.replace("%dice1%", String.valueOf(session.getDice1()));
                string = string.replace("%dice2%", String.valueOf(session.getDice2()));
                string = string.replace("%dice3%", String.valueOf(session.getDice3()));
                string = string.replace("%total%", String.valueOf(total));
                string = string.replace("%result%", MessageUtil.getFormatResultName(session.getResult()));
                string = string.replace("%bestWinners%", getBestWinner(session));

                sendBroadCast(string);
            }

            double tax = cfg.getDouble("bet-settings.tax") / 100;

            if (session.getResult() == TaiXiuResult.XIU && session.getXiuPlayers() != null) {
                executeWinners(session, session.getXiuPlayers(), session.getResult(), tax);

                for (String taiPlayer : session.getTaiPlayers().keySet()) {
                    String message = Messages.SESSION_PLAYER_LOSE
                            .replace("%result%", MessageUtil.getFormatResultName(TaiXiuResult.TAI))
                            .replace("%currencyName%", MessageUtil.getCurrencyName(session.getCurrencyType()))
                            .replace("%currencySymbol%", MessageUtil.getCurrencySymbol(session.getCurrencyType()))
                            .replace("%money%", MessageUtil.getFormatMoneyDisplay(session.getTaiPlayers().get(taiPlayer)));

                    playSound(Bukkit.getPlayer(taiPlayer), SoundType.lose);
                    sendMessage(Bukkit.getPlayer(taiPlayer), message);
                }
            } else if (session.getResult() == TaiXiuResult.TAI && session.getTaiPlayers() != null) {
                executeWinners(session, session.getTaiPlayers(), session.getResult(), tax);

                for (String xiuPlayer : session.getXiuPlayers().keySet()) {
                    String message = Messages.SESSION_PLAYER_LOSE
                            .replace("%result%", MessageUtil.getFormatResultName(TaiXiuResult.XIU))
                            .replace("%currencyName%", MessageUtil.getCurrencyName(session.getCurrencyType()))
                            .replace("%currencySymbol%", MessageUtil.getCurrencySymbol(session.getCurrencyType()))
                            .replace("%money%", MessageUtil.getFormatMoneyDisplay(session.getXiuPlayers().get(xiuPlayer)));

                    playSound(Bukkit.getPlayer(xiuPlayer), SoundType.lose);
                    sendMessage(Bukkit.getPlayer(xiuPlayer), message);
                }
            } else
                sendBroadCast(Messages.SESSION_SPECIAL_WIN
                        .replace("%currencyName%", MessageUtil.getCurrencyName(session.getCurrencyType()))
                        .replace("%currencySymbol%", MessageUtil.getCurrencySymbol(session.getCurrencyType())));

            SessionResultEvent event = new SessionResultEvent(session);
            TaiXiu.plugin.getServer().getScheduler().runTask(TaiXiu.plugin, () -> TaiXiu.plugin.getServer().getPluginManager().callEvent(event));

            debug("SESSION RESULTED", "Session: " + session.getSession() + " " +
                    "| Dice1: " + dice1 + " " +
                    "| Dice2: " + dice2 + " " +
                    "| Dice3: " + dice3 + " " +
                    "| Result: " + session.getResult());
        } catch (Exception e) {
            resultSeason(session, dice1, dice2, dice3);
            MessageUtil.throwErrorMessage("<taixiumanager.java<resultSeason>>" + e);
        }

        // discord web hook
        if (TaiXiu.getDiscordSupport() != null) {
            try {
                TaiXiu.getDiscordSupport().sendMessage(TaiXiu.getDiscordSupport().getResultMessageFromJSON(TaiXiu.plugin.getDataFolder() + "/discordsrv-result-message.json", session));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private static void executeWinners(ISession sessionData, @NotNull HashMap<String, Long> players, TaiXiuResult result, double tax) {
        for (String player : players.keySet()) {
            long money = players.get(player) * 2;
            String message;

            if (tax > 0) {
                money = players.get(player) + Math.round(players.get(player) - (players.get(player) * tax));
                message = Messages.SESSION_PLAYER_WIN_WITH_TAX
                        .replace("%result%", MessageUtil.getFormatResultName(result))
                        .replace("%currencyName%", MessageUtil.getCurrencyName(sessionData.getCurrencyType()))
                        .replace("%currencySymbol%", MessageUtil.getCurrencySymbol(sessionData.getCurrencyType()))
                        .replace("%money%", MessageUtil.getFormatMoneyDisplay(money))
                        .replace("%tax%", String.valueOf(tax * 100));
            } else {
                message = Messages.SESSION_PLAYER_WIN
                        .replace("%result%", MessageUtil.getFormatResultName(result))
                        .replace("%currencyName%", MessageUtil.getCurrencyName(sessionData.getCurrencyType()))
                        .replace("%currencySymbol%", MessageUtil.getCurrencySymbol(sessionData.getCurrencyType()))
                        .replace("%money%", MessageUtil.getFormatMoneyDisplay(money));
            }
            playSound(Bukkit.getPlayer(player), SoundType.win);
            sendMessage(Bukkit.getPlayer(player), message);
            if (sessionData.getCurrencyType() == CurrencyTyppe.VAULT)
                VaultSupport.getEcon().depositPlayer(player, money);
            else if (sessionData.getCurrencyType() == CurrencyTyppe.PLAYERPOINTS)
                TaiXiu.getPlayerPointsAPI().give(Bukkit.getPlayer(player).getUniqueId(), (int) money);
        }
    }

    private static void playSound(Player player, SoundType soundType) {
        if (player == null)
            return;

        if (TaiXiu.plugin.getConfig().getBoolean("sound." + soundType + ".enabled")) {
            player.playSound(player.getLocation(),
                    TaiXiu.nms.createSound(TaiXiu.plugin.getConfig().getString("sound." + soundType + ".sound-name")),
                    TaiXiu.plugin.getConfig().getInt("sound." + soundType + ".volume"),
                    TaiXiu.plugin.getConfig().getInt("sound." + soundType + ".pitch"));
        }
    }


    public static Long getXiuBet(@NotNull ISession session) {
        long sum = 0L;
        if (session.getXiuPlayers() != null) {
            for (long value : session.getXiuPlayers().values()) {
                sum += value;
            }
        }
        return sum;
    }

    public static String getXiuBetFormat(@NotNull ISession session) {
        return MessageUtil.getFormatMoneyDisplay(getXiuBet(session));
    }

    public static Long getTaiBet(@NotNull ISession session) {
        long sum = 0L;
        if (session.getTaiPlayers() != null) {
            for (long value : session.getTaiPlayers().values()) {
                sum += value;
            }
        }
        return sum;
    }

    public static String getTaiBetFormat(@NotNull ISession session) {
        return MessageUtil.getFormatMoneyDisplay(getTaiBet(session));
    }

    public static Long getTotalBet(@NotNull ISession session) {
        return getXiuBet(session) + getTaiBet(session);
    }

    public static String getBestWinner(@NotNull ISession session) {
        TaiXiuResult result = session.getResult();

        try {
            if (result == TaiXiuResult.NONE) {
                return Messages.RESULT_PLAYER_FORMAT_INVALID;
            }

            if (result == TaiXiuResult.SPECIAL) {
                return Messages.RESULT_PLAYER_FORMAT_VALID_SPECIAL
                        .replace("%currencyName%", MessageUtil.getCurrencyName(session.getCurrencyType()))
                        .replace("%currencySymbol%", MessageUtil.getCurrencySymbol(session.getCurrencyType()))
                        .replace("%allBet%", MessageUtil.getFormatMoneyDisplay(getTotalBet(session)));
            }

            Map<String, Long> bestWinners = result == TaiXiuResult.XIU ? session.getXiuPlayers() : session.getTaiPlayers();
            if (bestWinners.isEmpty())
                return Messages.RESULT_PLAYER_FORMAT_INVALID;
            Long bestWinnersBet = Collections.max(bestWinners.values());

            List<String> players = new ArrayList<>();
            for (Map.Entry<String, Long> entry : bestWinners.entrySet())
                if (entry.getValue() >= bestWinnersBet)
                    players.add(entry.getKey());

            String delim = Messages.RESULT_PLAYER_FORMAT_PLAYER_DELIM;
            String bestWinnersName = String.join(delim, players);

            return Messages.RESULT_PLAYER_FORMAT_VALID
                    .replace("%playerName%", bestWinnersName)
                    .replace("%currencyName%", MessageUtil.getCurrencyName(session.getCurrencyType()))
                    .replace("%currencySymbol%", MessageUtil.getCurrencySymbol(session.getCurrencyType()))
                    .replace("%bet%", MessageUtil.getFormatMoneyDisplay(bestWinnersBet * 2));
        } catch (Exception e) {
            return Messages.RESULT_PLAYER_FORMAT_INVALID;
        }
    }

    public void stopTask() {
        getTaiXiuTask().cancel();
    }

}
