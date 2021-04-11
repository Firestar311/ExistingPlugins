package com.craftyun83.ironhavensb.guis;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BiomeGUI {

    public BiomeGUI(Player p) {
        Inventory biomeGUI = Bukkit.getServer().createInventory(p, 18, "Biome Chooser");
        ItemStack jungle = createButton(Material.JUNGLE_WOOD, "Jungle Biome", "Generate a jungle biome skyblock island!");
        biomeGUI.setItem(1, jungle);

        ItemStack spruce = createButton(Material.SPRUCE_WOOD, "Spruce Biome", "Generate a spruce biome skyblock island!");
        biomeGUI.setItem(3, spruce);

        ItemStack forest = createButton(Material.OAK_WOOD, "Forest Biome", "Generate a forest biome skyblock island!");
        biomeGUI.setItem(5, forest);

        ItemStack savanah = createButton(Material.ACACIA_WOOD, "Savanah Biome", "Generate a savanah biome skyblock island!");
        biomeGUI.setItem(7, savanah);

        ItemStack classic = createButton(Material.GRASS_BLOCK, "Classic", "Generate a classic skyblock island!");
        biomeGUI.setItem(11, classic);

        ItemStack hardcore = createButton(Material.BARRIER, "Hardcore", "Generate a hardcore skyblock island!");
        biomeGUI.setItem(15, hardcore);

        p.openInventory(biomeGUI);
    }
    
    private ItemStack createButton(Material icon, String name, String description) {
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + description);
        meta.setLore(lore);
        meta.setDisplayName("§6§l" + name);
        item.setItemMeta(meta);
        return item;
    }
}