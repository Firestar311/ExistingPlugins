package me.alonedev.ironhavensb.guis;

import me.alonedev.ironhavensb.Main;
import me.alonedev.ironhavensb.Utils.GuiUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BiomeGUI {

    public static Inventory BiomeGUI;
    private static Main main;
    private static GuiUtil gui;

    public BiomeGUI(Player p, Main main) {
        BiomeGUI = Bukkit.createInventory(null, 18, ChatColor.translateAlternateColorCodes('&', "&bBiome Chooser"));
        this.main = main;
        this.gui = new GuiUtil(main);
        gui.generateItems(BiomeGUI, "BiomeGUI");

        p.openInventory(BiomeGUI);
    }
}