package com.cortezromeo.taixiu.support;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.manager.TaiXiuManager;
import com.cortezromeo.taixiu.util.MessageUtil;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class DiscordSupport {

    String webHookURL;

    public DiscordSupport(String webHookURL) {
        this.webHookURL = webHookURL;
    }

    public void sendMessage(String message) {
        DiscordWebhook discordWebhook = new DiscordWebhook(webHookURL);
        if (webHookURL == null || webHookURL.equals(""))
            return;

        TaiXiu.support.getFoliaLib().getScheduler().runAsync(task -> {
            discordWebhook.addEmbed(new DiscordWebhook.EmbedObject().setDescription(message));
            try {
                discordWebhook.execute();
            } catch (Exception exception) {
                MessageUtil.throwErrorMessage("[Discord Web Hook] Occur an error while trying to connect to discord web hook! (" + exception.getMessage() + ")");
            }
        });
    }

    public void sendMessage(DiscordWebhook.EmbedObject embedObject) {
        DiscordWebhook discordWebhook = new DiscordWebhook(webHookURL);
        if (webHookURL == null || webHookURL.equals(""))
            return;

        TaiXiu.support.getFoliaLib().getScheduler().runAsync(task -> {
            discordWebhook.addEmbed(embedObject);
            try {
                discordWebhook.execute();
            } catch (Exception exception) {
                MessageUtil.throwErrorMessage("[Discord Web Hook] Occur an error while trying to connect to discord web hook! (" + exception.getMessage() + ")");
            }
        });
    }

    public DiscordWebhook.EmbedObject getResultMessageFromJSON(String jsonFile, ISession session) throws IOException {
        String jsonString = new String(Files.readAllBytes(Paths.get(jsonFile)));
        JSONObject jsonObject = new JSONObject(jsonString);
        DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject();

        if (jsonObject.has("title")) {
            embedObject.setTitle(formatResultString(jsonObject.getString("title"), jsonObject, session));
        }
        if (jsonObject.has("description")) {
            embedObject.setDescription(formatResultString(jsonObject.getString("description"), jsonObject, session));
        }
        if (jsonObject.has("thumbnail")) {
            JSONObject object = jsonObject.getJSONObject("thumbnail");
            if (session.getResult() == TaiXiuResult.TAI) {
                embedObject.setThumbnail(object.getString("tai"));
            } else if (session.getResult() == TaiXiuResult.XIU)
                embedObject.setThumbnail(object.getString("xiu"));
            else
                embedObject.setThumbnail(object.getString("special"));
        }
        if (jsonObject.has("color")) {
            JSONObject object = jsonObject.getJSONObject("color");
            if (session.getResult() == TaiXiuResult.TAI) {
                embedObject.setColor(object.getInt("tai"));
            } else if (session.getResult() == TaiXiuResult.XIU)
                embedObject.setColor(object.getInt("xiu"));
            else
                embedObject.setColor(object.getInt("special"));
        }
        if (jsonObject.has("fields")) {
            for (Object fieldObj : jsonObject.getJSONArray("fields")) {
                JSONObject field = (JSONObject) fieldObj;
                if (field.getString("fieldtype").equalsIgnoreCase("blank")) {
                    embedObject.addBlankField(false);
                } else {
                    String name = formatResultString(field.getString("name"), jsonObject, session);
                    String value = formatResultString(field.getString("value"), jsonObject, session);
                    embedObject.addField(name, value, field.optBoolean("inline", false));
                }
            }
        }
        if (jsonObject.has("footer")) {
            JSONObject object = jsonObject.getJSONObject("footer");
            String text = formatResultString(object.getString("text"), jsonObject, session);
            String icon_url = object.getString("icon_url");
            if (icon_url == null)
                embedObject.setFooter(text);
            else
                embedObject.setFooter(text, icon_url);
        }
        return embedObject;
    }

    public DiscordWebhook.EmbedObject getPlayerBetMessageFromJSON(String jsonFile, ISession session, Player player, TaiXiuResult taiXiuResult, long money) throws IOException {
        String jsonString = new String(Files.readAllBytes(Paths.get(jsonFile)));
        JSONObject jsonObject = new JSONObject(jsonString);
        DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject();

        if (jsonObject.has("title")) {
            embedObject.setTitle(jsonObject.getString("title"));
        }
        if (jsonObject.has("author")) {
            JSONObject object = jsonObject.getJSONObject("author");
            String name = formatPlayerBet(object.getString("name"), jsonObject, session, player, taiXiuResult, money);
            String icon_url = object.getString("icon_url");
            if (icon_url == null)
                embedObject.setAuthor(name);
            else
                embedObject.setAuthor(name, formatPlayerBet(icon_url, jsonObject, session, player, taiXiuResult, money), formatPlayerBet(icon_url, jsonObject, session, player, taiXiuResult, money));
        }
        if (jsonObject.has("color")) {
            JSONObject object = jsonObject.getJSONObject("color");
            if (taiXiuResult == TaiXiuResult.TAI) {
                embedObject.setColor(object.getInt("tai"));
            } else if (taiXiuResult == TaiXiuResult.XIU)
                embedObject.setColor(object.getInt("xiu"));
        }
        if (jsonObject.has("fields")) {
            for (Object fieldObj : jsonObject.getJSONArray("fields")) {
                JSONObject field = (JSONObject) fieldObj;
                if (field.getString("fieldtype").equalsIgnoreCase("blank")) {
                    embedObject.addBlankField(false);
                } else {
                    String name = formatPlayerBet(field.getString("name"), jsonObject, session, player, taiXiuResult, money);
                    String value = formatPlayerBet(field.getString("value"), jsonObject, session, player, taiXiuResult, money);
                    embedObject.addField(name, value, field.optBoolean("inline", false));
                }
            }
        }
        return embedObject;
    }

    private String formatPlayerBet(String string, JSONObject jsonObject, ISession session, Player player, TaiXiuResult taiXiuResult, long money) {
        String resultFormatted = "N/A";
        if (taiXiuResult == TaiXiuResult.TAI)
            resultFormatted = jsonObject.getJSONObject("placeholders").getString("tai");
        else if (taiXiuResult == TaiXiuResult.XIU)
            resultFormatted = jsonObject.getJSONObject("placeholders").getString("xiu");
        String pattern = jsonObject.getJSONObject("placeholders").getString("date");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        string = string.replace("%playerName%", player.getName())
                .replace("%playerUUID%", player.getUniqueId().toString())
                .replace("%currencyName%", TaiXiu.nms.stripColor(MessageUtil.getCurrencyName(session.getCurrencyType())))
                .replace("%money%", MessageUtil.getFormatMoneyDisplay(money))
                .replace("%bet%", resultFormatted)
                .replace("%date%", simpleDateFormat.format(new Date()));
        return string;
    }

    private String formatResultString(String string, JSONObject jsonObject, ISession session) {
        String resultFormatted;
        TaiXiuResult result = session.getResult();
        if (result == TaiXiuResult.TAI)
            resultFormatted = jsonObject.getJSONObject("placeholders").getString("tai");
        else if (result == TaiXiuResult.XIU)
            resultFormatted = jsonObject.getJSONObject("placeholders").getString("xiu");
        else
            resultFormatted = jsonObject.getJSONObject("placeholders").getString("special");

        String bestWinnersFormatted = "N/A";
        try {
            String invalid = jsonObject.getJSONObject("placeholders").getJSONObject("bestWinners").getString("invalid");
            if (result == TaiXiuResult.NONE)
                bestWinnersFormatted = invalid;
            else if (result == TaiXiuResult.SPECIAL) {
                bestWinnersFormatted = jsonObject.getJSONObject("placeholders").getJSONObject("bestWinners").getString("valid-special");
                bestWinnersFormatted = bestWinnersFormatted.replace("%allBet%", MessageUtil.getFormatMoneyDisplay(TaiXiuManager.getTotalBet(session)));
            } else {
                Map<String, Long> bestWinners = result == TaiXiuResult.XIU ? session.getXiuPlayers() : session.getTaiPlayers();
                if (bestWinners.isEmpty()) {
                    bestWinnersFormatted = invalid;
                } else {
                    Long bestWinnersBet = Collections.max(bestWinners.values());

                    List<String> players = new ArrayList<>();
                    for (Map.Entry<String, Long> entry : bestWinners.entrySet())
                        if (entry.getValue() >= bestWinnersBet)
                            players.add(entry.getKey());

                    String delim = jsonObject.getJSONObject("placeholders").getJSONObject("bestWinners").getString("playerName-delim");
                    String bestWinnersName = String.join(delim, players);

                    bestWinnersFormatted = jsonObject.getJSONObject("placeholders").getJSONObject("bestWinners").getString("valid");
                    bestWinnersFormatted = bestWinnersFormatted.replace("%playerName%", bestWinnersName)
                            .replace("%bet%", MessageUtil.getFormatMoneyDisplay(bestWinnersBet * 2));
                }
            }
        } catch (Exception e) {
            MessageUtil.throwErrorMessage("<discordmanager.java<formatString>>" + e);
        }

        string = string.replace("%session%", String.valueOf(session.getSession()))
                .replace("%dice1%", String.valueOf(session.getDice1()))
                .replace("%dice2%", String.valueOf(session.getDice2()))
                .replace("%dice3%", String.valueOf(session.getDice3()))
                .replace("%totalPoint%", String.valueOf(session.getDice1() + session.getDice2() + session.getDice3()))
                .replace("%currencyName%", TaiXiu.nms.stripColor(MessageUtil.getCurrencyName(session.getCurrencyType())))
                .replace("%result%", resultFormatted)
                .replace("%bestWinners%", bestWinnersFormatted);
        return string;
    }
}
