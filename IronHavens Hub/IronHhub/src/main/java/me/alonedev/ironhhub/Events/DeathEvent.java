package me.alonedev.ironhhub.Events;

import me.alonedev.ironhhub.IronHhub;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathEvent implements Listener {

    private IronHhub main;

    public DeathEvent (IronHhub main) {
        this.main = main;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        final Player player = e.getEntity();

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            player.spigot().respawn();
        }, 1L);
    }
}