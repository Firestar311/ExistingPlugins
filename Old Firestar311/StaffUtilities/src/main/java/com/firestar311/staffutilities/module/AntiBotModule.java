package com.firestar311.staffutilities.module;

import com.firestar311.lib.region.Cuboid;
import com.firestar311.lib.util.Utils;
import com.firestar311.staffutilities.StaffUtilities;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import java.util.*;

public class AntiBotModule implements Listener {
    
    private final StaffUtilities plugin;
    private Map<UUID, Cuboid> regions = new HashMap<>();
    
    public AntiBotModule(StaffUtilities plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Location minLocation = e.getPlayer().getLocation().subtract(2, 2, 2);
        Location maxLocation = e.getPlayer().getLocation().add(2, 2, 2);
        Cuboid cuboid = new Cuboid(minLocation, maxLocation);
        this.regions.put(e.getPlayer().getUniqueId(), cuboid);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> e.getPlayer().sendMessage(Utils.color("&cYou must move if you want to speak.")), 5L);
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (this.regions.containsKey(player.getUniqueId())) {
            Cuboid cuboid = this.regions.get(player.getUniqueId());
            if (!cuboid.contains(player)) {
                this.regions.remove(player.getUniqueId());
                player.sendMessage(Utils.color("&aChatting ability enabled."));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (this.regions.containsKey(player.getUniqueId())) {
            Cuboid cuboid = this.regions.get(player.getUniqueId());
            if (!cuboid.contains(player)) {
                this.regions.remove(player.getUniqueId());
            } else {
                player.sendMessage(Utils.color("&cYou must move around in order to chat."));
                e.setCancelled(true);
            }
        }
    }
}