package me.alonedev.ironhavensb.guis;

import me.alonedev.ironhavensb.Main;
import me.alonedev.ironhavensb.Utils.GuiUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CreatedGUI {

    private static Inventory createdGUI;
    private static Main main;
    private static GuiUtil gui;

    public CreatedGUI(Player p, Main main) {
        createdGUI = Bukkit.createInventory(null, 18, ChatColor.translateAlternateColorCodes('&', "&bIsland Viewer"));
        this.main = main;
        this.gui = new GuiUtil(main);
        gui.generateItems(createdGUI, "CreatedGUI");

        p.openInventory(createdGUI);
    }
}