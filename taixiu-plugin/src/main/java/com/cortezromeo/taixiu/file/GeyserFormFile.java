package com.cortezromeo.taixiu.file;

import com.cortezromeo.taixiu.TaiXiu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class GeyserFormFile {

    private static File file;
    private static FileConfiguration inventoryFile;
    private static final String fileName = "geyserform.yml";

    public static void setup() {
        file = new File(TaiXiu.plugin.getDataFolder() + "/" + fileName);

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
        file = new File(TaiXiu.plugin.getDataFolder() + "/" + fileName);
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
                        "     ______   ______     __        __  __     __     __  __\n" +
                        "    /\\__  _\\ /\\  __ \\   /\\ \\      /\\_\\_\\_\\   /\\ \\   /\\ \\/\\ \\\n" +
                        "    \\/_/\\ \\/ \\ \\  __ \\  \\ \\ \\     \\/_/\\_\\/_  \\ \\ \\  \\ \\ \\_\\ \\\n" +
                        "       \\ \\_\\  \\ \\_\\ \\_\\  \\ \\_\\      /\\_\\/\\_\\  \\ \\_\\  \\ \\_____\\\n" +
                        "        \\/_/   \\/_/\\/_/   \\/_/      \\/_/\\/_/   \\/_/   \\/_____/\n" +
                        "\n" +
                        " Author: Cortez_Romeo\n" +
                        " Download plugin này miễn phí tại: https://minecraftvn.net\n"
        );

        get().addDefault("file-version", 2);

        String menuForm = "form.menu.";
        get().addDefault(menuForm + "title", "&0Tài xỉu");
        get().addDefault(menuForm + "button.rule.name", "&0Luật chơi");
        get().addDefault(menuForm + "button.rule.imageType", "URL");
        get().addDefault(menuForm + "button.rule.imageData", "https://i.imgur.com/UD8v9Ui.png");
        get().addDefault(menuForm + "button.sessionInfo.name", "&0Thông tin phiên");
        get().addDefault(menuForm + "button.sessionInfo.imageType", "URL");
        get().addDefault(menuForm + "button.sessionInfo.imageData", "https://i.imgur.com/m6EdubF.png");
        get().addDefault(menuForm + "button.bet.name", "&0Cược");
        get().addDefault(menuForm + "button.bet.imageType", "URL");
        get().addDefault(menuForm + "button.bet.imageData", "https://i.imgur.com/ReMGrcW.png");
        get().addDefault(menuForm + "button.toggle.on.name", "&2Bật&0 thông báo");
        get().addDefault(menuForm + "button.toggle.on.imageType", "URL");
        get().addDefault(menuForm + "button.toggle.on.imageData", "https://i.imgur.com/aANvBJ9.png");
        get().addDefault(menuForm + "button.toggle.off.name", "&4Tắt&0 thông báo");
        get().addDefault(menuForm + "button.toggle.off.imageType", "URL");
        get().addDefault(menuForm + "button.toggle.off.imageData", "https://i.imgur.com/3pDJQgt.png");

        String ruleForm = "form.rule.";
        get().addDefault(ruleForm + "title", "&0Luật chơi tài xỉu");
        get().addDefault(ruleForm + "content",
                "&aTài xỉu - Luật chơi\n\n" +
                "&fKhi cộng tổng các nút của 3 xúc xắc từ &e4 - 10&f thì sẽ ra &2Xỉu\n" +
                "&fKhi cộng tổng các nút của 3 xúc xắc từ &e11 - 17&f thì sẽ ra &4Tài\n" +
                "&fKhi tổng các nút là &e3&f hoặc &e18&f thì nhà cái ăn (Tài, xỉu đều thua)\n" +
                "&fSố tiền cược tối thiểu là &6$%minBet%\n" +
                "&fSố tiền cược tối đa là &6$%maxBet%\n" +
                "&fKhi thắng bạn sẽ nhận được gấp đôi số tiền đã đặt (1 ăn 1)");
        get().addDefault(ruleForm + "button.goBack.name", "&cTrở về");
        get().addDefault(ruleForm + "button.close.name", "&4Đóng");

        String infoForm = "form.info.";
        get().addDefault(infoForm + "title", "Thông tin phiên");
        get().addDefault(infoForm + "content.content",
                "&fTài xỉu phiên số &b#%session%\n" +
                "&fCó kết quả sau: &d%time% giây\n\n" +
                "&fNgười chọn xỉu:\n" +
                "%xiuPlayers%\n" +
                "&fNgười chọn tài:\n" +
                "%taiPlayers%\n" +
                "&fTổng tiền cược: &6$%totalBet%");
        get().addDefault(infoForm + "content.placeholders.xiuPlayers", "&a%player% &7- &6$%money%\n");
        get().addDefault(infoForm + "content.placeholders.taiPlayers", "&c%player% &7- &6$%money%\n");
        get().addDefault(infoForm + "button.goBack.name", "&cTrở về");
        get().addDefault(infoForm + "button.close.name", "&4Đóng");

        String betForm = "form.bet.";
        get().addDefault(betForm + "title", "Đặt cược");
        get().addDefault(betForm + "order.1.label"
                , "Bạn đang đặt cược vào phiên số &b#%session%&r. " +
                 "&rBạn còn &d%secondsLeft% giây &rđể đặt cược trước khi phiên khóa.");
        get().addDefault(betForm + "order.2.dropdown.name", "Vui lòng chọn:");
        get().addDefault(betForm + "order.2.dropdown.options.tai", "&4Tài");
        get().addDefault(betForm + "order.2.dropdown.options.xiu", "&2Xỉu");
        get().addDefault(betForm + "order.3.input.name", "Số tiền muốn cược:");
        get().addDefault(betForm + "order.3.input.placeholder", "%minBet% - %maxBet%");

        get().options().copyDefaults(true);
        save();
    }

}
