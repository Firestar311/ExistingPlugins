package me.alonedev.ihhub.Mechanics;

import me.alonedev.ihhub.IHhub;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.List;

public class ServerMOTD implements Listener {

    private static IHhub main;

    public ServerMOTD(IHhub main) {
        this.main = main;
    }

    @EventHandler
    public void ServerPing(ServerListPingEvent event) {

        List<String> MOTD = main.getConfig().getStringList("MOTD");
        event.setMotd(ChatColor.translateAlternateColorCodes('&', MOTD.get(0) + "\n" + MOTD.get(1))
                .replace("%line%", "\n"));
    }



}
