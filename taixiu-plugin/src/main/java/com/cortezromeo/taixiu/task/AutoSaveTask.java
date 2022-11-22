package com.cortezromeo.taixiu.task;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Set;

import static com.cortezromeo.taixiu.util.MessageUtil.log;

public class AutoSaveTask implements Runnable {

    private BukkitTask task;

    public AutoSaveTask(int time) {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(TaiXiu.plugin, this, 20L * time, 20L * time);
    }

    public BukkitTask getBukkitTask() {
        return task;
    }

    public int getTaskID() {
        return task.getTaskId();
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
