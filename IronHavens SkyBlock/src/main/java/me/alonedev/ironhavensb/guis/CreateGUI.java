package me.alonedev.ironhavensb.guis;

import me.alonedev.ironhavensb.Main;
import me.alonedev.ironhavensb.Utils.GuiUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CreateGUI {

    private static Inventory createGUI;
    private static Main main;
    private static GuiUtil gui;

    public CreateGUI(Player p, Main main) {
        createGUI = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', "&bBiome Chooser"));
        this.main = main;
        this.gui = new GuiUtil(main);
        gui.generateItems(createGUI, "CreateGUI");

        p.openInventory(createGUI);
    }
}