package me.alonedev.ironhhub.Mechanics;

import me.alonedev.ironhhub.IronHhub;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class VoidTP implements Listener {

    private IronHhub main;

    public VoidTP(IronHhub main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerMove(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                event.setCancelled(true);
                Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                    @Override
                    public void run() {
                        Location spawn = new Location(Bukkit.getWorld(IronHhub.spawnworld), IronHhub.x, IronHhub.y, IronHhub.z, IronHhub.yaw, IronHhub.pitch);
                        player.teleport(spawn);
                        player.setFallDistance(0);
                        player.setHealth(20.0);
                    }

                }, 10L);
            }
        }


    }
}

