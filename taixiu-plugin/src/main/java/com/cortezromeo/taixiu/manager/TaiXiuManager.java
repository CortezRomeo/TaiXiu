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
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
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

    public void startTask(int time) {
        taiXiuTask = new TaiXiuTask(time);
    }

    public static TaiXiuState getState() {
        return getTaiXiuTask().getState();
    }

    public void setState(TaiXiuState state) {
        getTaiXiuTask().setState(state);
    }

    public int getTime() {
        return getTaiXiuTask().getTime();
    }

    public void setTime(int time) {
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
            session.setDice3(dice3);
        }

        if (total == 18 && cfg.getBoolean("bet-settings.disable-special")) {
            dice3--;
            session.setDice3(dice3);
        }

        FileConfiguration messageF = MessageFile.get();
        String bestWinners = messageF.getString("bestWinners-format.invalid");

        if (total >= 4 && total <= 10) {
            session.setResult(TaiXiuResult.XIU);
            if (session.getXiuPlayers().size() > 0)
                bestWinners = getBestWinner(session.getXiuPlayers());
        } else if (total >= 11 && total <= 17) {
            session.setResult(TaiXiuResult.TAI);
            if (session.getTaiPlayers().size() > 0)
                bestWinners = getBestWinner(session.getTaiPlayers());
        } else {
            session.setResult(TaiXiuResult.SPECIAL);
            bestWinners = messageF.getString("bestWinners-format.valid-special");

            Long sum1 = 0L;
            if (session.getXiuPlayers() != null) {
                for (Long value : session.getXiuPlayers().values()) {
                    sum1 += value;
                }
            }

            Long sum2 = 0L;
            if (session.getTaiPlayers() != null) {
                for (Long value : session.getTaiPlayers().values()) {
                    sum2 += value;
                }
            }

            bestWinners = bestWinners.replace("%allBet%", String.valueOf(sum1 + sum2));
        }

        for (String string : messageF.getStringList("session-result")) {
            string = string.replace("%session%", String.valueOf(session.getSession()));
            string = string.replace("%dice1%", String.valueOf(session.getDice1()));
            string = string.replace("%dice2%", String.valueOf(session.getDice2()));
            string = string.replace("%dice3%", String.valueOf(session.getDice3()));
            string = string.replace("%total%", String.valueOf(total));
            string = string.replace("%result%", MessageUtil.getFormatName(session.getResult()));
            string = string.replace("%bestWinners%", bestWinners);

            sendBoardCast(string);
        }

        if (session.getResult() == TaiXiuResult.XIU && session.getXiuPlayers() != null) {
            for (String player : session.getXiuPlayers().keySet()) {
                econ.depositPlayer(player, session.getXiuPlayers().get(player) * 2);
                sendMessage(Bukkit.getPlayer(player), messageF.getString("session-player-win")
                        .replaceAll("%result%", MessageUtil.getFormatName(session.getResult()))
                        .replaceAll("%money%", String.valueOf(session.getXiuPlayers().get(player) * 2)));
            }
        } else if (session.getResult() == TaiXiuResult.TAI && session.getTaiPlayers() != null) {
            for (String player : session.getTaiPlayers().keySet()) {
                econ.depositPlayer(player, session.getTaiPlayers().get(player) * 2);
                sendMessage(Bukkit.getPlayer(player), messageF.getString("session-player-win")
                        .replaceAll("%result%", MessageUtil.getFormatName(session.getResult()))
                        .replaceAll("%money%", String.valueOf(session.getTaiPlayers().get(player) * 2)));
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

    }

    private static String getBestWinner(HashMap<String, Long> hashmap) {
        FileConfiguration messageF = MessageFile.get();
        Long bestWinnersBet = Collections.max(hashmap.values());

        List<String> players = new ArrayList<>();
        for (Map.Entry<String, Long> entry : hashmap.entrySet()) {
            if (entry.getValue() >= bestWinnersBet) {
                players.add(entry.getKey());
            }
        }

        String delim = messageF.getString("bestWinners-format.playerName-delim");
        String bestWinnersName = String.join(delim, players);

        return messageF.getString("bestWinners-format.valid")
                .replaceAll("%playerName%", bestWinnersName)
                .replaceAll("%bet%", String.valueOf(bestWinnersBet * 2));
    }


    public void stopTask() {
        getTaiXiuTask().cancel();
    }

}
