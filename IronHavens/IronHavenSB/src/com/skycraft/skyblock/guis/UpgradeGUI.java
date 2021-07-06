package com.skycraft.skyblock.guis;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class UpgradeGUI {
	
	public UpgradeGUI(Player p) {
		
		Inventory createdGUI = Bukkit.getServer().createInventory(p, 9, "Island Upgrades");
		
		// Upgrade Border Item
		
		ItemStack borderUpgrade = new ItemStack(Material.GLASS);
		ItemMeta borderUpgradeMeta = borderUpgrade.getItemMeta();
		ArrayList<String> borderUpgradeMetaLore = new ArrayList<String>();
			
		borderUpgradeMetaLore.add(ChatColor.GREEN+"Increase the world border of your island!");
		
		if (Bukkit.getWorld(p.getUniqueId().toString()).getWorldBorder().getSize() == 51) {
			
			borderUpgradeMetaLore.add(ChatColor.GREEN+"Current World Border: 50x50");
			borderUpgradeMetaLore.add(ChatColor.GREEN+"Next Upgrade: 100x100");
			
		} else if (Bukkit.getWorld(p.getUniqueId().toString()).getWorldBorder().getSize() == 101) {
			
			borderUpgradeMetaLore.add(ChatColor.GREEN+"Current World Border: 100x100");
			borderUpgradeMetaLore.add(ChatColor.GREEN+"Next Upgrade: 150x150");
			
		} else if (Bukkit.getWorld(p.getUniqueId().toString()).getWorldBorder().getSize() == 151) {
			
			borderUpgradeMetaLore.add(ChatColor.GREEN+"Current World Border: 150x150");
			borderUpgradeMetaLore.add(ChatColor.GREEN+"Next Upgrade: 200x200");
			
		} else if (Bukkit.getWorld(p.getUniqueId().toString()).getWorldBorder().getSize() == 201) {
			
			borderUpgradeMetaLore.add(ChatColor.GREEN+"Current World Border: 200x200");
			borderUpgradeMetaLore.add(ChatColor.GREEN+"This upgrade is maxed!!!");
			
		}
		
		borderUpgradeMeta.setLore(borderUpgradeMetaLore);
		borderUpgradeMeta.setDisplayName("¡±6¡±lIncrease world border");
		
		
		borderUpgrade.setItemMeta(borderUpgradeMeta);
		createdGUI.setItem(2, borderUpgrade);
		
		//Here opens the inventory
		p.openInventory(createdGUI);
		
	}

}
