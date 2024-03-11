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
                        " Download plugin này miễn phí tại: https://minecraftvn.net\n" +
                        "\n" +
                        "ĐỌC KỸ TRƯỚC KHI CHỈNH FILE" +
                        "\n"
        );

        get().addDefault("file-version", 2);

        String menuForm = "form.menu.";
        get().addDefault(menuForm + "title", "&0Tài xỉu");
        get().addDefault(menuForm + "button.rule.name", "&0Luật chơi");
        get().addDefault(menuForm + "button.rule.imageType", "PATH");
        get().addDefault(menuForm + "button.rule.imageData", "images/menubutton1.png");
        get().addDefault(menuForm + "button.sessionInfo.name", "&0Thông tin phiên");
        get().addDefault(menuForm + "button.sessionInfo.imageType", "PATH");
        get().addDefault(menuForm + "button.sessionInfo.imageData", "images/menubutton2.png");
        get().addDefault(menuForm + "button.bet.name", "&0Cược");
        get().addDefault(menuForm + "button.bet.imageType", "PATH");
        get().addDefault(menuForm + "button.bet.imageData", "images/menubutton3.png");
        get().addDefault(menuForm + "button.toggle.name", "&0Bật/Tắt thông báo");
        get().addDefault(menuForm + "button.toggle.imageType", "PATH");
        get().addDefault(menuForm + "button.toggle.imageData", "images/menubutton4.png");

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
        get().addDefault(infoForm + "content", "&fTài xỉu phiên số &b#%session%");
        get().addDefault(infoForm + "button.goBack.name", "&cTrở về");
        get().addDefault(infoForm + "button.close.name", "&4Đóng");

        String betForm = "form.bet.";
        get().addDefault(betForm + "title", "Đặt cược");
        get().addDefault(betForm + "order.1.label"
                , "Bạn đang đặt cược vào phiên số &b#%session%\n" +
                 "Bạn còn &d%secondsLeft% giây &rđể đặt cược trước khi phiên khóa.");
        get().addDefault(betForm + "order.2.dropdown.name", "Vui lòng chọn:");
        get().addDefault(betForm + "order.2.dropdown.options.tai", "Tài");
        get().addDefault(betForm + "order.2.dropdown.options.xiu", "Xỉu");
        get().addDefault(betForm + "order.3.input.name", "Số tiền muốn cược:");
        get().addDefault(betForm + "order.3.input.placeholder", "%minBet% - %maxBet%");

        get().options().copyDefaults(true);
        save();
    }

}
