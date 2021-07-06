package com.skycraft.skyblock.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.skycraft.skyblock.Main;

public class SpawnCommand implements CommandExecutor {

	@SuppressWarnings("unused")
	private Main plugin;
	
	public SpawnCommand(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginCommand("spawn").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			
			sender.sendMessage(ChatColor.RED+"[Iron Haven] >> This command is only available for players!");
			
		} else {
			
			Player p = (Player) sender;
			
			p.teleport(new Location(Bukkit.getWorld("world"), 0.5, 161, 0.5));
			
		}
		
		return false;
	}

}
