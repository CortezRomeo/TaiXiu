package com.cortezromeo.taixiu.manager;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.TaiXiuResult;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.util.MessageUtil;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.json.JSONObject;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class DiscordManager {

    private final boolean discordsrvEnabled;
    private final String channelID;

    public DiscordManager(TaiXiu plugin) {
        this.discordsrvEnabled = plugin.getConfig().getBoolean("discordsrv-settings.enabled");
        this.channelID = plugin.getConfig().getString("discordsrv-settings.channel-id");
    }

    public void sendMessage(String message) {
        if (!discordsrvEnabled) {
            return;
        }
        TextChannel messageChannel = getGuild().getTextChannelById(channelID);
        if (messageChannel == null) {
            MessageUtil.throwErrorMessage("[DiscordSRV] Không thể kết nối tới channel ID " + channelID + ", vui lòng kiểm tra lại!");
            return;
        }
        messageChannel.sendMessage(message).queue();
    }

    public void sendMessage(Message message) {
        if (!discordsrvEnabled) {
            return;
        }
        TextChannel messageChannel = getGuild().getTextChannelById(channelID);
        if (messageChannel == null) {
            MessageUtil.throwErrorMessage("[DiscordSRV] Không thể kết nối tới channel ID " + channelID + ", vui lòng kiểm tra lại!");
            return;
        }
        DiscordUtil.queueMessage(messageChannel, message);
    }

    public static Message getResultMessageFromJSON(String jsonFile, ISession session) throws IOException {
        String jsonString = new String(Files.readAllBytes(Paths.get(jsonFile)));
        JSONObject jsonObject = new JSONObject(jsonString);
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (jsonObject.has("title")) {
            embedBuilder.setTitle(formatResultString(jsonObject.getString("title"), jsonObject, session));
        }
        if (jsonObject.has("description")) {
            embedBuilder.setDescription(formatResultString(jsonObject.getString("description"), jsonObject, session));
        }
        if (jsonObject.has("thumbnail")) {
            JSONObject object = jsonObject.getJSONObject("thumbnail");
            if (session.getResult() == TaiXiuResult.TAI) {
                embedBuilder.setThumbnail(object.getString("tai"));
            } else if (session.getResult() == TaiXiuResult.XIU)
                embedBuilder.setThumbnail(object.getString("xiu"));
            else
                embedBuilder.setThumbnail(object.getString("special"));
        }
        if (jsonObject.has("color")) {
            JSONObject object = jsonObject.getJSONObject("color");
            if (session.getResult() == TaiXiuResult.TAI) {
                embedBuilder.setColor(object.getInt("tai"));
            } else if (session.getResult() == TaiXiuResult.XIU)
                embedBuilder.setColor(object.getInt("xiu"));
            else
                embedBuilder.setColor(object.getInt("special"));
        }
        if (jsonObject.has("fields")) {
            for (Object fieldObj : jsonObject.getJSONArray("fields")) {
                JSONObject field = (JSONObject) fieldObj;
                if (field.getString("fieldtype").equalsIgnoreCase("blank")) {
                    embedBuilder.addBlankField(false);
                } else {
                    String name = formatResultString(field.getString("name"), jsonObject, session);
                    String value = formatResultString(field.getString("value"), jsonObject, session);
                    embedBuilder.addField(name, value, field.optBoolean("inline", false));
                }
            }
        }
        if (jsonObject.has("footer")) {
            JSONObject object = jsonObject.getJSONObject("footer");
            String text = formatResultString(object.getString("text"), jsonObject, session);
            String icon_url = object.getString("icon_url");
            if (icon_url == null)
                embedBuilder.setFooter(text);
            else
                embedBuilder.setFooter(text, icon_url);
        }
        return new MessageBuilder().setEmbeds(embedBuilder.build()).build();
    }

    public static Message getPlayerBetMessageFromJSON(String jsonFile, ISession session, Player player, TaiXiuResult taiXiuResult, long money) throws IOException {
        String jsonString = new String(Files.readAllBytes(Paths.get(jsonFile)));
        JSONObject jsonObject = new JSONObject(jsonString);
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (jsonObject.has("title")) {
            embedBuilder.setTitle(jsonObject.getString("title"));
        }
        if (jsonObject.has("author")) {
            JSONObject object = jsonObject.getJSONObject("author");
            String name = formatPlayerBet(object.getString("name"), jsonObject, session, player, taiXiuResult, money);
            String icon_url = object.getString("icon_url");
            if (icon_url == null)
                embedBuilder.setAuthor(name);
            else
                embedBuilder.setAuthor(name, formatPlayerBet(icon_url, jsonObject, session, player, taiXiuResult, money), formatPlayerBet(icon_url, jsonObject, session, player, taiXiuResult, money));
        }
        if (jsonObject.has("color")) {
            JSONObject object = jsonObject.getJSONObject("color");
            if (taiXiuResult == TaiXiuResult.TAI) {
                embedBuilder.setColor(object.getInt("tai"));
            } else if (taiXiuResult == TaiXiuResult.XIU)
                embedBuilder.setColor(object.getInt("xiu"));
        }
        if (jsonObject.has("fields")) {
            for (Object fieldObj : jsonObject.getJSONArray("fields")) {
                JSONObject field = (JSONObject) fieldObj;
                if (field.getString("fieldtype").equalsIgnoreCase("blank")) {
                    embedBuilder.addBlankField(false);
                } else {
                    String name = formatPlayerBet(field.getString("name"), jsonObject, session, player, taiXiuResult, money);
                    String value = formatPlayerBet(field.getString("value"), jsonObject, session, player, taiXiuResult, money);
                    embedBuilder.addField(name, value, field.optBoolean("inline", false));
                }
            }
        }
        return new MessageBuilder().setEmbeds(embedBuilder.build()).build();
    }

    private static String formatPlayerBet(String string, JSONObject jsonObject, ISession session, Player player, TaiXiuResult taiXiuResult, long money) {
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

    private static String formatResultString(String string, JSONObject jsonObject, ISession session) {
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

    public Guild getGuild() {
        return DiscordSRV.getPlugin().getJda().getGuilds().stream().findFirst().orElse(null);
    }
}
