package com.cortezromeo.taixiu.task;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.TaiXiuState;
import com.cortezromeo.taixiu.api.event.SessionSwapEvent;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import com.cortezromeo.taixiu.manager.TaiXiuManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import static com.cortezromeo.taixiu.manager.DebugManager.debug;

public class TaiXiuTask implements Runnable {
    private BukkitTask task;

    private int time;
    private TaiXiuState state;
    private ISession data;

    public TaiXiuTask(int time) {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(TaiXiu.plugin, this, 0, 20L);
        data = DatabaseManager.getSessionData(DatabaseManager.getLastSession());
        this.time = time;
        this.state = TaiXiuState.PLAYING;
    }

    public BukkitTask getBukkitTask() {
        return task;
    }

    public int getTaskID() {
        return task.getTaskId();
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

    @Override
    public void run() {
        if (state == TaiXiuState.PLAYING) {
            time--;

            TaiXiuManager manager = TaiXiu.plugin.getManager();
            if (getSession().getResult() != TaiXiuResult.NONE)
                time = 0;

            if (time == 0) {

                manager.resultSeason(getSession(), 0, 0, 0);

                long newSession = DatabaseManager.getLastSession();
                debug("SESSION SWAPPED " + ">>> old_session: " + getSession().getSession() + " " +
                        "| new_session: " + newSession);

                ISession oldSessionData = getSession();
                setSession(newSession);
                ISession newSessionData = getSession();

                time = TaiXiu.plugin.getConfig().getInt("task.taiXiuTask.time-per-session");

                SessionSwapEvent event = new SessionSwapEvent(oldSessionData, newSessionData);
                TaiXiu.plugin.getServer().getScheduler().runTask(TaiXiu.plugin, () -> TaiXiu.plugin.getServer().getPluginManager().callEvent(event));
            }
        }
    }

    public void cancel() {
        task.cancel();
    }

}
