package me.alonedev.ihhub.Events;

import me.alonedev.ihhub.IHhub;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEvent implements Listener {

    private static IHhub main;

    public QuitEvent(IHhub main) {
        this.main = main;
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {


        String username = event.getPlayer().getDisplayName();


        //Quit Message
        event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("Messages.Quit.QuitMessage").
                replace("%player%", username)));

    }

}
