package com.cortezromeo.taixiu.manager;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.TaiXiuState;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.file.MessageFile;
import com.cortezromeo.taixiu.task.TaiXiuTask;
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
    private static ISession currentBossBarSession;
    public static boolean reloadingBossBar;
    private static boolean bbReloadingEnable;
    private static String bbPlayingTitle;
    private static String bbReloadingTitle;
    private static BarColor bbPlayingColorPlaying;
    private static BarColor bbPlayingColorBetDisabling;
    private static BarColor bbPlayingColorPausing;
    private static BarColor bbReloadingColor;
    private static BarStyle bbPlayingStyle;
    private static BarStyle bbReloadingStyle;
    private static ISession bbReloadingSession;
    private static int timeDisabling;
    private static double timeReloading;
    private static double timeReloadingCount;

    public static void setupValue() {
        FileConfiguration config = TaiXiu.plugin.getConfig();

        enable = config.getBoolean("boss-bar.enable");
        currentBossBarSession = TaiXiuManager.getTaiXiuTask().getSession();

        bbPlayingTitle = config.getString("boss-bar.type.playing.title");
        bbPlayingColorPlaying = BarColor.valueOf(config.getString("boss-bar.type.playing.color.playing"));
        bbPlayingColorBetDisabling = BarColor.valueOf(config.getString("boss-bar.type.playing.color.bet-disabling"));
        bbPlayingColorPausing = BarColor.valueOf(config.getString("boss-bar.type.playing.color.pausing"));
        bbPlayingStyle = BarStyle.valueOf(config.getString("boss-bar.type.playing.style"));
        timeDisabling = config.getInt("bet-settings.disable-while-remaining");

        bbReloadingEnable = config.getBoolean("boss-bar.type.reloading.enable");
        bbReloadingTitle = config.getString("boss-bar.type.reloading.title");
        bbReloadingColor = BarColor.valueOf(config.getString("boss-bar.type.reloading.color"));
        bbReloadingStyle = BarStyle.valueOf(config.getString("boss-bar.type.reloading.style"));
        timeReloading = config.getDouble("boss-bar.type.reloading.time");
        if (timeReloading <= 0)
            bbReloadingEnable = false;
        timeReloadingCount = 0;

    }

    public static void setReloadingBossBar(boolean status) {
        if (!bbReloadingEnable)
            return;

        reloadingBossBar = status;
    }

    public static void toggleBossBar(Player p) {

        if (!enable || !DatabaseManager.togglePlayers.contains(p.getName()))
            return;

        if (bossBarPlayers.containsKey(p)) {
            bossBarPlayers.get(p).removeAll();
            bossBarPlayers.remove(p);
        }

        BossBar taiXiuBossBar = Bukkit.createBossBar(TaiXiu.nms.addColor(MessageFile.get().getString("request-loading").replace("%prefix%", "")),
                bbPlayingColorPausing,
                bbPlayingStyle
        );
        taiXiuBossBar.setProgress(1);
        taiXiuBossBar.addPlayer(p);
        taiXiuBossBar.setVisible(true);
        bossBarPlayers.put(p, taiXiuBossBar);
    }

    public static void putValueBossBar(Player p, int timeLeft, int timePerSession) {

        if (!bossBarPlayers.containsKey(p) || !p.isOnline()) {
            return;
        }

        if (!DatabaseManager.togglePlayers.contains(p.getName()) && bossBarPlayers.containsKey(p)) {
            bossBarPlayers.get(p).removeAll();
            bossBarPlayers.remove(p);
            return;
        }

        BossBar bossBar = bossBarPlayers.get(p);

        if (timeLeft == 0) {
            if (bbReloadingEnable) {
                setReloadingBossBar(true);
                bbReloadingSession = currentBossBarSession;
            } else {
                currentBossBarSession = TaiXiuManager.getTaiXiuTask().getSession();
                timeLeft = timePerSession;
            }
        }

        String bossBarTitle = bbPlayingTitle;

        if (reloadingBossBar) {

            bossBarTitle = bbReloadingTitle;
            bossBarTitle = bossBarTitle.replace("%session%", String.valueOf(bbReloadingSession.getSession()));
            bossBarTitle = bossBarTitle.replace("%result%", MessageUtil.getFormatName(bbReloadingSession.getResult()));
            bossBarTitle = bossBarTitle.replace("%numberOfPlayers%", String.valueOf(
                    (bbReloadingSession.getResult() == TaiXiuResult.XIU ? bbReloadingSession.getXiuPlayers().size() : bbReloadingSession.getTaiPlayers().size())));
            bossBarTitle = bossBarTitle.replace("%money%", String.valueOf(
                    (bbReloadingSession.getResult() == TaiXiuResult.XIU ? TaiXiuManager.getXiuBet(bbReloadingSession) : TaiXiuManager.getTaiBet(bbReloadingSession))));

            bossBar.setTitle(TaiXiu.nms.addColor(bossBarTitle));

            bossBarPlayers.get(p).setStyle(bbReloadingStyle);

            try {
                bossBar.setProgress(timeReloadingCount / timeReloading);
                timeReloadingCount++;
            } catch (Exception e) {
                bossBar.setProgress(1);
            }

            bossBar.setColor(bbReloadingColor);

            if (timeReloadingCount / timeReloading == 1) {
                setReloadingBossBar(false);
                timeReloadingCount = 0;
                currentBossBarSession = TaiXiuManager.getTaiXiuTask().getSession();
            }
        } else {

            bossBarTitle = bossBarTitle.replace("%session%", String.valueOf(currentBossBarSession.getSession()));
            bossBarTitle = bossBarTitle.replace("%timeLeft%", String.valueOf(timeLeft));
            bossBarTitle = bossBarTitle.replace("%totalBet%", MessageUtil.formatMoney(TaiXiuManager.getTotalBet(currentBossBarSession)));
            bossBarTitle = bossBarTitle.replace("%xiuBet%", MessageUtil.formatMoney(TaiXiuManager.getXiuBet(currentBossBarSession)));
            bossBarTitle = bossBarTitle.replace("%taiBet%", MessageUtil.formatMoney(TaiXiuManager.getTaiBet(currentBossBarSession)));
            bossBar.setTitle(TaiXiu.nms.addColor(bossBarTitle));

            try {
                bossBar.setProgress((double) timeLeft / (double) timePerSession);
            } catch (Exception e) {
                bossBar.setProgress(1);
            }

            if (timeLeft <= timeDisabling) {
                bossBar.setColor(bbPlayingColorBetDisabling);
            } else
                bossBar.setColor(bbPlayingColorPlaying);

            if (TaiXiuManager.getState() == TaiXiuState.PAUSING)
                bossBar.setColor(bbPlayingColorPausing);
        }

    }

}
