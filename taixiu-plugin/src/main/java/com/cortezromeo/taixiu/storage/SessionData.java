package com.cortezromeo.taixiu.storage;

import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.storage.ISession;

import java.util.HashMap;

public class SessionData implements ISession {

    private long session;
    private int dice1;
    private int dice2;
    private int dice3;
    private HashMap<String, Long> taiPlayers;
    private HashMap<String, Long> xiuPlayers;
    private TaiXiuResult result;

    public SessionData(long session, int dice1, int dice2, int dice3, TaiXiuResult result, HashMap<String, Long> taiPlayers, HashMap<String, Long> xiuPlayers) {
        this.session = session;
        this.dice1 = dice1;
        this.dice2 = dice2;
        this.dice3 = dice3;
        this.result = result;
        this.taiPlayers = taiPlayers;
        this.xiuPlayers = xiuPlayers;
    }

    @Override
    public long getSession() {
        return session;
    }

    @Override
    public void setSession(long session) {
        this.session = session;
    }

    @Override
    public int getDice1() {
        return dice1;
    }

    @Override
    public void setDice1(int dice1) {
        this.dice1 = dice1;
    }

    @Override
    public int getDice2() {
        return dice2;
    }

    @Override
    public void setDice2(int dice2) {
        this.dice2 = dice2;
    }

    @Override
    public int getDice3() {
        return dice3;
    }

    @Override
    public void setDice3(int dice3) {
        this.dice3 = dice3;
    }

    @Override
    public TaiXiuResult getResult() {
        return result;
    }

    @Override
    public void setResult(TaiXiuResult result) {
        this.result = result;
    }

    @Override
    public HashMap<String, Long> getTaiPlayers() {
        return taiPlayers;
    }

    @Override
    public HashMap<String, Long> getXiuPlayers() {
        return xiuPlayers;
    }

    @Override
    public void addTaiPlayer(String playerName, Long money) {
        getTaiPlayers().put(playerName, money);
    }

    @Override
    public void addXiuPlayer(String playerName, Long money) {
        getXiuPlayers().put(playerName, money);
    }

    @Override
    public void removeTaiPlayer(String playerName) {
        taiPlayers.remove(playerName);
    }

    @Override
    public void removeXiuPlayer(String playerName) {
        xiuPlayers.remove(playerName);
    }

    @Override
    public void setTaiPlayer(HashMap<String, Long> hashmap) {
        taiPlayers = hashmap;
    }

    @Override
    public void setXiuPlayer(HashMap<String, Long> hashmap) {
        xiuPlayers = hashmap;
    }
}
