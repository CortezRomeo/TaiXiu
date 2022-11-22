package com.cortezromeo.taixiu.manager;

import com.cortezromeo.taixiu.TaiXiu;
import org.bukkit.Bukkit;

public class DebugManager {

    public static boolean debug;

    public static boolean getDebug() {
        return debug;
    }

    public static void setDebug(boolean b) {
        debug = b;
    }

    public static void debug(String message) {

        if (!debug)
            return;

        Bukkit.getConsoleSender().sendMessage(TaiXiu.nms.addColor("&6[TAI XIU DEBUG] &e" + message));
    }

}
