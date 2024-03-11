package com.cortezromeo.taixiu.geyserform;

import com.cortezromeo.taixiu.file.GeyserFormFile;
import com.cortezromeo.taixiu.inventory.Button;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.HashMap;

public class MenuGeyserForm {

    private static final FileConfiguration geyserFormFile = GeyserFormFile.get();
    private static String title;
    private static HashMap<Integer, ButtonData> buttonData = new HashMap<>();

    public static void setupValue() {
        String stringPath = "form.menu.";
        title = geyserFormFile.getString(stringPath + "title");
        buttonData.put(1, new ButtonData(geyserFormFile.getString(stringPath + "button.rule.name")
                , FormImage.Type.valueOf(geyserFormFile.getString(stringPath + "button.rule.imageType"))
                , geyserFormFile.getString(stringPath + "button.rule.imageData")));
        buttonData.put(2, new ButtonData(geyserFormFile.getString(stringPath + "button.sessionInfo.name")
                , FormImage.Type.valueOf(geyserFormFile.getString(stringPath + "button.sessionInfo.imageType"))
                , geyserFormFile.getString(stringPath + "button.sessionInfo.imageData")));
        buttonData.put(3, new ButtonData(geyserFormFile.getString(stringPath + "button.bet.name")
                , FormImage.Type.valueOf(geyserFormFile.getString(stringPath + "button.bet.imageType"))
                , geyserFormFile.getString(stringPath + "button.bet.imageData")));
        buttonData.put(4, new ButtonData(geyserFormFile.getString(stringPath + "button.toggle.name")
                , FormImage.Type.valueOf(geyserFormFile.getString(stringPath + "button.toggle.imageType"))
                , geyserFormFile.getString(stringPath + "button.toggle .imageData")));
    }

    public static SimpleForm getForm(Player player) {
        return SimpleForm.builder().content(title)
                .button(buttonData.get(1).getButtonName(), buttonData.get(1).getButtonImageType(), buttonData.get(1).getButtonImageData())
                .button(buttonData.get(2).getButtonName(), buttonData.get(2).getButtonImageType(), buttonData.get(2).getButtonImageData())
                .button(buttonData.get(3).getButtonName(), buttonData.get(3).getButtonImageType(), buttonData.get(3).getButtonImageData())
                .button(buttonData.get(4).getButtonName(), buttonData.get(4).getButtonImageType(), buttonData.get(4).getButtonImageData())
                .validResultHandler((simpleForm, simpleFormResponse) -> {

                    FloodgatePlayer fgPlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());

                    int clickedButtonID = simpleFormResponse.clickedButtonId();

                    if (clickedButtonID == 1)
                        fgPlayer.sendForm(InfoGeyserForm.getForm(player));

                    if (clickedButtonID == 2)
                        fgPlayer.sendForm(InfoGeyserForm.getForm(player));

                    if (clickedButtonID == 3)
                        fgPlayer.sendForm(BetGeyserForm.getForm(player));

                }).build();
    }

}
