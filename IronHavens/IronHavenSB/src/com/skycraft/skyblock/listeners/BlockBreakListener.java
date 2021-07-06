package com.skycraft.skyblock.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.skycraft.skyblock.Main;

public class BlockBreakListener implements Listener {
	
	private Main plugin;
	
	public BlockBreakListener (Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		
		FileConfiguration dmconfig = plugin.DiamondMinedConfig;
		
		if (e.getBlock().getType() == Material.DIAMOND_ORE) {
			
			if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
				
				if (dmconfig.getString(e.getPlayer().getName()) == null) {
					
					dmconfig.createSection(e.getPlayer().getName());
					dmconfig.set(e.getPlayer().getName(), 1);
					
					plugin.saveDiamondMinedYML(dmconfig, plugin.DiamondMinedYML);
					
				} else {
					
					dmconfig.set(e.getPlayer().getName(), dmconfig.getInt(e.getPlayer().getName())+1);
					plugin.saveDiamondMinedYML(dmconfig, plugin.DiamondMinedYML);
					
				}
				
			}
			
		}
		
	}

}
