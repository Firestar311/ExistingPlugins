package com.stardevmc.titanterritories.core.listeners;

import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.enums.Flag;
import com.stardevmc.titanterritories.core.objects.holder.Kingdom;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EntityListener implements Listener {
    
    private TitanTerritories plugin = TitanTerritories.getInstance();
    
    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        Kingdom kingdom = plugin.getKingdomManager().getKingdom(event.getLocation());
        if (kingdom == null) return;
        if (entity instanceof Monster) {
            if (!kingdom.getFlagController().getFlags().contains(Flag.MONSTERS)) {
                event.setCancelled(true);
            }
        } else if (entity instanceof Animals) {
            if (!kingdom.getFlagController().getFlags().contains(Flag.ANIMALS)) {
                event.setCancelled(true);
            }
        } else if (entity instanceof Projectile) {
            if (!kingdom.getFlagController().getFlags().contains(Flag.PROJECTILES)) {
                event.setCancelled(true);
            }
        }
    }
}