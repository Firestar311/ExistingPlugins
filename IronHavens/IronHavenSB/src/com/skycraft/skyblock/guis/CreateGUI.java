package com.skycraft.skyblock.guis;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CreateGUI {
	
	public CreateGUI(Player p) {
		
		Inventory createGUI = Bukkit.getServer().createInventory(p, 9, "Create your island");
		
		// Create Island Item
		
		ItemStack createIsland = new ItemStack(Material.GREEN_CONCRETE);
		ItemMeta createIslandMeta = createIsland.getItemMeta();
		ArrayList<String> createIslandLore = new ArrayList<String>();
			
		createIslandLore.add(ChatColor.GREEN+"Create your own island now!");
		
		createIslandMeta.setLore(createIslandLore);
		createIslandMeta.setDisplayName("¡±6¡±lCreate your island");
		
		
		createIsland.setItemMeta(createIslandMeta);
		createGUI.setItem(4, createIsland);
		
		//Here opens the inventory
		p.openInventory(createGUI);
		
	}

}
