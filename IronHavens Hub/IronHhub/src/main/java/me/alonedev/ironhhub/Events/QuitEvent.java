package me.alonedev.ironhhub.Events;

import me.alonedev.ironhhub.IronHhub;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEvent implements Listener {

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent event) {

        String username = event.getPlayer().getDisplayName();

        event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', IronHhub.QuitMessage)
                .replace("%player%", username).
                replace("%line%", "\n"));

    }

}
