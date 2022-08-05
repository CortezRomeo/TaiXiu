package com.cortezromeo.taixiu.support.version.v1_18_R2;

import com.cortezromeo.taixiu.api.server.VersionSupport;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class v1_18_R2 extends VersionSupport {
    public v1_18_R2(Plugin plugin, String versionName) {
        super(plugin, versionName);
    }

    @Override
    public int getVersion() {
        return 13;
    }

    @Override
    public org.bukkit.inventory.ItemStack createItemStack(String material, int amount, short data) {
        org.bukkit.inventory.ItemStack i;
        try {
            i = new org.bukkit.inventory.ItemStack(org.bukkit.Material.valueOf(material), amount);
        } catch (Exception ex) {
            getPlugin().getLogger().severe("----------------------------------------------------");
            getPlugin().getLogger().severe("MATERIAL " + material + " KHÔNG HỢP LỆ!");
            getPlugin().getLogger().severe(">> Link Materials cho 1.18 <<");
            getPlugin().getLogger().severe("https://helpch.at/docs/1.18/org/bukkit/Material.html");
            getPlugin().getLogger().severe("----------------------------------------------------");
            i = new org.bukkit.inventory.ItemStack(org.bukkit.Material.BEDROCK);
        }
        return i;
    }

    @Override
    public ItemStack getHeadItem() {
        return new ItemStack(Material.PLAYER_HEAD);
    }

    @Override
    public org.bukkit.inventory.ItemStack addCustomData(org.bukkit.inventory.ItemStack i, String data) {
        net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(i);
        NBTTagCompound tag = itemStack.s();
        if (tag == null) {
            tag = new NBTTagCompound();
            itemStack.c(tag);
        }

        tag.a("TaiXiu", data);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public String getCustomData(org.bukkit.inventory.ItemStack i) {
        net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(i);
        NBTTagCompound tag = itemStack.s();
        if (tag == null) return "";
        return tag.l("TaiXiu");
    }

    private final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-fA-F])");

    public String addColor(String textToTranslate) {

        if (textToTranslate == null)
            return "NULL";

        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    private Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('&') + "[0-9A-FK-OR]");

    public String stripColor(String textToStrip) {
        return textToStrip == null ? null : STRIP_COLOR_PATTERN.matcher(textToStrip).replaceAll("");
    }
}
