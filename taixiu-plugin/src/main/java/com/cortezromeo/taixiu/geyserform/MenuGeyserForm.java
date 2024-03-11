package com.cortezromeo.taixiu.geyserform;

import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;

public class MenuGeyserForm {

    public static SimpleForm getForm(Player player) {

        SimpleForm form = SimpleForm.builder().content("Title").button("Button 1").optionalButton("Button 2", true).
                validResultHandler((simpleForm, simpleFormResponse) -> {
                    if (simpleFormResponse.clickedButtonId() == 1) {
                        player.sendMessage("Button 1");
                    }
                }).build();


        return form;
    }

}
