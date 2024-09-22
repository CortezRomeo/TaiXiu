package com.cortezromeo.taixiu.manager;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.TaiXiuState;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.file.MessageFile;
import com.cortezromeo.taixiu.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
    private static HashMap<TaiXiuResult, BarColor> bbReloadingColor = new HashMap<>();
    private static BarStyle bbPlayingStyle;
    private static BarStyle bbReloadingStyle;
    private static ISession bbReloadingSession;
    public static int timePerSession;
    private static int timeDisabling;
    private static double timeReloading;
    private static double timeReloadingCount;

    public static void setupValue() {
        FileConfiguration config = TaiXiu.plugin.getConfig();

        enable = config.getBoolean("boss-bar.enabled");
        timePerSession = config.getInt("task.taiXiuTask.time-per-session");
        currentBossBarSession = TaiXiuManager.getTaiXiuTask().getSession();

        bbPlayingTitle = config.getString("boss-bar.type.playing.title");
        bbPlayingColorPlaying = BarColor.valueOf(config.getString("boss-bar.type.playing.color.playing").toUpperCase());
        bbPlayingColorBetDisabling = BarColor.valueOf(config.getString("boss-bar.type.playing.color.bet-disabling").toUpperCase());
        bbPlayingColorPausing = BarColor.valueOf(config.getString("boss-bar.type.playing.color.pausing").toUpperCase());
        bbPlayingStyle = BarStyle.valueOf(config.getString("boss-bar.type.playing.style").toUpperCase());
        timeDisabling = config.getInt("bet-settings.disable-while-remaining");

        bbReloadingEnable = config.getBoolean("boss-bar.type.reloading.enabled");
        bbReloadingTitle = config.getString("boss-bar.type.reloading.title");

        if (config.getString("boss-bar.type.reloading.color").equalsIgnoreCase("RESULT-COLOR")) {
            bbReloadingColor.put(TaiXiuResult.XIU, BarColor.valueOf(config.getString("boss-bar.type.reloading.result-color-setting.xiu").toUpperCase()));
            bbReloadingColor.put(TaiXiuResult.TAI, BarColor.valueOf(config.getString("boss-bar.type.reloading.result-color-setting.tai").toUpperCase()));
        } else {
            bbReloadingColor.remove(TaiXiuResult.XIU);
            bbReloadingColor.put(TaiXiuResult.NONE, BarColor.valueOf(config.getString("boss-bar.type.reloading.color").toUpperCase()));
        }
        bbReloadingStyle = BarStyle.valueOf(config.getString("boss-bar.type.reloading.style").toUpperCase());
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
        if (!enable)
            return;

        // reset bossBar
        if (bossBarPlayers.containsKey(p)) {
            bossBarPlayers.get(p).removeAll();
            bossBarPlayers.remove(p);
        }

        if (DatabaseManager.togglePlayers.contains(p.getName())) {
            BossBar taiXiuBossBar = Bukkit.createBossBar(TaiXiu.nms.addColor(MessageFile.get().getString("request-loading").replace("%prefix%", "")),
                    bbPlayingColorPausing,
                    bbPlayingStyle
            );
            taiXiuBossBar.setProgress(1);
            taiXiuBossBar.addPlayer(p);
            taiXiuBossBar.setVisible(true);
            bossBarPlayers.put(p, taiXiuBossBar);
        }
    }

    public static void putValueBossBar(@NotNull Player p, int timeLeft) {
        if (DatabaseManager.togglePlayers.contains(p.getName())) {
            if (!bossBarPlayers.containsKey(p))
                toggleBossBar(p);
            BossBar bossBar = bossBarPlayers.get(p);

            if (timeLeft <= 0) {
                timeLeft = 0;
                if (bbReloadingEnable) {
                    setReloadingBossBar(true);
                    bbReloadingSession = currentBossBarSession;
                } else {
                    currentBossBarSession = TaiXiuManager.getTaiXiuTask().getSession();
                    timePerSession = TaiXiu.plugin.getConfig().getInt("task.taiXiuTask.time-per-session");
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
                bossBarTitle = bossBarTitle.replace("%money%",
                        (bbReloadingSession.getResult() == TaiXiuResult.XIU ? TaiXiuManager.getXiuBetFormat(bbReloadingSession) : TaiXiuManager.getTaiBetFormat(bbReloadingSession)));

                bossBar.setTitle(TaiXiu.nms.addColor(bossBarTitle));

                bossBarPlayers.get(p).setStyle(bbReloadingStyle);

                try {
                    bossBar.setProgress(timeReloadingCount / timeReloading);
                    timeReloadingCount++;
                } catch (Exception e) {
                    bossBar.setProgress(1);
                }

                if (bbReloadingColor.get(TaiXiuResult.XIU) == null)
                    bossBar.setColor(bbReloadingColor.get(TaiXiuResult.NONE));
                else {
                    if (bbReloadingSession.getResult() == TaiXiuResult.XIU)
                        bossBar.setColor(bbReloadingColor.get(TaiXiuResult.XIU));
                    else
                        bossBar.setColor(bbReloadingColor.get(TaiXiuResult.TAI));
                }

                if (timeReloadingCount / timeReloading == 1) {
                    setReloadingBossBar(false);
                    timeReloadingCount = 0;
                    currentBossBarSession = TaiXiuManager.getTaiXiuTask().getSession();
                    timePerSession = timeLeft;
                }
            } else {
                bossBarTitle = bossBarTitle.replace("%session%", String.valueOf(currentBossBarSession.getSession()));
                bossBarTitle = bossBarTitle.replace("%timeLeft%", String.valueOf(timeLeft));
                bossBarTitle = bossBarTitle.replace("%totalBet%", MessageUtil.formatMoney(TaiXiuManager.getTotalBet(currentBossBarSession)));
                bossBarTitle = bossBarTitle.replace("%xiuBet%", MessageUtil.formatMoney(TaiXiuManager.getXiuBet(currentBossBarSession)));
                bossBarTitle = bossBarTitle.replace("%taiBet%", MessageUtil.formatMoney(TaiXiuManager.getTaiBet(currentBossBarSession)));
                bossBar.setTitle(TaiXiu.nms.addColor(bossBarTitle));

                bossBarPlayers.get(p).setStyle(bbPlayingStyle);

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
        } else {
            if (bossBarPlayers.containsKey(p)) {
                bossBarPlayers.get(p).removeAll();
                bossBarPlayers.remove(p);
            }
        }
    }
}
