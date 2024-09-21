package com.cortezromeo.taixiu.inventory.page;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.file.InventoryFile;
import com.cortezromeo.taixiu.inventory.Button;
import com.cortezromeo.taixiu.manager.TaiXiuManager;
import com.cortezromeo.taixiu.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import static com.cortezromeo.taixiu.util.MessageUtil.getFormatName;

/**
 * A paged pane. Credits @ I Al Ianstaan
 * Edited by @ Cortez Romeo
 */
public class PagedPane implements InventoryHolder {

    @SuppressWarnings("WeakerAccess")
    protected Button controlBack;
    @SuppressWarnings("WeakerAccess")
    protected Button controlNext;
    private final Inventory inventory;
    private final SortedMap<Integer, Page> pages = new TreeMap<>();
    private int currentIndex;
    private final int pageSize;
    private final long session;

    public PagedPane(int pageSize, int rows, String title, long session) {
        Objects.requireNonNull(title, "TITLE KHÔNG ĐƯỢC ĐỂ TRỐNG");
        String rowserror = "ROWS CẦN BÉ HƠN 6 HOẶC LỚN 1. ĐANG CÓ " + rows;
        if (rows > 6) {
            throw new IllegalArgumentException(rowserror);
        }
        if (pageSize > 6) {
            throw new IllegalArgumentException(rowserror);
        }

        this.pageSize = pageSize;
        this.session = session;
        inventory = Bukkit.createInventory(this, rows * 9, color(title));

        pages.put(0, new Page(pageSize));
    }

    public void addButton(Button button) {
        for (Entry<Integer, Page> entry : pages.entrySet()) {
            if (entry.getValue().addButton(button)) {
                if (entry.getKey() == currentIndex) {
                    reRender();
                }
                return;
            }
        }
        Page page = new Page(pageSize);
        page.addButton(button);
        pages.put(pages.lastKey() + 1, page);

        reRender();
    }

    @SuppressWarnings("unused")
    public void removeButton(Button button) {
        for (Iterator<Entry<Integer, Page>> iterator = pages.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<Integer, Page> entry = iterator.next();
            if (entry.getValue().removeButton(button)) {

                // we may need to delete the page
                if (entry.getValue().isEmpty()) {
                    // we have more than one page, so delete it
                    if (pages.size() > 1) {
                        iterator.remove();
                    }
                    // the currentIndex now points to a page that does not exist. Correct it.
                    if (currentIndex >= pages.size()) {
                        currentIndex--;
                    }
                }
                // if we modified the current one, re-render
                // if we deleted the current page, re-render too
                if (entry.getKey() >= currentIndex) {
                    reRender();
                }
                return;
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public int getPageAmount() {
        return pages.size();
    }

    @SuppressWarnings("WeakerAccess")
    public int getCurrentPage() {
        return currentIndex + 1;
    }

    @SuppressWarnings("WeakerAccess")
    public void selectPage(int index) {
        if (index < 0 || index >= getPageAmount()) {
            throw new IllegalArgumentException(
                    "Index out of bounds s: " + index + " [" + 0 + " " + getPageAmount() + ")"
            );
        }
        if (index == currentIndex) {
            return;
        }

        currentIndex = index;
        reRender();
    }

    @SuppressWarnings("WeakerAccess")
    public void reRender() {
        inventory.clear();
        pages.get(currentIndex).render(inventory);

        controlBack = null;
        controlNext = null;
        createControls(inventory);
    }

    @SuppressWarnings("WeakerAccess")
    public void onClick(InventoryClickEvent event) {
        FileConfiguration invF = InventoryFile.get();
        event.setCancelled(true);

        // info item
        if (event.getSlot() == (inventory.getSize() - 10) + invF.getInt("inventory.taiXiuInfo.items.bet-info.slot")) {
            reRender();
            return;
        }

        // back item
        if (event.getSlot() == (inventory.getSize() - 10) + invF.getInt("inventory.default-items.prevPage.slot")) {
            if (controlBack != null) {
                controlBack.onClick(event);
            }
            return;
        }

        // next item
        else if (event.getSlot() == (inventory.getSize() - 10) + invF.getInt("inventory.default-items.nextPage.slot")) {
            if (controlNext != null) {
                controlNext.onClick(event);
            }
            return;
        }

        pages.get(currentIndex).handleClick(event);

    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @SuppressWarnings("WeakerAccess")
    protected void createControls(Inventory inventory) {
        FileConfiguration invF = InventoryFile.get();
        // create separator
        fillRow(
                inventory.getSize() / 9 - 2,
                getItem(
                        invF.getString("inventory.default-items.borderItem.type"),
                        invF.getString("inventory.default-items.borderItem.value"),
                        (short) invF.getInt("inventory.default-items.borderItem.data"),
                        invF.getString("inventory.default-items.borderItem.name"),
                        invF.getStringList("inventory.default-items.borderItem.lore"))
                , inventory
        );

        if (getCurrentPage() > 1) {
            ItemStack itemStack = getItem(
                    invF.getString("inventory.default-items.prevPage.type"),
                    invF.getString("inventory.default-items.prevPage.value"),
                    (short) invF.getInt("inventory.default-items.prevPage.data"),
                    invF.getString("inventory.default-items.prevPage.name"),
                    invF.getStringList("inventory.default-items.prevPage.lore"));
            controlBack = new Button(itemStack, event -> selectPage(currentIndex - 1));
            inventory.setItem((inventory.getSize() - 10) + invF.getInt("inventory.default-items.prevPage.slot"), itemStack);
        }

        if (getCurrentPage() < getPageAmount()) {
            ItemStack itemStack = getItem(
                    invF.getString("inventory.default-items.nextPage.type"),
                    invF.getString("inventory.default-items.nextPage.value"),
                    (short) invF.getInt("inventory.default-items.nextPage.data"),
                    invF.getString("inventory.default-items.nextPage.name"),
                    invF.getStringList("inventory.default-items.nextPage.lore"));
            controlNext = new Button(itemStack, event -> selectPage(getCurrentPage()));
            inventory.setItem((inventory.getSize() - 10) + invF.getInt("inventory.default-items.nextPage.slot"), itemStack);
        }

        ItemStack itemStack = getItem(
                invF.getString("inventory.taiXiuInfo.items.bet-info.type"),
                invF.getString("inventory.taiXiuInfo.items.bet-info.value"),
                (short) invF.getInt("inventory.taiXiuInfo.items.bet-info.data"),
                invF.getString("inventory.taiXiuInfo.items.bet-info.name"),
                (TaiXiuManager.getSessionData(session).getResult() != TaiXiuResult.NONE ? invF.getStringList("inventory.taiXiuInfo.items.bet-info.loreEnded") : invF.getStringList("inventory.taiXiuInfo.items.bet-info.lorePlaying")));
        inventory.setItem((inventory.getSize() - 10) + invF.getInt("inventory.taiXiuInfo.items.bet-info.slot"), itemStack);

    }

    private void fillRow(int rowIndex, ItemStack itemStack, Inventory inventory) {
        int yMod = rowIndex * 9;
        for (int i = 0; i < 9; i++) {
            int slot = yMod + i;
            inventory.setItem(slot, itemStack);
        }
    }

    protected ItemStack getItem(String type, String value, short itemData, String name, List<String> lore) {
        AtomicReference<ItemStack> material = new AtomicReference<>(new ItemStack(Material.BEDROCK));

        if (type.equalsIgnoreCase("customhead"))
            material.set(TaiXiu.nms.getHeadItem(value, 1));
        else if (type.equalsIgnoreCase("playerhead"))
            material.set(TaiXiu.nms.getHeadItem(value, 2));
        
        if (type.equalsIgnoreCase("material"))
            material.set(TaiXiu.nms.createItemStack(value, 1, itemData));

        ItemMeta materialMeta = material.get().getItemMeta();
        ISession data = TaiXiuManager.getSessionData(this.session);

        if (!Objects.equals(name, "")) {
            name = getString(data, name);
            materialMeta.setDisplayName(TaiXiu.nms.addColor(name));
        }

        List<String> newList = new ArrayList<>();
        for (String string : lore) {
            string = getString(data, string);
            newList.add(TaiXiu.nms.addColor(string));
        }

        materialMeta.setLore(newList);

        material.get().setItemMeta(materialMeta);
        return material.get();

    }

    @NotNull
    private String getString(ISession data, String string) {

        string = string
                .replace("%nextPage%", String.valueOf(getCurrentPage() + 1))
                .replace("%prevPage%", String.valueOf(getCurrentPage() - 1))
                .replace("%time%", (TaiXiuManager.getSessionData(session).getResult() != TaiXiuResult.NONE ? "0" : String.valueOf(TaiXiuManager.getTime())))
                .replace("%session%", String.valueOf(this.session))
                .replace("%xiuPlayerNumber%", String.valueOf(data.getXiuPlayers().size()))
                .replace("%taiPlayerNumber%", String.valueOf(data.getTaiPlayers().size()))
                .replace("%xiuTotalBet%", MessageUtil.formatMoney(TaiXiuManager.getXiuBet(data)))
                .replace("%taiTotalBet%", MessageUtil.formatMoney(TaiXiuManager.getTaiBet(data)))
                .replace("%totalBet%", MessageUtil.formatMoney(TaiXiuManager.getTotalBet(data)))
                .replace("%bestWinners%", TaiXiuManager.getBestWinner(TaiXiuManager.getSessionData(session)))
                .replace("%dice1%", String.valueOf(data.getDice1()))
                .replace("%dice2%", String.valueOf(data.getDice2()))
                .replace("%dice3%", String.valueOf(data.getDice3()))
                .replace("%result%", getFormatName(data.getResult()));
        return string;
    }

    @SuppressWarnings("WeakerAccess")
    protected String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public void open(Player player) {
        reRender();
        player.openInventory(getInventory());
    }


    private static class Page {
        private List<Button> buttons = new ArrayList<>();
        private int maxSize;

        Page(int maxSize) {
            this.maxSize = maxSize;
        }

        void handleClick(InventoryClickEvent event) {
            if (event.getRawSlot() > event.getInventory().getSize()) {
                return;
            }
            if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) {
                return;
            }
            if (event.getSlot() >= buttons.size()) {
                return;
            }
            Button button = buttons.get(event.getSlot());
            button.onClick(event);
        }

        boolean hasSpace() {
            return buttons.size() < maxSize * 9;
        }

        boolean addButton(Button button) {
            if (!hasSpace()) {
                return false;
            }
            buttons.add(button);

            return true;
        }

        boolean removeButton(Button button) {
            return buttons.remove(button);
        }

        void render(Inventory inventory) {
            for (int i = 0; i < buttons.size(); i++) {
                Button button = buttons.get(i);

                inventory.setItem(i, button.getItemStack());
            }
        }

        boolean isEmpty() {
            return buttons.isEmpty();
        }
    }

}