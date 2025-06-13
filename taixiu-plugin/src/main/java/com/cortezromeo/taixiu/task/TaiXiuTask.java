package com.cortezromeo.taixiu.task;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.CurrencyTyppe;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.TaiXiuState;
import com.cortezromeo.taixiu.api.event.SessionSwapEvent;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.enums.SessionEndingType;
import com.cortezromeo.taixiu.language.Messages;
import com.cortezromeo.taixiu.manager.BossBarManager;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import com.cortezromeo.taixiu.manager.TaiXiuManager;
import com.cortezromeo.taixiu.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import static com.cortezromeo.taixiu.manager.DebugManager.debug;

public class TaiXiuTask implements Runnable {
    private ScheduledTask task;
    private int time;
    private TaiXiuState state;
    private ISession data;

    public TaiXiuTask(int time) {
        this.task = TaiXiu.plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(
            TaiXiu.plugin,
            t -> run(),
            1L,
            20L
        );
        data = DatabaseManager.getSessionData(DatabaseManager.getLastSessionFromFile());
        this.time = time;
        this.state = TaiXiuState.PLAYING;

        debug("TAIXIU TASK", "RUNNING TASK ID: " + getTaskID() + " | SESSION NUMBER: " + data.getSession());
    }

    public ScheduledTask getBukkitTask() {
        return task;
    }

    public int getTaskID() {
        return task != null ? task.hashCode() : -1;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public TaiXiuState getState() {
        return state;
    }

    public void setState(TaiXiuState state) {
        this.state = state;
    }

    public ISession getSession() {
        return data;
    }

    public void setSession(long session) {
        this.data = DatabaseManager.getSessionData(session);
    }

    public void setSession(ISession session) {
        this.data = session;
    }

    @Override
    public void run() {
        if (state == TaiXiuState.PLAYING) {
            try {
                time--;

                if (getSession().getResult() != TaiXiuResult.NONE)
                    time = 0;

                if (time <= 0) {
                    time = 0;
                    if ((getSession().getTaiPlayers().isEmpty() && getSession().getXiuPlayers().isEmpty()) && getSession().getResult() == TaiXiuResult.NONE) {
                        MessageUtil.sendBroadCast(Messages.NOT_ENOUGH_PLAYER.replace("%session%", String.valueOf(getSession().getSession())));
                    } else {
                        TaiXiuManager.resultSeason(getSession(), 0, 0, 0);

                        ISession oldSessionData = getSession();
                        long newSession = DatabaseManager.getLastSession() + 1;
                        setSession(newSession);

                        debug("SESSION SWAPPED", "Old session: " + oldSessionData.getSession() + " " +
                                "| New session: " + newSession);

                        SessionSwapEvent event = new SessionSwapEvent(oldSessionData, getSession());
                        TaiXiu.plugin.getServer().getScheduler().runTask(TaiXiu.plugin, () -> TaiXiu.plugin.getServer().getPluginManager().callEvent(event));

                        if (DatabaseManager.sessionEndingType == SessionEndingType.SAVE)
                            DatabaseManager.saveSessionData(oldSessionData.getSession());
                        else
                            DatabaseManager.unloadSessionData(oldSessionData.getSession());

                        if (!DatabaseManager.togglePlayers.isEmpty())
                            for (String playerBossBar : DatabaseManager.togglePlayers)
                                BossBarManager.putValueBossBar(Bukkit.getPlayer(playerBossBar), time);
                    }
                    TaiXiuManager.setTime(TaiXiu.plugin.getConfig().getInt("task.taiXiuTask.time-per-session"));
                    TaiXiuManager.setCurrencyType(CurrencyTyppe.valueOf(TaiXiu.plugin.getConfig().getString("currency-settings.default").toUpperCase()));
                } else {
                    if (!DatabaseManager.togglePlayers.isEmpty())
                        for (String playerBossBar : DatabaseManager.togglePlayers)
                            BossBarManager.putValueBossBar(Bukkit.getPlayer(playerBossBar), time);
                }
            } catch (Exception e) {
                cancel();
                MessageUtil.throwErrorMessage("<taixiutask.java<run>>" + e);
                setSession(DatabaseManager.getLastSession() + 1);
                TaiXiuManager.startTask(TaiXiu.plugin.getConfig().getInt("task.taiXiuTask.time-per-session"));
            }
        }
    }

    public void cancel() {
        task.cancel();
    }
}
