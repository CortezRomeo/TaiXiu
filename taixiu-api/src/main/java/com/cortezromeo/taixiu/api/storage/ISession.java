package com.cortezromeo.taixiu.api.storage;

import com.cortezromeo.taixiu.api.TaiXiuResult;

import java.util.HashMap;

public interface ISession {

    long getSession();

    void setSession(long session);

    int getDice1();

    void setDice1(int dice1);

    int getDice2();

    void setDice2(int dice2);

    int getDice3();

    void setDice3(int dice3);

    TaiXiuResult getResult();

    void setResult(TaiXiuResult result);

    HashMap<String, Long> getTaiPlayers();

    HashMap<String, Long> getXiuPlayers();

    void addTaiPlayer(String playerName, Long money);

    void addXiuPlayer(String playerName, Long money);

    void removeTaiPlayer(String playerName);

    void removeXiuPlayer(String playerName);

    void setTaiPlayer(HashMap<String, Long> hashmap);

    void setXiuPlayer(HashMap<String, Long> hashmap);
}
