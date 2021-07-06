package com.skycraft.skyblock.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.skycraft.skyblock.Main;
import com.skycraft.skyblock.guis.CreateGUI;
import com.skycraft.skyblock.guis.CreatedGUI;

public class IslandCommands implements CommandExecutor {
	
	@SuppressWarnings("unused")
	private Main plugin;
	
	public IslandCommands(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginCommand("is").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			
			sender.sendMessage(ChatColor.RED+"[Iron Haven] >> This command is only available to players!");
			
		} else {
			
			Player p = (Player) sender;
			
			if (Bukkit.getWorlds().contains(Bukkit.getWorld(p.getUniqueId().toString()))) {
				
				new CreatedGUI(p);
				
			} else {
				
				new CreateGUI(p);
				
			}
			
		} 
		
		return false;
	}

}
