package net.brutuspvp.core.managers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;

import net.brutuspvp.core.BrutusCore;

public class MOTDManager implements Listener {
	
	public MOTDManager(BrutusCore plugin) {
		plugin.registerListener(this);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		
	}
	
	@EventHandler
	public void onServerListPing(ServerListPingEvent e) {
		e.setMotd("\u00A74\u00A7lBrutusPvP Rome \u00A76\u00A7l| \u00A7b\u00A7lbrutuspvp.net\n"
	            + "\u00A7bCurrent Phase: \u00A7c\u00A7lCLOSED ALPHA");
		
	}
}