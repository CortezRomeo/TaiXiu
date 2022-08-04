package com.cortezromeo.taixiu.support.version.v1_12_R1;

import com.cortezromeo.taixiu.api.server.VersionSupport;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.regex.Pattern;

public class v1_12_R1 extends VersionSupport {

    public v1_12_R1(Plugin plugin, String versionName) {
        super(plugin, versionName);
    }

    public int getVersion() {
        return 12;
    }

    @Override
    public org.bukkit.inventory.ItemStack createItemStack(String material, int amount, short data) {
        org.bukkit.inventory.ItemStack i;
        try {
            i = new org.bukkit.inventory.ItemStack(org.bukkit.Material.valueOf(material), amount, data);
        } catch (Exception ex) {
            getPlugin().getLogger().severe("----------------------------------------------------");
            getPlugin().getLogger().severe("MATERIAL " + material + " KHÔNG HỢP LỆ!");
            getPlugin().getLogger().severe(">> Link Materials cho các phiên bản từ 1.12.2 đến 1.15 <<");
            getPlugin().getLogger().severe("https://helpch.at/docs/1.12.2/org/bukkit/Material.html");
            getPlugin().getLogger().severe("----------------------------------------------------");
            i = new org.bukkit.inventory.ItemStack(org.bukkit.Material.BEDROCK);
        }
        return i;
    }

    @Override
    public ItemStack getHeadItem() {
        return createItemStack("SKULL_ITEM", 1, (short) 3);
    }

    @Override
    public org.bukkit.inventory.ItemStack addCustomData(org.bukkit.inventory.ItemStack i, String data) {
        net.minecraft.server.v1_12_R1.ItemStack itemStack = CraftItemStack.asNMSCopy(i);
        NBTTagCompound tag = itemStack.getTag();
        if (tag == null) {
            tag = new NBTTagCompound();
            itemStack.setTag(tag);
        }

        tag.setString("TaiXiu", data);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public String getCustomData(org.bukkit.inventory.ItemStack i) {
        net.minecraft.server.v1_12_R1.ItemStack itemStack = CraftItemStack.asNMSCopy(i);
        NBTTagCompound tag = itemStack.getTag();
        if (tag == null) return "";
        return tag.getString("TaiXiu");
    }

    public String addColor(String textToTranslate) {
        return ChatColor.translateAlternateColorCodes('&', textToTranslate);
    }

    private Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('&') + "[0-9A-FK-OR]");

    public String stripColor(String textToStrip) {
        return textToStrip == null ? null : STRIP_COLOR_PATTERN.matcher(textToStrip).replaceAll("");
    }
}
