package me.alonedev.ironhavensb.guis;

import me.alonedev.ironhavensb.Utils.GuiUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.alonedev.ironhavensb.Main;

public class InviteGUI {

	private static Inventory InviteGUI;
	private static Main main;
	private static GuiUtil gui;
	
	public static Player invitor;
	
	public InviteGUI(Player target, Player invitor, Main main) {
		this.invitor = invitor;

		InviteGUI = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&bCOOP Invite"));
		this.main = main;
		this.gui = new GuiUtil(main);
		gui.generateItems(InviteGUI, "InviteGUI");

		target.openInventory(InviteGUI);
	}
}