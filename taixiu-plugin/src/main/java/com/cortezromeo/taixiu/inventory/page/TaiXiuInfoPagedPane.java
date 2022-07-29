package com.cortezromeo.taixiu.inventory.page;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.file.InventoryFile;
import com.cortezromeo.taixiu.inventory.Button;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import com.cortezromeo.taixiu.manager.TaiXiuManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.cortezromeo.taixiu.manager.DebugManager.debug;
import static com.cortezromeo.taixiu.util.ColorUtil.addColor;
import static com.cortezromeo.taixiu.util.ItemHeadUtil.*;
import static com.cortezromeo.taixiu.util.MessageUtil.getFormatName;

public class TaiXiuInfoPagedPane {

    private static boolean serverFullData;

    public static void openInventory(Player p, long session) {
        FileConfiguration invF = InventoryFile.get();
        PagedPane pagedPane = new PagedPane(invF.getInt("inventory.taiXiuInfo.rows") - 2,
                invF.getInt("inventory.taiXiuInfo.rows"),
                invF.getString("inventory.taiXiuInfo.title").replace("%session%", String.valueOf(session)), session);
        TaiXiuManager txm = TaiXiu.plugin.getManager();

        pagedPane.open(p);

        serverFullData = false;
        Bukkit.getScheduler().runTaskAsynchronously(TaiXiu.plugin, () -> {
            for (String player : txm.getSessionData(session).getXiuPlayers().keySet()) {
                try {
                    pagedPane.addButton(new Button(getItem(
                            invF.getString("inventory.taiXiuInfo.items.bet-player.type"),
                            invF.getString("inventory.taiXiuInfo.items.bet-player.value"),
                            invF.getString("inventory.taiXiuInfo.items.bet-player.name"),
                            invF.getStringList("inventory.taiXiuInfo.items.bet-player.lore"),
                            player, TaiXiuResult.XIU, session)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (String player : txm.getSessionData(session).getTaiPlayers().keySet()) {
                try {
                    pagedPane.addButton(new Button(getItem(
                            invF.getString("inventory.taiXiuInfo.items.bet-player.type"),
                            invF.getString("inventory.taiXiuInfo.items.bet-player.value"),
                            invF.getString("inventory.taiXiuInfo.items.bet-player.name"),
                            invF.getStringList("inventory.taiXiuInfo.items.bet-player.lore"),
                            player, TaiXiuResult.TAI, session)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static ItemStack getItem(String type, String value, String name, List<String> lore, String playerName, TaiXiuResult bet, Long session) {
        TaiXiuManager manager = TaiXiu.plugin.getManager();
        AtomicReference<ItemStack> material = new AtomicReference<>(new ItemStack(Material.BEDROCK));

        if (type.equalsIgnoreCase("customhead"))
            material.set(getCustomHead(value));

        if (type.equalsIgnoreCase("material"))
            material.set(new ItemStack(Material.valueOf(value)));

        if (type.equalsIgnoreCase("playerhead")) {
            Map<String, String> headData = DatabaseManager.HeadData;
            ItemStack defaulthead = new ItemStack(Material.PLAYER_HEAD);
            if (headData.containsKey(playerName))
                if (headData.get(playerName).equals("none"))
                    material.set(defaulthead);
                else
                    material.set(getCustomHead(headData.get(playerName)));
            else {
                if (!serverFullData) {
                    if (getURLContent("https://api.mojang.com/users/profiles/minecraft/Cortez_Romeo").equals("TooManyRequestsException")) {
                        serverFullData = true;
                        material.set(defaulthead);
                        debug("&cERROR &eDữ liệu lấy từ API của Mojang đã bị quá tải, toàn bộ playerhead sẽ được set về default" +
                                ". Lỗi này sẽ được fix trong 5 phút hoặc hơn");
                    } else {
                        String headvalue = getHeadValue(playerName);
                        if (headvalue != null) {
                            headData.put(playerName, headvalue);
                            material.set(getCustomHead(headvalue));
                        } else {
                            headData.put(playerName, "none");
                            material.set(defaulthead);
                        }
                    }
                } else material.set(defaulthead);
            }
        }

        ItemMeta materialMeta = material.get().getItemMeta();

        if (name != "") {
            name = name
                    .replaceAll("%playerName%", playerName)
                    .replaceAll("%bet%", getFormatName(bet))
                    .replaceAll("%money%",
                            (bet == TaiXiuResult.XIU
                                    ? String.valueOf(manager.getSessionData(session).getXiuPlayers().get(playerName))
                                    : String.valueOf(manager.getSessionData(session).getTaiPlayers().get(playerName))));


            materialMeta.setDisplayName(addColor(name));
        }

        List<String> newList = new ArrayList<String>();
        for (String string : lore) {
            string = string
                    .replaceAll("%playerName%", playerName)
                    .replaceAll("%bet%", getFormatName(bet))
                    .replaceAll("%money%",
                            (bet == TaiXiuResult.XIU
                                    ? String.valueOf(manager.getSessionData(session).getXiuPlayers().get(playerName))
                                    : String.valueOf(manager.getSessionData(session).getTaiPlayers().get(playerName))));

            newList.add(addColor(string));
        }

        materialMeta.setLore(newList);

        material.get().setItemMeta(materialMeta);
        return material.get();

    }

}
