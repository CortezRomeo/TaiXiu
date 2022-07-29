package com.cortezromeo.taixiu.storage;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.storage.ISession;

import java.io.File;

public class SessionDataStorage {

    private static SessionStorage STORAGE;

    public static void init() {
        File file = new File(TaiXiu.plugin.getDataFolder() + "/session/");
        if (!file.exists()) {
            file.mkdirs();
        }
        SessionDataStorage.STORAGE = new SessionFileStorage();
    }

    public static ISession getSessionData(long session) {
        return SessionDataStorage.STORAGE.getData(session);
    }

    public static void saveTaiXiuData(long session, ISession data) {
        SessionDataStorage.STORAGE.saveData(session, data);
    }

}
