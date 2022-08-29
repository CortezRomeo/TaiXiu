package com.cortezromeo.taixiu.file;

import com.cortezromeo.taixiu.TaiXiu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class InventoryFile {

    private static File file;
    private static FileConfiguration inventoryFile;

    public static void setup() {
        file = new File(TaiXiu.plugin.getDataFolder() + "/inventory.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        inventoryFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return inventoryFile;
    }

    public static void fileExists() {
        file = new File(TaiXiu.plugin.getDataFolder() + "/inventory.yml");
        inventoryFile = YamlConfiguration.loadConfiguration(file);
    }

    public static void save() {
        try {
            inventoryFile.save(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        inventoryFile = YamlConfiguration.loadConfiguration(file);
    }

    public static void setupLang() {

        get().options().header(
                "\n" +
                        "    ▀▀█▀▀  █▀▀█ ▀█▀   ▀▄ ▄▀ ▀█▀  █  █\n" +
                        "      █    █▄▄█  █      █    █   █  █\n" +
                        "      █    █  █ ▄█▄   ▄▀ ▀▄ ▄█▄  ▀▄▄▀\n" +
                        "\n" +
                        " Author: Cortez_Romeo\n" +
                        " Download plugin này miễn phí tại: https://minecraftvn.net\n" +
                        "\n" +
                        " Lưu ý:\n" +
                        " * Có hỗ trợ hex color\n" +
                        " * Có 3 type chính, đó là:\n" +
                        "   + material: value chỉnh thành MATERIAL của block\n" +
                        "     > Theo các phiên bản bé hơn 1.13, bạn có thể điền số trong phần 'value:' và 'data:' của item đó" +
                        "         Ví dụ: (value: 95 | data: 1) [https://minecraft-ids.grahamedgecombe.com/]" +
                        "     > Theo các phiên bản lớn hơn 1.12.2, bạn có thể điền thẳng tên item đó (Phần 'data:' sẽ không còn cần thiết)" +
                        "         Ví dụ: (value: BOOK) [https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html]" +
                        "   + playerhead: dành cho item \"bet-player:\" để hiện thị đầu người hơi\n" +
                        "   + customhead: Đọc phần ở dưới\n" +
                        "     + Cách sử dụng custom-head:\n" +
                        "         Bước 1: Truy cập vào một custom-head mà bạn cần ở web có link https://minecraft-heads.com/custom-heads/\n" +
                        "         Bước 2: Kéo xuống dưới cùng bạn sẽ thấy có một mục là \"Other\", Copy phần \"Value:\" và\n" +
                        "                 dán nó vào phần \"value:\" của item bạn cần\n" +
                        " * Số rows phải từ 1 đến 6 (Không dưới hoặc hơn)\n" +
                        "\n" +
                        "ĐỌC KỸ TRƯỚC KHI CHỈNH FILE" +
                        "\n"
        );

        String defaultitems = "inventory.default-items.";
        get().addDefault(defaultitems + "nextPage.name", "&aTrang sau");
        get().addDefault(defaultitems + "nextPage.slot", 8);
        get().addDefault(defaultitems + "nextPage.type", "customhead");
        get().addDefault(defaultitems + "nextPage.value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDYzNjgyZjE5ZGU0OTZjYjNjMzE0ZDYyOWQzZmMxZjQyNWU2NjAyNTI4MmQyY2U4YTFmMGUyMjQ3NmMwMWMwNCJ9fX0=");
        get().addDefault(defaultitems + "nextPage.data", 0);
        get().addDefault(defaultitems + "nextPage.lore", new String[]{
                "&7Sang trang %nextPage%"
        });
        get().addDefault(defaultitems + "prevPage.name", "&aTrang trước");
        get().addDefault(defaultitems + "prevPage.slot", 2);
        get().addDefault(defaultitems + "prevPage.type", "customhead");
        get().addDefault(defaultitems + "prevPage.value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWMwNjQ2YTI4YjQ1MWRhNzQzOTRlNjk4YjA0ZmFjOTM1YmExOTc1ZjQyODI5MDY3YTBmYmZlZDE4MWEzNjU5NCJ9fX0=");
        get().addDefault(defaultitems + "prevPage.data", 0);
        get().addDefault(defaultitems + "prevPage.lore", new String[]{
                "&7Về trang %prevPage%"
        });
        get().addDefault(defaultitems + "borderItem.name", "");
        get().addDefault(defaultitems + "borderItem.type", "material");
        get().addDefault(defaultitems + "borderItem.value", "BLACK_STAINED_GLASS_PANE");
        get().addDefault(defaultitems + "borderItem.data", 0);
        get().addDefault(defaultitems + "borderItem.lore", new String[]{
                ""
        });

        String txinfo = "inventory.taiXiuInfo.";
        get().addDefault(txinfo + "title", "&0Thông tin phiên số &b#%session%");
        get().addDefault(txinfo + "rows", 6);
        get().addDefault(txinfo + "items.bet-player.name", "&b%playerName%");
        get().addDefault(txinfo + "items.bet-player.type", "playerhead");
        get().addDefault(txinfo + "items.bet-player.data", 0);
        get().addDefault(txinfo + "items.bet-player.lore", new String[]{
                "",
                "&fCược vào:&r %bet%",
                "&fSố tiền cược: &6%money%$",
                ""
        });
        get().addDefault(txinfo + "items.bet-info.name", "&eThông tin phiên");
        get().addDefault(txinfo + "items.bet-info.slot", 5);
        get().addDefault(txinfo + "items.bet-info.type", "material");
        get().addDefault(txinfo + "items.bet-info.value", "BOOK");
        get().addDefault(txinfo + "items.bet-info.data", 0);
        get().addDefault(txinfo + "items.bet-info.lorePlaying", new String[]{
                "&fCó kết quả sau: &d%time% giây",
                "",
                "&fPhiên số: &b%session%",
                "&fSố người chọn " + TaiXiu.getForCurrentVersion("&2Xỉu", "&#1fc433Xỉu") + "&f: &e%xiuPlayerNumber% &6(%xiuTotalBet%$)",
                "&fSố người chọn " + TaiXiu.getForCurrentVersion("&4Tài", "&#c42d1fTài") + "&f: &e%taiPlayerNumber% &6(%taiTotalBet%$)",
                "&fTổng tiền cược phiên này: &6%totalBet%$",
                "",
                "&eNhấn để tải lại thông tin"
        });
        get().addDefault(txinfo + "items.bet-info.loreEnded", new String[]{
                "&bPhiên này đã có kết quả!",
                "",
                "&fPhiên số: &b%session%",
                "&fSố người chọn " + TaiXiu.getForCurrentVersion("&2Xỉu", "&#1fc433Xỉu") + "&f: &e%xiuPlayerNumber% &6(%xiuTotalBet%$)",
                "&fSố người chọn " + TaiXiu.getForCurrentVersion("&4Tài", "&#c42d1fTài") + "&f: &e%taiPlayerNumber% &6(%taiTotalBet%$)",
                "&fTổng tiền cược phiên này: &6%totalBet%$",
                "&fĂn nhiều nhất: %bestWinners%",
                "",
                "&fXúc xắc 1: &e%dice1%",
                "&fXúc xắc 2: &e%dice2%",
                "&fXúc xắc 3: &e%dice3%",
                "&fKết quả:&r %result%",
                ""
        });

        get().options().copyDefaults(true);
        save();
    }

}
