package com.skycraft.skyblock.upgrades;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.skycraft.skyblock.Main;

import net.milkbowl.vault.economy.Economy;

public class BorderUpgrade {
	
	@SuppressWarnings("unused")
	private Main plugin;
	
	public BorderUpgrade(Player p) {
		
		Economy econ = Main.econ;
		World world = Bukkit.getWorld(p.getUniqueId().toString());
		double borderSize = world.getWorldBorder().getSize();
		
		if (borderSize == 51) {
			
			if (econ.getBalance(p) >= 5000) {
				
				econ.withdrawPlayer(p, 5000);
				p.sendMessage(ChatColor.GREEN+"[Iron Haven] >> Succesfully bought a island size upgrade for $5000!");
				world.getWorldBorder().setSize(101);
				p.closeInventory();
				
			} else {
				
				p.sendMessage(ChatColor.RED+"[Iron Haven] >> You do not have enough money to buy this upgrade!");
				p.closeInventory();
				
			}
			
		} else if (borderSize == 101) {
			
			if (econ.getBalance(p) >= 7500) {
				
				econ.withdrawPlayer(p, 7500);
				p.sendMessage(ChatColor.GREEN+"[Iron Haven] >> Succesfully bought a island size upgrade for $7500!");
				world.getWorldBorder().setSize(151);
				p.closeInventory();
				
			} else {
				
				p.sendMessage(ChatColor.RED+"[Iron Haven] >> You do not have enough money to buy this upgrade!");
				p.closeInventory();
				
			}
			
		} else if (borderSize == 151) {
			
			if (econ.getBalance(p) >= 10000) {
				
				econ.withdrawPlayer(p, 10000);
				p.sendMessage(ChatColor.GREEN+"[Iron Haven] >> Succesfully bought a island size upgrade for $10000!");
				world.getWorldBorder().setSize(201);
				p.closeInventory();
				
			} else {
				
				p.sendMessage(ChatColor.RED+"[Iron Haven] >> You do not have enough money to buy this upgrade!");
				p.closeInventory();
				
			}
			
		} else {
			
			p.sendMessage(ChatColor.RED+"[Iron Haven] >> You have already maxed out this upgrade!");
			p.closeInventory();
			
		}
		
	}

}
