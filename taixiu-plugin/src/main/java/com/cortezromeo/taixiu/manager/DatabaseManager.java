package com.cortezromeo.taixiu.manager;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.storage.SessionDataStorage;
import com.cortezromeo.taixiu.storage.loadingtype.PluginDisablingType;
import com.cortezromeo.taixiu.storage.loadingtype.SessionEndingType;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;

import static com.cortezromeo.taixiu.manager.DebugManager.debug;
import static com.cortezromeo.taixiu.util.MessageUtil.log;
import static com.cortezromeo.taixiu.util.MessageUtil.thowErrorMessage;

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

        debug("LOADING SESSION DATA", "Session number " + session);
        if (taiXiuData.containsKey(session))
            return;

        taiXiuData.put(session, SessionDataStorage.getSessionData(session));
        debug("SESSION DATA LOADED", "Session number " + session);
    }

    public static void saveSessionData(long session) {

        debug("SAVING SESSION DATA", "Session number " + session);
        SessionDataStorage.saveTaiXiuData(session, taiXiuData.get(session));
        debug("SESSION DATA SAVED", "Session number " + session);
    }

    public static void unloadSessionData(long session) {

        debug("UNLOADING SESSION DATA", "Session number " + session);
        SessionDataStorage.saveTaiXiuData(session, taiXiuData.get(session));
        taiXiuData.remove(session);
        debug("SESSION DATA UNLOADED", "Session number " + session);
    }

    public static boolean checkExistsFileData(long session) {

        if (taiXiuData.containsKey(session))
            return true;

        File file = new File(TaiXiu.plugin.getDataFolder() + "/session/" + String.valueOf(session) + ".yml");
        if (file.exists()) {
            try {
                return true;
            } catch (Exception e) {
                thowErrorMessage("" + e);
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
            log("&e[TAI XIU] Ti???n h??nh save " + sessionData.size() + " d??? li???u...");
            for (long session : sessionData)
                saveSessionData(session);
            log("&e[TAI XIU] Save " + sessionData.size() + " d??? li???u th??nh c??ng!");
        } else if (DatabaseManager.pluginDisablingType == PluginDisablingType.SAVE_LATEST) {
            long latestSession = DatabaseManager.getLastSession();

            log("&e[TAI XIU] Ti???n h??nh save d??? li???u cu???i c??ng (S??? " + latestSession + ")");
            saveSessionData(latestSession);
            log("&e[TAI XIU] Save d??? li???u s??? " + latestSession + " th??nh c??ng!");
        } else {
            log("&e[TAI XIU] Ti???n h??nh save d??? li???u s??? " + DatabaseManager.getLastSession() + " v?? x??a t???t c??? d??? li???u c??...");

            int totalFiles = 0;
            for (int i = 0; i < listOfFilesSession.length; i++) {
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
                        thowErrorMessage("KH??NG TH??? X??A FILE " + sessionFile.getName());
                    }
                }
            }
            log("&e[TAI XIU] Save th??nh c??ng d??? li???u s??? " + DatabaseManager.getLastSession() + " v?? x??a " + totalFiles + " d??? li???u!");
        }
    }

}
