package com.cortezromeo.taixiu.storage;

import com.cortezromeo.taixiu.api.storage.ISession;

public interface SessionStorage {

    void saveData(long session, ISession data);

    ISession getData(long session);
}
