package com.cortezromeo.taixiu.manager;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.TaiXiuState;
import com.cortezromeo.taixiu.api.event.PlayerBetEvent;
import com.cortezromeo.taixiu.api.event.SessionResultEvent;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.file.MessageFile;
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
import static com.cortezromeo.taixiu.util.MessageUtil.sendBoardCast;
import static com.cortezromeo.taixiu.util.MessageUtil.sendMessage;

enum SoundType {
    win, lose
}

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
        Economy econ = VaultSupport.econ;
        ISession data = getSessionData();
        FileConfiguration messageF = MessageFile.get();
        FileConfiguration cfg = TaiXiu.plugin.getConfig();

        if (getSessionData().getXiuPlayers().containsKey(pName) || data.getTaiPlayers().containsKey(pName)) {
            sendMessage(player, messageF.getString("have-bet-before")
                    .replace("%bet%", MessageUtil.getFormatName((data.getXiuPlayers().containsKey(pName)
                            ? TaiXiuResult.XIU
                            : TaiXiuResult.TAI)))
                    .replace("%money%", (data.getXiuPlayers().containsKey(pName)
                            ? MessageUtil.formatMoney(data.getXiuPlayers().get(pName))
                            : MessageUtil.formatMoney(data.getTaiPlayers().get(pName)))));
            return;
        }

        int configDisableTime = cfg.getInt("bet-settings.disable-while-remaining");
        if (TaiXiuManager.getTime() <= configDisableTime) {
            sendMessage(player, messageF.getString("late-bet")
                    .replaceAll("%time%", String.valueOf(TaiXiuManager.getTime()))
                    .replaceAll("%configDisableTime%", String.valueOf(configDisableTime)));
            return;
        }

        if (econ.getBalance(player) < money) {
            sendMessage(player, messageF.getString("not-enough-money"));
            return;
        }

        long minBet = cfg.getLong("bet-settings.min-bet");
        if (money < minBet) {
            sendMessage(player, messageF.getString("min-bet").replace("%minBet%", MessageUtil.formatMoney(minBet)));
            return;
        }

        long maxBet = cfg.getLong("bet-settings.max-bet");
        if (money > maxBet) {
            sendMessage(player, messageF.getString("max-bet").replace("%maxBet%", MessageUtil.formatMoney(maxBet)));
            return;
        }

        econ.withdrawPlayer(player, money);

        if (result == TaiXiuResult.XIU)
            data.addXiuPlayer(pName, money);

        if (result == TaiXiuResult.TAI)
            data.addTaiPlayer(pName, money);

        sendMessage(player, messageF.getString("player-bet")
                .replace("%bet%", MessageUtil.getFormatName(result))
                .replace("%money%", MessageUtil.formatMoney(money))
                .replace("%session%", String.valueOf(data.getSession()))
                .replace("%time%", String.valueOf(TaiXiuManager.getTime())));

        String messageBoardcastPlayerBet = messageF.getString("broadcast-player-bet")
                .replace("%prefix%", messageF.getString("prefix"))
                .replace("%player%", player.getName())
                .replace("%bet%", MessageUtil.getFormatName(result))
                .replace("%money%", MessageUtil.formatMoney(money));

        if (!TaiXiu.PAPISupport())
            Bukkit.broadcastMessage(TaiXiu.nms.addColor(messageBoardcastPlayerBet));
        else
            Bukkit.broadcastMessage(TaiXiu.nms.addColor(PlaceholderAPI.setPlaceholders(player, messageBoardcastPlayerBet)));

        PlayerBetEvent event = new PlayerBetEvent(player, result, money);
        Bukkit.getServer().getPluginManager().callEvent(event);

        debug("PLAYER BETTED",
                "Name: " + pName + " " +
                        "| Bet: " + result.toString() + " " +
                        "| Money: " + money + " " +
                        "| Session: " + data.getSession());

        // discordSRV
        if (TaiXiu.getDiscordManager() != null) {
            try {
                TaiXiu.getDiscordManager().sendMessage(DiscordManager.getPlayerBetMessageFromJSON(TaiXiu.plugin.getDataFolder() + "/discordsrv-playerbet-message.json", player, result, money));
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
                executeWinners(session.getXiuPlayers(), session.getResult(), tax);

                for (String taiPlayer : session.getTaiPlayers().keySet()) {
                    String message = messageF.getString("session-player-lose")
                            .replaceAll("%result%", MessageUtil.getFormatName(TaiXiuResult.TAI))
                            .replaceAll("%money%", MessageUtil.formatMoney(session.getTaiPlayers().get(taiPlayer)));

                    playSound(Bukkit.getPlayer(taiPlayer), SoundType.lose);
                    sendMessage(Bukkit.getPlayer(taiPlayer), message);
                }
            } else if (session.getResult() == TaiXiuResult.TAI && session.getTaiPlayers() != null) {
                executeWinners(session.getTaiPlayers(), session.getResult(), tax);

                for (String xiuPlayer : session.getXiuPlayers().keySet()) {
                    String message = messageF.getString("session-player-lose")
                            .replaceAll("%result%", MessageUtil.getFormatName(TaiXiuResult.XIU))
                            .replaceAll("%money%", MessageUtil.formatMoney(session.getXiuPlayers().get(xiuPlayer)));

                    playSound(Bukkit.getPlayer(xiuPlayer), SoundType.lose);
                    sendMessage(Bukkit.getPlayer(xiuPlayer), message);
                }
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
            MessageUtil.thowErrorMessage("<taixiumanager.java<resultSeason>>" + e);
        }

        // discordSRV
        if (TaiXiu.getDiscordManager() != null) {
            try {
                TaiXiu.getDiscordManager().sendMessage(DiscordManager.getResultMessageFromJSON(TaiXiu.plugin.getDataFolder() + "/discordsrv-result-message.json", session));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private static void executeWinners(@NotNull HashMap<String, Long> players, TaiXiuResult result, double tax) {
        FileConfiguration messageF = MessageFile.get();
        for (String player : players.keySet()) {

            long money = players.get(player) * 2;
            String message;

            if (tax > 0) {
                money = players.get(player) + Math.round(players.get(player) - (players.get(player) * tax));
                message = messageF.getString("session-player-win-with-tax")
                        .replaceAll("%result%", MessageUtil.getFormatName(result))
                        .replaceAll("%money%", MessageUtil.formatMoney(money))
                        .replaceAll("%tax%", String.valueOf(tax * 100));
            } else {
                message = messageF.getString("session-player-win")
                        .replaceAll("%result%", MessageUtil.getFormatName(result))
                        .replaceAll("%money%", MessageUtil.formatMoney(money));
            }
            playSound(Bukkit.getPlayer(player), SoundType.win);
            sendMessage(Bukkit.getPlayer(player), message);
            VaultSupport.econ.depositPlayer(player, money);
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
        return MessageUtil.formatMoney(getXiuBet(session));
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
        return MessageUtil.formatMoney(getTaiBet(session));
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
            if (bestWinners.isEmpty())
                return messageF.getString("bestWinners-format.invalid");
            Long bestWinnersBet = Collections.max(bestWinners.values());

            List<String> players = new ArrayList<>();
            for (Map.Entry<String, Long> entry : bestWinners.entrySet())
                if (entry.getValue() >= bestWinnersBet)
                    players.add(entry.getKey());

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
