package com.cortezromeo.taixiu.geyserform;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.file.GeyserFormFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.floodgate.api.FloodgateApi;

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
        goBackButtonName = geyserFormFile.getString(stringPath + "button.goBack");
        closeButtonName = geyserFormFile.getString(stringPath + "button.close");
    }

    public static ModalForm getForm(Player player) {
        return ModalForm.builder().title(title)
                .content(content)
                .button1(goBackButtonName)
                .button2(closeButtonName)
                .validResultHandler((modalForm, modalFormResponse) -> {
                    if (modalFormResponse.clickedButtonId() == 1)
                        FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(MenuGeyserForm.getForm(player));
                })
                .build();
    }

}
