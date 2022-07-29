package com.cortezromeo.taixiu.manager;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.task.AutoSaveTask;

public class AutoSaveManager {

    private static AutoSaveTask autoSaveTask;

    public static void startAutoSave(int time) {

        if (TaiXiu.plugin.getConfig().getBoolean("auto-save-database.enable") && autoSaveStatus == true && autoSaveTask != null)
            return;

        autoSaveTask = new AutoSaveTask(time);
        autoSaveStatus = true;

    }

    public static void stopAutoSave() {

        if (autoSaveStatus = false  && autoSaveTask == null)
            return;

        autoSaveTask.cancel();
        autoSaveStatus = false;

    }

    public static void reloadTimeAutoSave() {

        if (!getAutoSaveStatus())
            return;

        stopAutoSave();
        startAutoSave(TaiXiu.plugin.getConfig().getInt("auto-save-database.time"));

    }

    public static boolean autoSaveStatus = false;

    public static boolean getAutoSaveStatus() {
        return autoSaveStatus;
    }

    public static void setAutoSaveStatus(boolean b) {
        autoSaveStatus = b;
    }

}
