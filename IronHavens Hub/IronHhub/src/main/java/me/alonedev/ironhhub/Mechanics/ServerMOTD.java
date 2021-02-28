package me.alonedev.ironhhub.Mechanics;

import me.alonedev.ironhhub.IronHhub;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;


public class ServerMOTD implements Listener {

    private IronHhub main;
    public ServerMOTD (IronHhub main) {
        this.main = main;
    }

    @EventHandler
    public void onPing(ServerListPingEvent event){
        String MOTD = IronHhub.MOTD;
        event.setMotd(ChatColor.translateAlternateColorCodes('&', MOTD)
                .replace("%line%", "\n"));
    }
    
}
