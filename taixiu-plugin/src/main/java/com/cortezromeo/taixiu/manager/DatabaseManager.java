package com.cortezromeo.taixiu.manager;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.storage.SessionDataStorage;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.*;

public class DatabaseManager {
    public static List<String> togglePlayers = new ArrayList<>();
    public static Map<Long, ISession> taiXiuData = new TreeMap<>();
    public static long lastSession;

    public static ISession getSessionData(long session) {
        if (!taiXiuData.containsKey(session)) {
            loadSessionData(session);
        }
        return taiXiuData.get(session);
    }

    public static long getLastSession() {
        if (taiXiuData.isEmpty())
            lastSession = 1;
        else {
            lastSession = Collections.max(taiXiuData.keySet()) + 1;

            if (lastSession > 1)
                if (getSessionData(lastSession - 1).getResult() == TaiXiuResult.NONE)
                    lastSession--;
        }

        return lastSession;
    }

    public static void loadSessionData(long session) {
        taiXiuData.put(session, SessionDataStorage.getSessionData(session));
    }

    public static void saveSessionData(long session) {
        SessionDataStorage.saveTaiXiuData(session, taiXiuData.get(session));
    }

    public static void unloadSessionData(long session) {
        SessionDataStorage.saveTaiXiuData(session, taiXiuData.get(session));
        taiXiuData.remove(session);
    }

    public static void loadAllDatabase() {

        File sessionFolder = new File(TaiXiu.plugin.getDataFolder() + "/session");
        File[] listOfFilesSession = sessionFolder.listFiles();
        if (listOfFilesSession == null)
            return;

        for (int i = 0; i < listOfFilesSession.length; i++) {
            if (listOfFilesSession[i].isFile()) {

                File playerFile = listOfFilesSession[i];
                String session = FilenameUtils.removeExtension(playerFile.getName());
                loadSessionData(Integer.valueOf(session));

            }
        }
    }

    public static void saveAllDatabase() {

        Set<Long> sessionData = taiXiuData.keySet();
        for (long session : sessionData)
            saveSessionData(session);
    }

}
