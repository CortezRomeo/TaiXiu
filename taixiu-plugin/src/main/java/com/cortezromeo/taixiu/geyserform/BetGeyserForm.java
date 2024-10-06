package com.cortezromeo.taixiu.geyserform;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.file.GeyserFormFile;
import com.cortezromeo.taixiu.language.Messages;
import com.cortezromeo.taixiu.manager.TaiXiuManager;
import com.cortezromeo.taixiu.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;

import static com.cortezromeo.taixiu.util.MessageUtil.sendMessage;

public class BetGeyserForm {

    private static final FileConfiguration geyserFormFile = GeyserFormFile.get();
    private static String title;
    private static String secondOrder;
    private static String secondOrderOption1;
    private static String secondOrderOption2;
    private static String thirdOrder;
    private static String thirdOrderPlaceholder;

    public static void setupValue() {
        String stringPath = "form.bet.";
        title = geyserFormFile.getString(stringPath + "title");

        secondOrder = geyserFormFile.getString(stringPath + "order.2.dropdown.name");
        secondOrderOption1 = geyserFormFile.getString(stringPath + "order.2.dropdown.options.tai");
        secondOrderOption2 = geyserFormFile.getString(stringPath + "order.2.dropdown.options.xiu");
        thirdOrder = geyserFormFile.getString(stringPath + "order.3.input.name");
        thirdOrderPlaceholder = geyserFormFile.getString(stringPath + "order.3.input.placeholder");
        thirdOrderPlaceholder = thirdOrderPlaceholder.replace("%minBet%", TaiXiu.plugin.getConfig().getString("bet-settings.min-bet"));
        thirdOrderPlaceholder = thirdOrderPlaceholder.replace("%maxBet%", TaiXiu.plugin.getConfig().getString("bet-settings.max-bet"));
    }

    public static CustomForm getForm(Player player) {
        String firstOrder = geyserFormFile.getString("form.bet.order.1.label");
        firstOrder = firstOrder.replace("%session%", String.valueOf(TaiXiuManager.getTaiXiuTask().getSession().getSession()));
        firstOrder = firstOrder.replace("%secondsLeft%", String.valueOf(
                (TaiXiuManager.getTaiXiuTask().getTime() - TaiXiu.plugin.getConfig().getInt("bet-settings.disable-while-remaining"))));

        return CustomForm.builder().title(TaiXiu.nms.addColor(title))
                .label(TaiXiu.nms.addColor(firstOrder))
                .dropdown(secondOrder, TaiXiu.nms.addColor(secondOrderOption1), TaiXiu.nms.addColor(secondOrderOption2))
                .input(TaiXiu.nms.addColor(thirdOrder), TaiXiu.nms.addColor(thirdOrderPlaceholder))
                .validResultHandler((customForm, customFormResponse) -> {

                    if (customFormResponse.asInput(2) == null)
                        return;

                    long money = 0;
                    try {
                        money = Long.parseLong(customFormResponse.asInput(2));
                    } catch (Exception e) {
                        sendMessage(player, Messages.INVALID_CURRENCY
                                .replace("%currencyName%", MessageUtil.getCurrencyName(TaiXiuManager.getSessionData().getCurrencyType()))
                                .replace("%currencySymbol%", MessageUtil.getCurrencySymbol(TaiXiuManager.getSessionData().getCurrencyType())));
                        return;
                    }

                    TaiXiuResult result;
                    if (customFormResponse.asDropdown(1) == 0) {
                        result = TaiXiuResult.TAI;
                    } else
                        result = TaiXiuResult.XIU;

                    TaiXiuManager.playerBet(player, money, result);
                }).build();
    }
}
