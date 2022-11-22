package com.cortezromeo.taixiu.manager;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.TaiXiuState;
import com.cortezromeo.taixiu.api.event.SessionResultEvent;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.file.MessageFile;
import com.cortezromeo.taixiu.support.VaultSupport;
import com.cortezromeo.taixiu.task.TaiXiuTask;
import com.cortezromeo.taixiu.util.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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
        if (session.getResult() != TaiXiuResult.NONE)
            return;

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

            if (session.getResult() == TaiXiuResult.XIU && session.getXiuPlayers() != null) {
                for (String player : session.getXiuPlayers().keySet()) {
                    econ.depositPlayer(player, session.getXiuPlayers().get(player) * 2);
                    sendMessage(Bukkit.getPlayer(player), messageF.getString("session-player-win")
                            .replaceAll("%result%", MessageUtil.getFormatName(session.getResult()))
                            .replaceAll("%money%", MessageUtil.formatMoney(session.getXiuPlayers().get(player) * 2)));
                }
            } else if (session.getResult() == TaiXiuResult.TAI && session.getTaiPlayers() != null) {
                for (String player : session.getTaiPlayers().keySet()) {
                    econ.depositPlayer(player, session.getTaiPlayers().get(player) * 2);
                    sendMessage(Bukkit.getPlayer(player), messageF.getString("session-player-win")
                            .replaceAll("%result%", MessageUtil.getFormatName(session.getResult()))
                            .replaceAll("%money%", MessageUtil.formatMoney(session.getTaiPlayers().get(player) * 2)));
                }
            } else
                sendBoardCast(messageF.getString("session-special-win"));

            SessionResultEvent event = new SessionResultEvent(session);
            TaiXiu.plugin.getServer().getScheduler().runTask(TaiXiu.plugin, () -> TaiXiu.plugin.getServer().getPluginManager().callEvent(event));

            debug("SESSION RESULTED " + ">>> session: " + session.getSession() + " " +
                    "| dice1: " + dice1 + " " +
                    "| dice2: " + dice2 + " " +
                    "| dice3: " + dice3 + " " +
                    "| result: " + session.getResult());
        } catch (Exception e) {
            resultSeason(session, dice1, dice2, dice3);
            debug("ERROR [SESSION RESULTED] " + ">>> " + e);
            debug("&b&lVUI LÒNG BÁO LẠI LỖI NÀY QUA DISCORD CỦA MÌNH: Cortez_Romeo#1290");
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

            HashMap<String, Long> bestWinners = (result == TaiXiuResult.XIU ? (session.getXiuPlayers() == null ? null : session.getXiuPlayers()) : (session.getTaiPlayers() == null ? null : session.getTaiPlayers()));
            Long bestWinnersBet = Collections.max(bestWinners.values());

            List<String> players = new ArrayList<>();
            for (Map.Entry<String, Long> entry : bestWinners.entrySet()) {
                if (entry.getValue() == bestWinnersBet) {
                    players.add(entry.getKey());
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
