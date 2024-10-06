package com.cortezromeo.taixiu.storage;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.CurrencyTyppe;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.storage.ISession;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SessionFileStorage implements SessionStorage  {

    private static File getFile(long session) {
        File file = new File(TaiXiu.plugin.getDataFolder() + "/session/" + String.valueOf(session) + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static SessionData fromFile(File file, long session) {
        final YamlConfiguration storage = YamlConfiguration.loadConfiguration(file);

        HashMap<String, Long> taiPlayers = new HashMap<>();
        HashMap<String, Long> xiuPlayers = new HashMap<>();
        SessionData data = new SessionData(session, 0, 0, 0, TaiXiuResult.NONE, taiPlayers, xiuPlayers, CurrencyTyppe.valueOf(TaiXiu.plugin.getConfig().getString("currency-settings.default").toUpperCase()));

        if (!storage.contains("data"))
            return data;

        data.setSession(storage.getLong("data.session"));
        data.setDice1(storage.getInt("data.dice1"));
        data.setDice2(storage.getInt("data.dice2"));
        data.setDice3(storage.getInt("data.dice3"));
        if (storage.getString("data.currency") != null)
            data.setCurrencyType(CurrencyTyppe.valueOf(storage.getString("data.currency").toUpperCase()));
        data.setCurrencyType(CurrencyTyppe.VAULT);
        data.setResult(TaiXiuResult.valueOf(storage.getString("data.result")));
        if (storage.get("data.taiPlayers") != null)
            for (String player : storage.getConfigurationSection("data.taiPlayers").getKeys(false))
                data.addTaiPlayer(player, storage.getLong("data.taiPlayers." + player));
        if (storage.get("data.xiuPlayers") != null)
            for (String player : storage.getConfigurationSection("data.xiuPlayers").getKeys(false))
                data.addXiuPlayer(player, storage.getLong("data.xiuPlayers." + player));

        return data;
    }

    public static void saveDataDirect(long session, ISession data) {
        final File file = getFile(session);
        final YamlConfiguration storage = YamlConfiguration.loadConfiguration(file);

        storage.set("data.session", data.getSession());
        storage.set("data.dice1", data.getDice1());
        storage.set("data.dice2", data.getDice2());
        storage.set("data.dice3", data.getDice3());
        storage.set("data.result", data.getResult().toString());
        if (data.getCurrencyType() == null)
            data.setCurrencyType(CurrencyTyppe.VAULT);
        storage.set("data.currency", data.getCurrencyType().toString());
        if (data.getTaiPlayers() != null) {
            List<String> listTaiPlayersKey = new ArrayList<String>(data.getTaiPlayers().keySet());
            for (String key : listTaiPlayersKey)
                storage.set("data.taiPlayers." + key, data.getTaiPlayers().get(key));
        }
        if (data.getXiuPlayers() != null) {
            List<String> listXiuPlayersKey = new ArrayList<String>(data.getXiuPlayers().keySet());
            for (String key : listXiuPlayersKey)
                storage.set("data.xiuPlayers." + key, data.getXiuPlayers().get(key));
        }

        try {
            storage.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveData(long session, ISession data) {
        saveDataDirect(session, data);
    }

    public ISession getData(long session) {
        final File file = getFile(session);
        return fromFile(file, session);
    }

}
