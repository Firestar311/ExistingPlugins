package me.alonedev.ironhhub.Mechanics;

import me.alonedev.ironhhub.IronHhub;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class VoidTP implements Listener {


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = (Player) event.getPlayer();
        if(player.getLocation().getY() < -5) {
            Location spawn = new Location(Bukkit.getWorld(IronHhub.spawnworld), IronHhub.x, IronHhub.y, IronHhub.z, IronHhub.yaw, IronHhub.pitch);
            player.teleport(spawn);
        }
    }


}

