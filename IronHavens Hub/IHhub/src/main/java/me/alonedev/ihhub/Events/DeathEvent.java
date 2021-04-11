package me.alonedev.ihhub.Events;

import me.alonedev.ihhub.IHhub;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathEvent implements Listener {

    private static IHhub main;

    public DeathEvent(IHhub main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            event.getEntity().spigot().respawn();
        }, 1L);
    }


}
