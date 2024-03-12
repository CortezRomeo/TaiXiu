package com.cortezromeo.taixiu.geyserform;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.file.GeyserFormFile;
import com.cortezromeo.taixiu.file.MessageFile;
import com.cortezromeo.taixiu.manager.TaiXiuManager;
import com.cortezromeo.taixiu.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.ModalForm;

public class InfoGeyserForm {

    private static final FileConfiguration geyserFormFile = GeyserFormFile.get();
    private static String title;
    private static String goBackButtonName;
    private static String closeButtonName;

    public static void setupValue() {
        String stringPath = "form.info.";
        title = geyserFormFile.getString(stringPath + "title");
        goBackButtonName = geyserFormFile.getString(stringPath + "button.goBack.name");
        closeButtonName = geyserFormFile.getString(stringPath + "button.close.name");
    }

    public static ModalForm getForm(Player player) {
        return ModalForm.builder().title(title)
                .content(TaiXiu.nms.addColor(getContent(TaiXiuManager.getTaiXiuTask().getSession())))
                .button1(TaiXiu.nms.addColor(goBackButtonName))
                .button2(TaiXiu.nms.addColor(closeButtonName))
                .validResultHandler((modalForm, modalFormResponse) -> {
                    if (modalFormResponse.clickedButtonId() == 0)
                        MenuGeyserForm.openForm(player);
                })
                .build();
    }

    private static String getContent(ISession session) {
        String content = geyserFormFile.getString("form.info.content.content");
        content = content.replace("%session%", String.valueOf(session.getSession()));
        content = content.replace("%time%", String.valueOf(TaiXiuManager.getTaiXiuTask().getTime()));

        String xiuPlayersFormat = geyserFormFile.getString("form.info.content.placeholders.xiuPlayers");
        StringBuilder xiuPlayers = new StringBuilder();
        if (!session.getXiuPlayers().isEmpty()) {
            for (String player : session.getXiuPlayers().keySet()) {
                xiuPlayers.append(xiuPlayersFormat.replaceAll("%player%", player).replaceAll("%money%", MessageUtil.formatMoney(session.getXiuPlayers().get(player))));
            }
            content = content.replace("%xiuPlayers%", xiuPlayers);
        } else
            content = content.replace("%xiuPlayers%", MessageFile.get().getString("none-name") + "\n");

        String taiPlayersFormat = geyserFormFile.getString("form.info.content.placeholders.taiPlayers");
        StringBuilder taiPlayers = new StringBuilder();
        if (!session.getTaiPlayers().isEmpty()) {
            for (String player : session.getTaiPlayers().keySet()) {
                taiPlayers.append(taiPlayersFormat.replaceAll("%player%", player).replaceAll("%money%", MessageUtil.formatMoney(session.getTaiPlayers().get(player))));
            }
            content = content.replace("%taiPlayers%", taiPlayers);
        } else
            content = content.replace("%taiPlayers%", MessageFile.get().getString("none-name") + "\n");

        content = content.replace("%totalBet%", MessageUtil.formatMoney(TaiXiuManager.getTotalBet(session)));
        return content;
    }

}
