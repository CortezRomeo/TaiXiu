package com.cortezromeo.taixiu.manager;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuState;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.file.MessageFile;
import com.cortezromeo.taixiu.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class BossBarManager {

    public static Map<Player, BossBar> bossBarPlayers = new HashMap<>();
    private static boolean enable;
    private static String title;
    private static BarColor colorPlaying;
    private static BarColor colorBetDisabling;
    private static BarColor colorPausing;
    private static BarStyle style;
    private static int timePerSession;
    private static int timeDisabling;


    public static void setupValue() {
        FileConfiguration config = TaiXiu.plugin.getConfig();

        enable = config.getBoolean("boss-bar.enable");
        title = config.getString("boss-bar.title");
        colorPlaying = BarColor.valueOf(config.getString("boss-bar.color.playing"));
        colorBetDisabling = BarColor.valueOf(config.getString("boss-bar.color.bet-disabling"));
        colorPausing = BarColor.valueOf(config.getString("boss-bar.color.pausing"));
        style = BarStyle.valueOf(config.getString("boss-bar.style"));

        timePerSession = config.getInt("task.taiXiuTask.time-per-session");
        timeDisabling = config.getInt("bet-settings.disable-while-remaining");

    }

    public static void setTimePerSession(int time) {
        timePerSession = time;
    }

    public static void toggleBossBar(Player p) {

        if (!enable || !DatabaseManager.togglePlayers.contains(p.getName()))
            return;

        if (bossBarPlayers.containsKey(p)) {
            bossBarPlayers.get(p).removeAll();
            bossBarPlayers.remove(p);
        }

        BossBar newBossBar = Bukkit.createBossBar(TaiXiu.nms.addColor(MessageFile.get().getString("request-loading").replace("%prefix%", "")),
                colorPausing,
                style,
                new BarFlag[0]);
        newBossBar.setProgress(1);
        newBossBar.addPlayer(p);
        newBossBar.setVisible(true);
        bossBarPlayers.put(p, newBossBar);

        new BukkitRunnable() {
            public void run() {

                if (!bossBarPlayers.containsKey(p) || !p.isOnline()) {
                    cancel();
                    return;
                }

                if (!DatabaseManager.togglePlayers.contains(p.getName()) && bossBarPlayers.containsKey(p)) {
                    bossBarPlayers.get(p).removeAll();
                    bossBarPlayers.remove(p);
                    cancel();
                    return;
                }

                BossBar bossBar = bossBarPlayers.get(p);
                ISession session = TaiXiuManager.getSessionData();
                int timeLeft = TaiXiuManager.getTaiXiuTask().getTime();

                String bossBarTitle = title.replace("%session%", String.valueOf(session.getSession()));
                bossBarTitle = bossBarTitle.replace("%timeLeft%", String.valueOf(timeLeft));
                bossBarTitle = bossBarTitle.replace("%totalBet%", MessageUtil.formatMoney(TaiXiuManager.getTotalBet(session)));
                bossBarTitle = bossBarTitle.replace("%xiuBet%", MessageUtil.formatMoney(TaiXiuManager.getXiuBet(session)));
                bossBarTitle = bossBarTitle.replace("%taiBet%", MessageUtil.formatMoney(TaiXiuManager.getTaiBet(session)));
                bossBar.setTitle(TaiXiu.nms.addColor(bossBarTitle));

                try {
                    double bossBarProgess = (double) timeLeft / (double) timePerSession;
                    bossBar.setProgress(bossBarProgess);
                } catch (Exception e) {
                    bossBar.setProgress(1);
                }

                if (timeLeft <= timeDisabling) {
                    bossBar.setColor(colorBetDisabling);
                } else
                    bossBar.setColor(colorPlaying);

                if (TaiXiuManager.getState() == TaiXiuState.PAUSING)
                    bossBar.setColor(colorPausing);

            }
        }.runTaskTimerAsynchronously(TaiXiu.plugin, 0, 20);
    }
}
