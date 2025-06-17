package com.cortezromeo.taixiu.support.version.cross;

import com.cortezromeo.taixiu.api.server.VersionSupport;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.COLOR_CHAR;

public class CrossVersionSupport extends VersionSupport {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)&[0-9A-FK-OR]");
    private static final String NBT_KEY = "TaiXiu";

    public CrossVersionSupport(Plugin plugin) {
        super(plugin);
    }

    @Override
    public ItemStack createItemStack(String material, int amount, short data) {
        return XMaterial.matchXMaterial(material + ":" + data)
                .map(XMaterial::parseItem)
                .map(item -> {
                    item.setAmount(amount);
                    return item;
                })
                .orElseGet(() -> {
                    getPlugin().getLogger().severe("----------------------------------------------------");
                    getPlugin().getLogger().severe("INVALID MATERIAL: " + material);
                    getPlugin().getLogger().severe("The material name may be incorrect or not available in this server version.");
                    getPlugin().getLogger().severe(">> Reference Material List <<");
                    getPlugin().getLogger().severe("https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");
                    getPlugin().getLogger().severe("----------------------------------------------------");
                    return new ItemStack(Material.BEDROCK);
                });
    }

    @Override
    public Sound createSound(String soundName) {
        return XSound.matchXSound(soundName).map(XSound::parseSound).orElseGet(() -> {
            getPlugin().getLogger().severe("----------------------------------------------------");
            getPlugin().getLogger().severe("INVALID SOUND NAME: " + soundName);
            getPlugin().getLogger().severe("The sound name may be incorrect or not supported in this server version.");
            getPlugin().getLogger().severe(">> Reference Sound List <<");
            getPlugin().getLogger().severe("https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html");
            getPlugin().getLogger().severe("----------------------------------------------------");
            return XSound.BLOCK_AMETHYST_CLUSTER_BREAK.parseSound();
        });
    }

    @Override
    public ItemStack getHeadItemFromBase64(String headValue) {
        return XSkull.createItem().profile(Profileable.of(ProfileInputType.BASE64, headValue)).apply();
    }

    public ItemStack getHeadItemFromPlayerName(String playerName) {
        try {
            if (Bukkit.getPlayer(playerName) != null)
                playerName = Bukkit.getPlayer(playerName).getUniqueId().toString();
            else if (!Bukkit.getServer().getOnlineMode()) {
                String offlinePlayerString = "OfflinePlayer:" + playerName;
                playerName = UUID.nameUUIDFromBytes(offlinePlayerString.getBytes(StandardCharsets.UTF_8)).toString();
            }
            return XSkull.createItem().profile(Profileable.of(UUID.fromString(playerName))).apply();
            // normally, if this account is not a part of microsoft account, it will occur error
        } catch (Exception exception) {
            return XMaterial.PLAYER_HEAD.parseItem();
        }
    }

    @Override
    public ItemStack addCustomData(ItemStack itemStack, String data) {
        if (itemStack == null)
            return null;

        if (itemStack.getType() == Material.AIR)
            return null;

        NBT.modify(itemStack, nbt -> {
            nbt.setString(NBT_KEY + ".customdata", data);
        });

        return itemStack;
    }

    @Override
    public String getCustomData(ItemStack itemStack) {
        if (itemStack == null)
            return null;

        if (itemStack.getType() == Material.AIR)
            return null;

        return NBT.get(itemStack, nbt -> (String) (nbt.getString(NBT_KEY + ".customdata")));
    }

    @Override
    public String addColor(String textToTranslate) {
        if (textToTranslate == null)
            return "NULL";

        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuilder buffer = new StringBuilder(textToTranslate.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        String hexTranslated = matcher.appendTail(buffer).toString();

        return ChatColor.translateAlternateColorCodes('&', hexTranslated);
    }

    @Override
    public String stripColor(String textToStrip) {
        return textToStrip == null ? null : STRIP_COLOR_PATTERN.matcher(textToStrip).replaceAll("");
    }
}
