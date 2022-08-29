package com.cortezromeo.taixiu.inventory.page;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.file.InventoryFile;
import com.cortezromeo.taixiu.inventory.Button;
import com.cortezromeo.taixiu.manager.TaiXiuManager;
import com.cortezromeo.taixiu.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.cortezromeo.taixiu.util.MessageUtil.getFormatName;

public class TaiXiuInfoPagedPane {

    public static void openInventory(Player p, long session) {
        FileConfiguration invF = InventoryFile.get();
        PagedPane pagedPane = new PagedPane(invF.getInt("inventory.taiXiuInfo.rows") - 2,
                invF.getInt("inventory.taiXiuInfo.rows"),
                invF.getString("inventory.taiXiuInfo.title").replace("%session%", String.valueOf(session)), session);

        pagedPane.open(p);

        Bukkit.getScheduler().runTaskAsynchronously(TaiXiu.plugin, () -> {
            for (String player : TaiXiuManager.getSessionData(session).getXiuPlayers().keySet()) {
                try {
                    pagedPane.addButton(new Button(getItem(
                            invF.getString("inventory.taiXiuInfo.items.bet-player.type"),
                            invF.getString("inventory.taiXiuInfo.items.bet-player.value"),
                            (short) invF.getInt("inventory.taiXiuInfo.items.bet-player.data"),
                            invF.getString("inventory.taiXiuInfo.items.bet-player.name"),
                            invF.getStringList("inventory.taiXiuInfo.items.bet-player.lore"),
                            player, TaiXiuResult.XIU, session)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (String player : TaiXiuManager.getSessionData(session).getTaiPlayers().keySet()) {
                try {
                    pagedPane.addButton(new Button(getItem(
                            invF.getString("inventory.taiXiuInfo.items.bet-player.type"),
                            invF.getString("inventory.taiXiuInfo.items.bet-player.value"),
                            (short) invF.getInt("inventory.taiXiuInfo.items.bet-player.data"),
                            invF.getString("inventory.taiXiuInfo.items.bet-player.name"),
                            invF.getStringList("inventory.taiXiuInfo.items.bet-player.lore"),
                            player, TaiXiuResult.TAI, session)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static ItemStack getItem(String type, String value, short data, String name, List<String> lore, String playerName, TaiXiuResult bet, Long session) {
        AtomicReference<ItemStack> material = new AtomicReference<>(new ItemStack(Material.BEDROCK));

        if (type.equalsIgnoreCase("customhead"))
            material.set(TaiXiu.nms.getHeadItem(value));

        if (type.equalsIgnoreCase("material"))
            material.set(TaiXiu.nms.createItemStack(value, 1, data));

        if (type.equalsIgnoreCase("playerhead"))
            material.set(TaiXiu.nms.getHeadItem(playerName));

        ItemMeta materialMeta = material.get().getItemMeta();

        if (name != "") {
            name = name
                    .replaceAll("%playerName%", playerName)
                    .replaceAll("%bet%", getFormatName(bet))
                    .replaceAll("%money%",
                            (bet == TaiXiuResult.XIU
                                    ? MessageUtil.formatMoney(TaiXiuManager.getSessionData(session).getXiuPlayers().get(playerName))
                                    : MessageUtil.formatMoney(TaiXiuManager.getSessionData(session).getTaiPlayers().get(playerName))));


            materialMeta.setDisplayName(TaiXiu.nms.addColor(name));
        }

        List<String> newList = new ArrayList<String>();
        for (String string : lore) {
            string = string
                    .replaceAll("%playerName%", playerName)
                    .replaceAll("%bet%", getFormatName(bet))
                    .replaceAll("%money%",
                            (bet == TaiXiuResult.XIU
                                    ? MessageUtil.formatMoney(TaiXiuManager.getSessionData(session).getXiuPlayers().get(playerName))
                                    : MessageUtil.formatMoney(TaiXiuManager.getSessionData(session).getTaiPlayers().get(playerName))));

            newList.add(TaiXiu.nms.addColor(string));
        }

        materialMeta.setLore(newList);

        material.get().setItemMeta(materialMeta);
        return material.get();

    }

}
