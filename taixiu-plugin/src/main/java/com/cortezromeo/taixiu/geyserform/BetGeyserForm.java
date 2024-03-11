package com.cortezromeo.taixiu.geyserform;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.file.GeyserFormFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.ModalForm;

public class BetGeyserForm {

    private static final FileConfiguration geyserFormFile = GeyserFormFile.get();
    private static String title;
    private static String firstOrder;
    private static String secondOrder;
    private static String secondOrderOption1;
    private static String secondOrderOption2;
    private static String thirdOrder;
    private static String thirdOrderPlaceholder;

    public static void setupValue() {
        String stringPath = "form.bet.";
        title = geyserFormFile.getString(stringPath + "title");
        firstOrder = geyserFormFile.getString(stringPath + "order.1.label");
        secondOrder = geyserFormFile.getString(stringPath + "order.2.dropdown.name");
        secondOrderOption1 = geyserFormFile.getString(stringPath + "order.2.dropdown.options.tai");
        secondOrderOption2 = geyserFormFile.getString(stringPath + "order.2.dropdown.options.xiu");
        thirdOrder = geyserFormFile.getString(stringPath + "order.3.input.name");
        thirdOrderPlaceholder = geyserFormFile.getString(stringPath + "order.3.input.placeholder");
        thirdOrderPlaceholder = thirdOrderPlaceholder.replace("%minBet%", TaiXiu.plugin.getConfig().getString("bet-settings.min-bet"));
        thirdOrderPlaceholder = thirdOrderPlaceholder.replace("%maxBet%", TaiXiu.plugin.getConfig().getString("bet-settings.max-bet"));
    }

    public static CustomForm getForm(Player player) {
        return CustomForm.builder().title(title)
                .label(firstOrder)
                .label(secondOrder)
                .dropdown(secondOrderOption1, secondOrderOption2)
                .input(thirdOrder, thirdOrderPlaceholder)
                .validResultHandler((customForm, customFormResponse) -> {
                    //
                }).build();
    }

}
