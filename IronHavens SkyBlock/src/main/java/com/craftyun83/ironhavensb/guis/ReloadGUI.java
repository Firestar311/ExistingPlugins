package com.craftyun83.ironhavensb.guis;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ReloadGUI {
	
	public ReloadGUI(Player p) {
		
		Inventory reloadGUI = Bukkit.getServer().createInventory(p, 9, "Reload Island");
		
		// Create Confirm Item
		
		ItemStack confirmReload = new ItemStack(Material.GREEN_CONCRETE);
		ItemMeta confirmReloadMeta = confirmReload.getItemMeta();
		ArrayList<String> confirmReloadLore = new ArrayList<String>();
			
		confirmReloadLore.add(ChatColor.GREEN+"I want to reload my island!");
		
		confirmReloadMeta.setLore(confirmReloadLore);
		confirmReloadMeta.setDisplayName("§a§lConfirm");
		
		
		confirmReload.setItemMeta(confirmReloadMeta);
		reloadGUI.setItem(3, confirmReload);
		
		// Create Go Back Item
		
		ItemStack cancelReload = new ItemStack(Material.RED_CONCRETE);
		ItemMeta cancelReloadMeta = cancelReload.getItemMeta();
		ArrayList<String> cancelReloadLore = new ArrayList<String>();
		
		cancelReloadLore.add(ChatColor.RED+"I wanna go back!");
		
		cancelReloadMeta.setLore(cancelReloadLore);
		cancelReloadMeta.setDisplayName("§c§lCancel");
		
		
		cancelReload.setItemMeta(cancelReloadMeta);
		reloadGUI.setItem(5, cancelReload);
		
		//Here opens the inventory
		p.openInventory(reloadGUI);
		
	}

}
