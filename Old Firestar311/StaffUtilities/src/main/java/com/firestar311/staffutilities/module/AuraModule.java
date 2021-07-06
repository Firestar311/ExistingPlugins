package com.firestar311.staffutilities.module;

import com.firestar311.staffutilities.StaffUtilities;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AuraModule implements Listener {
    
    private StaffUtilities plugin;
    
    private Map<String, Integer[]> colors = new HashMap<>();
    
    public AuraModule(StaffUtilities plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        
        Random random = new Random();
        new BukkitRunnable() {
            public void run() {
                Location loc = player.getLocation();
                for (int i = 0; i < 20; i++) {
                    double x = loc.getX();
                    double xOffset = random.nextDouble();
                    int xType = random.nextInt(2);
                    double particleX = 0;
                    if (xType == 1) {
                        particleX = x + xOffset;
                    } else {
                        particleX = x - xOffset;
                    }
                    double y = loc.getY();
                    double yOffset = random.nextDouble();
                    int yType = random.nextInt(2);
                    double particleY;
                    if (yType == 1) {
                        particleY = y + yOffset;
                    } else {
                        particleY = y - yOffset;
                    }
                    particleY += yType;
                    double z = loc.getZ();
                    double zOffset = random.nextDouble();
                    int zType = random.nextInt(2);
                    double particleZ = 0;
                    if (zType == 1) {
                        particleZ = z + zOffset;
                    } else {
                        particleZ = z - zOffset;
                    }
                    DustOptions dustOptions = new DustOptions(Color.GREEN, 1);
                    player.spawnParticle(Particle.REDSTONE, particleX, particleY, particleZ, 1, dustOptions);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}