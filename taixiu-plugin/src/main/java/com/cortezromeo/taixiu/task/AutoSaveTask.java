package com.cortezromeo.taixiu.task;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import com.tcoded.folialib.wrapper.task.WrappedTask;

import java.util.Set;

import static com.cortezromeo.taixiu.util.MessageUtil.log;

public class AutoSaveTask implements Runnable {

    private WrappedTask task;

    public AutoSaveTask(int time) {
        this.task = TaiXiu.support.getFoliaLib().getScheduler().runTimerAsync(this, 20L * time, 20L * time);
    }

    public WrappedTask getWrappedTask() {
        return task;
    }

    @Override
    public void run() {
        Set<Long> sessionData = DatabaseManager.taiXiuData.keySet();
        log("&e[TAI XIU] Saving " + sessionData.size() + " databases...");
        for (long session : sessionData)
            DatabaseManager.saveSessionData(session);
        log("&e[TAI XIU] Saved " + sessionData.size() + " databases!");

    }

    public void cancel() {
        task.cancel();
    }

}
