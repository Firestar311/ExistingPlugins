package me.alonedev.ihhub.Mechanics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;

public class VoidTP implements Listener {


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = (Player) event.getPlayer();
        if(player.getLocation().getY() < 0) {
            Location spawn = new Location(Bukkit.getWorld("world"), -16.5, 67, -22.5, 0, 0);
            player.teleport(spawn);
        }
    }


}
