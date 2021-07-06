package net.brutuspvp.core.managers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import net.brutuspvp.core.BrutusCore;

public class GUIManager implements Listener, CommandExecutor {
	
	private BrutusCore plugin;
	
	public GUIManager(BrutusCore plugin) {
		this.plugin = plugin;
		this.plugin.registerListener(this);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		return true;
	}
}