package com.cortezromeo.taixiu.task;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Set;

import static com.cortezromeo.taixiu.util.MessageUtil.log;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class AutoSaveTask implements Runnable {

    private ScheduledTask task;

    public AutoSaveTask(int time) {
        this.task = TaiXiu.plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(
            TaiXiu.plugin,
            t -> run(), // or just pass `this` if you're using lambda-style Runnable
            20L * time,
            20L * time
        );
    }

    public ScheduledTask getBukkitTask() {
        return task;
    }

    public int getTaskID() {
        return task != null ? task.hashCode() : -1;
    }

    @Override
    public void run() {
        Set<Long> sessionData = DatabaseManager.taiXiuData.keySet();
        log("&e[TAI XIU] Tiến hành save " + sessionData.size() + " dữ liệu...");
        for (long session : sessionData)
            DatabaseManager.saveSessionData(session);
        log("&e[TAI XIU] Save " + sessionData.size() + " dữ liệu thành công!");

    }

    public void cancel() {
        task.cancel();
    }

}
