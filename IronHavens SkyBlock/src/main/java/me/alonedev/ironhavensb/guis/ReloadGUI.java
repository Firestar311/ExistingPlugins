package me.alonedev.ironhavensb.guis;

import me.alonedev.ironhavensb.Main;
import me.alonedev.ironhavensb.Utils.GuiUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ReloadGUI {

    private static Inventory ReloadGUI;
    private static Main main;
    private static GuiUtil gui;

    public ReloadGUI(Player p, Main main) {
        ReloadGUI = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&bReset Island"));
        this.main = main;
        this.gui = new GuiUtil(main);
        gui.generateItems(ReloadGUI, "ReloadGUI");

        p.openInventory(ReloadGUI);
    }
}