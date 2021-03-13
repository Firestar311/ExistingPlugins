package me.alonedev.ironhhub.Events;

import me.alonedev.ironhhub.IronHhub;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class OnRespawn implements Listener {

    public void Respawn (PlayerRespawnEvent event) {
        Location spawn = new Location(Bukkit.getWorld(IronHhub.spawnworld), IronHhub.x, IronHhub.y, IronHhub.z, IronHhub.yaw, IronHhub.pitch);
        event.setRespawnLocation(spawn);
    }

}
