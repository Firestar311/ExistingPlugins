package com.skycraft.skyblock.guis;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CreatedGUI {
	
	public CreatedGUI(Player p) {
		
		Inventory createdGUI = Bukkit.getServer().createInventory(p, 9, "Island viewer");
		
		// Teleport to island Item
		
		ItemStack teleportIsland = new ItemStack(Material.END_PORTAL_FRAME);
		ItemMeta teleportIslandMeta = teleportIsland.getItemMeta();
		ArrayList<String> teleportIslandLore = new ArrayList<String>();
			
		teleportIslandLore.add(ChatColor.GREEN+"Teleport to your island!");
		
		teleportIslandMeta.setLore(teleportIslandLore);
		teleportIslandMeta.setDisplayName("¡±6¡±lTeleport to your island");
		
		
		teleportIsland.setItemMeta(teleportIslandMeta);
		createdGUI.setItem(4, teleportIsland);
		
		// Upgrade island Item
		
		ItemStack upgradeIsland = new ItemStack(Material.DIAMOND);
		ItemMeta upgradeIslandMeta = upgradeIsland.getItemMeta();
		ArrayList<String> upgradeIslandLore = new ArrayList<String>();
		
		upgradeIslandLore.add(ChatColor.GREEN+"Upgrade things on your island!");
		
		upgradeIslandMeta.setLore(upgradeIslandLore);
		upgradeIslandMeta.setDisplayName("¡±6¡±lUpgrade your island");
		
		upgradeIsland.setItemMeta(upgradeIslandMeta);
		createdGUI.setItem(2, upgradeIsland);
		
		// Reload island Item
		
		ItemStack reloadIsland = new ItemStack(Material.REDSTONE);
		ItemMeta reloadIslandMeta = reloadIsland.getItemMeta();
		ArrayList<String> reloadIslandLore = new ArrayList<String>();
		
		reloadIslandLore.add(ChatColor.RED+"Reload island and start over!");
		
		reloadIslandMeta.setLore(reloadIslandLore);
		reloadIslandMeta.setDisplayName("¡±c¡±lReload Island");
		
		reloadIsland.setItemMeta(reloadIslandMeta);
		createdGUI.setItem(0, reloadIsland);
		
		// Delete island Item
		
		ItemStack deleteIsland = new ItemStack(Material.BARRIER);
		ItemMeta deleteIslandMeta = deleteIsland.getItemMeta();
		ArrayList<String> deleteIslandLore = new ArrayList<String>();

		deleteIslandLore.add(ChatColor.RED+"Delete island and start over!");

		deleteIslandMeta.setLore(deleteIslandLore);
		deleteIslandMeta.setDisplayName("¡±c¡±lDelete Island");

		deleteIsland.setItemMeta(deleteIslandMeta);
		createdGUI.setItem(8, deleteIsland);
				
		//Here opens the inventory
		p.openInventory(createdGUI);
		
	}

}
