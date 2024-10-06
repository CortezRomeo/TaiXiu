package com.cortezromeo.taixiu.inventory;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.file.inventory.TaiXiuInfoInventoryFile;
import com.cortezromeo.taixiu.util.ItemUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class PaginatedInventory extends TaiXiuInventoryBase {

    protected int page = 0;
    protected int maxItemsPerPage = 28;
    protected int index = 0;

    public PaginatedInventory(Player owner) {
        super(owner);
    }

    //Set the border and menu buttons for the menu
    public void addPaginatedMenuItems() {
        FileConfiguration invFileConfig = TaiXiuInfoInventoryFile.get();

        ItemStack borderItem = ItemUtil.getItem(invFileConfig.getString("items.border.type"),
                invFileConfig.getString("items.border.value"),
                (short) invFileConfig.getInt("items.border.data"),
                invFileConfig.getString("items.border.name"),
                invFileConfig.getStringList("items.border.lore"));

        ItemStack closeItem = TaiXiu.nms.addCustomData(ItemUtil.getItem(invFileConfig.getString("items.close.type"),
                invFileConfig.getString("items.close.value"),
                (short) invFileConfig.getInt("items.close.data"),
                invFileConfig.getString("items.close.name"),
                invFileConfig.getStringList("items.close.lore")), "closeItem");
        int closeItemSlot = invFileConfig.getInt("items.close.slot");
        if (closeItemSlot < 0)
            closeItemSlot = 0;
        if (closeItemSlot > 8)
            closeItemSlot = 8;
        closeItemSlot = 45 + closeItemSlot;

        ItemStack prevItem = TaiXiu.nms.addCustomData(ItemUtil.getItem(invFileConfig.getString("items.prevPage.type"),
                invFileConfig.getString("items.prevPage.value"),
                (short) invFileConfig.getInt("items.prevPage.data"),
                invFileConfig.getString("items.prevPage.name"),
                invFileConfig.getStringList("items.prevPage.lore")), "prevPage");
        int prevPageItemSlot = invFileConfig.getInt("items.prevPage.slot");
        if (prevPageItemSlot < 0)
            prevPageItemSlot = 0;
        if (prevPageItemSlot > 8)
            prevPageItemSlot = 8;
        prevPageItemSlot = 45 + prevPageItemSlot;

        ItemStack nextItem = TaiXiu.nms.addCustomData(ItemUtil.getItem(invFileConfig.getString("items.nextPage.type"),
                invFileConfig.getString("items.nextPage.value"),
                (short) invFileConfig.getInt("items.nextPage.data"),
                invFileConfig.getString("items.nextPage.name"),
                invFileConfig.getStringList("items.nextPage.lore")), "nextPage");
        int nextPageItemSlot = invFileConfig.getInt("items.nextPage.slot");
        if (nextPageItemSlot < 0)
            nextPageItemSlot = 0;
        if (nextPageItemSlot > 8)
            nextPageItemSlot = 8;
        nextPageItemSlot = 45 + nextPageItemSlot;

        if (page > 0)
            inventory.setItem(prevPageItemSlot, getPageItemStack(prevItem));
        inventory.setItem(closeItemSlot, closeItem);
        inventory.setItem(nextPageItemSlot, getPageItemStack(nextItem));

        for (int i = 0; i < 10; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, borderItem);
            }
        }
        inventory.setItem(17, borderItem);
        inventory.setItem(18, borderItem);
        inventory.setItem(26, borderItem);
        inventory.setItem(27, borderItem);
        inventory.setItem(35, borderItem);
        inventory.setItem(36, borderItem);
        for (int i = 44; i < 54; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, borderItem);
            }
        }
    }

    private @NotNull ItemStack getPageItemStack(ItemStack itemStack) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();

        List<String> itemLore = itemMeta.getLore();
        itemLore.replaceAll(string -> TaiXiu.nms.addColor(string.replace("%page%", String.valueOf(page))
                .replace("%nextPage%", String.valueOf(page + 2))
                .replace("%prevPage%", String.valueOf(page > 0 ? page : 0))));
        itemMeta.setLore(itemLore);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }
}
