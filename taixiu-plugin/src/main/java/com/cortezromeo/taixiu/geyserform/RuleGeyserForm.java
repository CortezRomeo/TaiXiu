package com.cortezromeo.taixiu.geyserform;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.file.GeyserFormFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.ModalForm;

public class RuleGeyserForm {

    private static final FileConfiguration geyserFormFile = GeyserFormFile.get();
    private static String title;
    private static String content;
    private static String goBackButtonName;
    private static String closeButtonName;

    public static void setupValue() {
        String stringPath = "form.rule.";
        title = geyserFormFile.getString(stringPath + "title");
        content = geyserFormFile.getString(stringPath + "content");
        content = content.replace("%minBet%", TaiXiu.plugin.getConfig().getString("bet-settings.min-bet"));
        content = content.replace("%maxBet%", TaiXiu.plugin.getConfig().getString("bet-settings.max-bet"));
        goBackButtonName = geyserFormFile.getString(stringPath + "button.goBack.name");
        closeButtonName = geyserFormFile.getString(stringPath + "button.close.name");
    }

    public static ModalForm getForm(Player player) {
        return ModalForm.builder().title(TaiXiu.nms.addColor(title))
                .content(TaiXiu.nms.addColor(content))
                .button1(TaiXiu.nms.addColor(goBackButtonName))
                .button2(TaiXiu.nms.addColor(closeButtonName))
                .validResultHandler((modalForm, modalFormResponse) -> {
                    if (modalFormResponse.clickedButtonId() == 0)
                        MenuGeyserForm.openForm(player);
                })
                .build();
    }

}
