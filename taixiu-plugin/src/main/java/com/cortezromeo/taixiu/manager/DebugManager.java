package com.cortezromeo.taixiu.manager;

import com.cortezromeo.taixiu.util.ColorUtil;
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

        Bukkit.getConsoleSender().sendMessage(ColorUtil.addColor("&f[TAIXIU-DEBUG] &e" + message));
    }

}
