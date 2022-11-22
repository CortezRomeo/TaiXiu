package com.cortezromeo.taixiu.manager;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.storage.SessionDataStorage;
import com.cortezromeo.taixiu.storage.loadingtype.PluginDisablingType;
import com.cortezromeo.taixiu.storage.loadingtype.SessionEndingType;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.cortezromeo.taixiu.manager.DebugManager.debug;
import static com.cortezromeo.taixiu.util.MessageUtil.log;

public class DatabaseManager {
    public static List<String> togglePlayers = new ArrayList<>();
    public static Map<Long, ISession> taiXiuData = new TreeMap<>();
    public static long lastSession;

    public static PluginDisablingType pluginDisablingType;
    public static SessionEndingType sessionEndingType;

    public static ISession getSessionData(long session) {
        if (!taiXiuData.containsKey(session)) {
            loadSessionData(session);
        }
        return taiXiuData.get(session);
    }

    public static long getLastSession() {
        if (taiXiuData.isEmpty())
            lastSession = 0;
        else {
            lastSession = Collections.max(taiXiuData.keySet());
        }

        return lastSession;
    }

    public static long getLastSessionFromFile() {

        File sessionFolder = new File(TaiXiu.plugin.getDataFolder() + "/session");
        File[] listOfFilesSession = sessionFolder.listFiles();

        if (listOfFilesSession.length == 0) {
            return 0;
        }

        List<Long> sessions = new ArrayList<>();

        for (int i = 0; i < listOfFilesSession.length; i++) {
            if (listOfFilesSession[i].isFile()) {

                File sessionFile = listOfFilesSession[i];
                String session = FilenameUtils.removeExtension(sessionFile.getName());

                try {
                    Long.parseLong(session);
                } catch (Exception e) {
                    continue;
                }

                sessions.add(Long.valueOf(session));
            }
        }

        return Collections.max(sessions);
    }

    public static void loadSessionData(long session) {

        if (taiXiuData.containsKey(session))
            return;

        taiXiuData.put(session, SessionDataStorage.getSessionData(session));
    }

    public static void saveSessionData(long session) {
        SessionDataStorage.saveTaiXiuData(session, taiXiuData.get(session));
    }

    public static void unloadSessionData(long session) {
        SessionDataStorage.saveTaiXiuData(session, taiXiuData.get(session));
        taiXiuData.remove(session);
    }

    public static boolean checkExistsFileData(long session) {

        if (taiXiuData.containsKey(session))
            return true;

        File file = new File(TaiXiu.plugin.getDataFolder() + "/session/" + String.valueOf(session) + ".yml");
        if (file.exists()) {
            try {
                return true;
            } catch (Exception e) {
                debug("ERROR " + ">>> " + e);
                debug("&b&lVUI LÒNG BÁO LẠI LỖI NÀY QUA DISCORD CỦA MÌNH: Cortez_Romeo#1290");
                return false;
            }
        }
        return false;
    }

    public static void loadLoadingType() {

        FileConfiguration config = TaiXiu.plugin.getConfig();

        sessionEndingType = SessionEndingType.valueOf(config.getString("database.while-ending-session.type").toUpperCase());
        pluginDisablingType = PluginDisablingType.valueOf(config.getString("database.while-disabling-plugin.type").toUpperCase());

    }

    public static void saveDatabase() {

        File sessionFolder = new File(TaiXiu.plugin.getDataFolder() + "/session");
        File[] listOfFilesSession = sessionFolder.listFiles();
        if (listOfFilesSession == null)
            return;

        if (DatabaseManager.pluginDisablingType == PluginDisablingType.SAVE_ALL) {
            Set<Long> sessionData = taiXiuData.keySet();
            log("&e[TAI XIU] Tiến hành save " + sessionData.size() + " dữ liệu...");
            for (long session : sessionData)
                saveSessionData(session);
            log("&e[TAI XIU] Save " + sessionData.size() + " dữ liệu thành công!");
        } else if (DatabaseManager.pluginDisablingType == PluginDisablingType.SAVE_LATEST) {
            long latestSession = DatabaseManager.getLastSession();

            log("&e[TAI XIU] Tiến hành save dữ liệu cuối cùng (Số " + latestSession + ")");
            saveSessionData(latestSession);
            log("&e[TAI XIU] Save dữ liệu số " + latestSession + " thành công!");
        } else {
            log("&e[TAI XIU] Tiến hành save dữ liệu số " + DatabaseManager.getLastSession() + " và xóa tất cả dữ liệu cũ...");

            int totalFiles = 0;
            for (int i = 0; i <= listOfFilesSession.length; i++) {
                if (listOfFilesSession[i].isFile()) {
                    totalFiles++;

                    File sessionFile = listOfFilesSession[i];
                    Long session = Long.valueOf(FilenameUtils.removeExtension(sessionFile.getName()));
                    if (session == getLastSessionFromFile()) {
                        totalFiles--;

                        if(taiXiuData.containsKey(session))
                            saveSessionData(session);
                        continue;
                    }

                    if(sessionFile.delete()) {
                        // stuffs
                    } else {
                        debug("&cERROR [COULD NOT DELETE FILE] " + ">>> " + session + ".yml");
                    }
                }
            }
            log("&e[TAI XIU] Save thành công dữ liệu số " + DatabaseManager.getLastSession() + " và xóa " + totalFiles + " dữ liệu!");
        }
    }

}
