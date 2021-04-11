package me.alonedev.ihhub.Events;

import me.alonedev.ihhub.IHhub;
import me.alonedev.ihhub.Utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class VoidTP implements Listener {

    private static IHhub main;

    public VoidTP(IHhub main) {
        this.main = main;
    }

    @EventHandler
    public void teleportOnVoid(EntityDamageEvent event) {

        if (event.getEntity() instanceof Player) {

            Player player = (Player) event.getEntity();
            if (event.getCause() != EntityDamageEvent.DamageCause.VOID) {
                return;
            }

            List<String> disabledWorlds = main.getConfig().getStringList("disabledWorlds");

            for (String world : disabledWorlds) {

                if (player.getWorld().getName() == world) {
                    Util.sendMsg("This world is disabled for voidTP!", player);
                    return;
                }
            }

            event.setCancelled(true);
            Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                @Override
                public void run() {
                    Location spawn = new Location(Bukkit.getWorld(main.getConfig().getString("Spawn.spawnWorld")), main.getConfig().getDouble("Spawn.x"), main.getConfig().getDouble("Spawn.y"), main.getConfig().getDouble("Spawn.z"), main.getConfig().getInt("Spawn.yaw"), main.getConfig().getInt("Spawn.pitch"));

                    player.teleport(spawn);
                    player.setFallDistance(0);
                    player.setHealth(20.0);

                }
            }, 10L);
        }



    }


}
