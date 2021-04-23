package com.craftyun83.ironhavensb.Utils;

import com.craftyun83.ironhavensb.Main;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GuiUtil {

    public static Main main;

    public GuiUtil(Main main) {
        this.main = main;
    }

    public ItemStack createGuiItem(Inventory GUI, final Material material, final String name, final int slot, final ArrayList<String> lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(lore);
        item.setItemMeta(meta);
        GUI.setItem(slot, item);
        return item;
    }

    public void generateItems(Inventory GUI, String gui) {
        List<String> guiItems = main.getConfig().getStringList("GUIS." + gui);
        for (String item : guiItems) {
            String[] items = item.split(";");
            String name = items[0];
            Material material = Material.valueOf(items[1]);
            int slot = Integer.parseInt(items[2]);
            String[] lores = items[3].split(",");
            ArrayList<String> formatLore = new ArrayList<String>();
            for (String lore : lores) {
                formatLore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', lore));
            }

            GUI.setItem(slot, createGuiItem(GUI, material, org.bukkit.ChatColor.translateAlternateColorCodes('&', name), slot, formatLore));
        }
    }
}
