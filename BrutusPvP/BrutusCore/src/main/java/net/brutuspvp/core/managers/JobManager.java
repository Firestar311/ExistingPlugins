package net.brutuspvp.core.managers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import net.brutuspvp.core.BrutusCore;

@SuppressWarnings("unused")
public class JobManager implements CommandExecutor, Listener {

	private BrutusCore plugin;
	
	public JobManager(BrutusCore plugin) {
		this.plugin = plugin;
		plugin.registerListener(this);
		this.loadJobs();
	}
	
	public void saveJobs() {
		
	}
	
	public void loadJobs() {
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		
		
		return true;
	}
}