package com.craftyun83.ironhavensb.commands;

import com.craftyun83.ironhavensb.Main;
import com.craftyun83.ironhavensb.island.InviteIsland;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.craftyun83.ironhavensb.Main;
import com.craftyun83.ironhavensb.guis.CreateGUI;
import com.craftyun83.ironhavensb.guis.CreatedGUI;

public class IslandCommands implements CommandExecutor {
	
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

			try {

				if (args[0].equals("testanvil")) {

					new InviteIsland(p, plugin);

				}

			} catch (ArrayIndexOutOfBoundsException exc) {

				if (Bukkit.getWorlds().contains(Bukkit.getWorld(p.getUniqueId().toString()))) {

					new CreatedGUI(p);

				} else {

					new CreateGUI(p);

				}

			}
			
		} 
		
		return false;
	}

}
