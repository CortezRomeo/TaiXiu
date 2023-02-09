package com.cortezromeo.taixiu.manager;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.TaiXiuState;
import com.cortezromeo.taixiu.api.event.SessionResultEvent;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.file.MessageFile;
import com.cortezromeo.taixiu.support.VaultSupport;
import com.cortezromeo.taixiu.task.TaiXiuTask;
import com.cortezromeo.taixiu.util.CommandUtil;
import com.cortezromeo.taixiu.util.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.cortezromeo.taixiu.manager.DebugManager.debug;
import static com.cortezromeo.taixiu.util.MessageUtil.sendBoardCast;
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

    public static void resultSeason(@NotNull ISession session, int dice1, int dice2, int dice3) {

        debug("RESULTING SESSION", "Session number " + session.getSession());

        if (session.getResult() != TaiXiuResult.NONE) {
            debug("RESULTING SESSION [CANCELED]", "Session number " + session.getSession() + " không thể trao kết quả vì đã có kết quả");
            return;
        }

        Economy econ = VaultSupport.econ;
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

        FileConfiguration messageF = MessageFile.get();

        try {
            for (String string : messageF.getStringList("session-result")) {
                string = string.replace("%session%", String.valueOf(session.getSession()));
                string = string.replace("%dice1%", String.valueOf(session.getDice1()));
                string = string.replace("%dice2%", String.valueOf(session.getDice2()));
                string = string.replace("%dice3%", String.valueOf(session.getDice3()));
                string = string.replace("%total%", String.valueOf(total));
                string = string.replace("%result%", MessageUtil.getFormatName(session.getResult()));
                string = string.replace("%bestWinners%", getBestWinner(session));

                sendBoardCast(string);
            }

            double tax = cfg.getDouble("bet-settings.tax") / 100;

            if (session.getResult() == TaiXiuResult.XIU && session.getXiuPlayers() != null) {
                giveMoney(session.getXiuPlayers(), session.getResult(), tax);
            } else if (session.getResult() == TaiXiuResult.TAI && session.getTaiPlayers() != null) {
                giveMoney(session.getTaiPlayers(), session.getResult(), tax);
            } else
                sendBoardCast(messageF.getString("session-special-win"));

            SessionResultEvent event = new SessionResultEvent(session);
            TaiXiu.plugin.getServer().getScheduler().runTask(TaiXiu.plugin, () -> TaiXiu.plugin.getServer().getPluginManager().callEvent(event));

            debug("SESSION RESULTED", "Session: " + session.getSession() + " " +
                    "| Dice1: " + dice1 + " " +
                    "| Dice2: " + dice2 + " " +
                    "| Dice3: " + dice3 + " " +
                    "| Result: " + session.getResult());
        } catch (Exception e) {
            resultSeason(session, dice1, dice2, dice3);
            MessageUtil.thowErrorMessage("" + e);
        }
    }

    private static void giveMoney(@NotNull  HashMap<String, Long> players, TaiXiuResult result, double tax) {
        FileConfiguration messageF = MessageFile.get();
        for (String player : players.keySet()) {

            long money = players.get(player) * 2;
            String message;

            if (tax > 0) {
                money = Math.round(money - (money * tax));
                message = messageF.getString("session-player-win-with-tax")
                        .replaceAll("%result%", MessageUtil.getFormatName(result))
                        .replaceAll("%money%", MessageUtil.formatMoney(money))
                        .replaceAll("%tax%", String.valueOf(tax * 100));
            } else {
                message = messageF.getString("session-player-win")
                        .replaceAll("%result%", MessageUtil.getFormatName(result))
                        .replaceAll("%money%", MessageUtil.formatMoney(money));
            }

            sendMessage(Bukkit.getPlayer(player), message);

            if (TaiXiu.FloodgateSupport()) {
                if (Bukkit.getPlayer(player) != null) {
                    if (FloodgateApi.getInstance().isFloodgatePlayer(Bukkit.getPlayer(player).getUniqueId())) {
                        CommandUtil.dispatchCommand(Bukkit.getPlayer(player), "console:eco give " + player + " " + money);
                        continue;
                    }
                }
            }

            VaultSupport.econ.depositPlayer(player, money);
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

    public static Long getTaiBet(@NotNull ISession session) {

        long sum = 0L;
        if (session.getTaiPlayers() != null) {
            for (long value : session.getTaiPlayers().values()) {
                sum += value;
            }
        }
        return sum;
    }

    public static Long getTotalBet(@NotNull ISession session) {
        return getXiuBet(session) + getTaiBet(session);
    }

    public static String getBestWinner(@NotNull ISession session) {

        TaiXiuResult result = session.getResult();
        FileConfiguration messageF = MessageFile.get();

        try {
            if (result == TaiXiuResult.NONE) {
                return messageF.getString("bestWinners-format.invalid");
            }

            if (result == TaiXiuResult.SPECIAL) {
                return messageF.getString("bestWinners-format.valid-special").replace("%allBet%", MessageUtil.formatMoney(getTotalBet(session)));
            }

            Map<String, Long> bestWinners = result == TaiXiuResult.XIU ? session.getXiuPlayers() : session.getTaiPlayers();
            Long bestWinnersBet = Collections.max(bestWinners.values());

            List<String> players = new ArrayList<>();
            for (Map.Entry<String, Long> entry : bestWinners.entrySet()) {
                if (entry.getValue() >= bestWinnersBet)
                    players.add(entry.getKey());
                else {
                    Bukkit.getConsoleSender().sendMessage(String.valueOf(entry.getValue() + " khong bang " + bestWinnersBet + " :))"));
                }
            }

            String delim = messageF.getString("bestWinners-format.playerName-delim");
            String bestWinnersName = String.join(delim, players);

            return messageF.getString("bestWinners-format.valid")
                    .replace("%playerName%", bestWinnersName)
                    .replace("%bet%", MessageUtil.formatMoney(bestWinnersBet * 2));
        } catch (Exception e) {
            return messageF.getString("bestWinners-format.invalid");
        }
    }

    public void stopTask() {
        getTaiXiuTask().cancel();
    }

}
