package me.alonedev.ihhub.Events;

import me.alonedev.ihhub.IHhub;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class OnRespawn implements Listener {

    private static IHhub main;

    public OnRespawn(IHhub main) {
        this.main = main;
    }

    @EventHandler
    public void PlayerSpawn(PlayerRespawnEvent event) {

        Location spawn = new Location(Bukkit.getWorld(main.getConfig().getString("Spawn.spawnWorld")), main.getConfig().getDouble("Spawn.x"), main.getConfig().getDouble("Spawn.y"), main.getConfig().getDouble("Spawn.z"), main.getConfig().getInt("Spawn.yaw"), main.getConfig().getInt("Spawn.pitch"));
        event.setRespawnLocation(spawn);
    }

}
