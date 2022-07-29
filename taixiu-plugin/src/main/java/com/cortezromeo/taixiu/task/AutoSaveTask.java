package com.cortezromeo.taixiu.task;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.task.AutoSaveTaskAPI;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

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

        DatabaseManager.saveAllDatabase();
        log("&7[TAIXIU-AUTOSAVE] Database saved!");

    }

    public void cancel() {
        task.cancel();
    }

}
