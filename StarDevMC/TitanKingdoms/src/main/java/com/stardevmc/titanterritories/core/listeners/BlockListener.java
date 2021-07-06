package com.stardevmc.titanterritories.core.listeners;

import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.holder.Kingdom;
import com.stardevmc.titanterritories.core.objects.enums.Flag;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class BlockListener implements Listener {
    
    private TitanTerritories plugin = TitanTerritories.getInstance();
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Kingdom kingdom = plugin.getKingdomManager().getKingdom(e.getBlock().getLocation());
        if (kingdom == null) return;
        if (!kingdom.getClaimController().canBuild(player.getUniqueId())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Kingdom kingdom = plugin.getKingdomManager().getKingdom(e.getBlock().getLocation());
        if (kingdom == null) return;
        if (!kingdom.getClaimController().canBuild(player.getUniqueId())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEndermanChange(EntityChangeBlockEvent e) {
        if (!e.getEntityType().equals(EntityType.ENDERMAN)) return;
        Kingdom kingdom = plugin.getKingdomManager().getKingdom(e.getBlock().getLocation());
        if (kingdom == null) return;
        if (!kingdom.getFlagController().getFlags().contains(Flag.ENDER_GRIEF)) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        Kingdom kingdom = plugin.getKingdomManager().getKingdom(e.getBlock().getLocation());
        if (kingdom == null) return;
        
        if (kingdom.getUserController().getOnlineUsers().isEmpty()) {
            if (!kingdom.getFlagController().getFlags().contains(Flag.OFFLINE_EXPLOSIONS)) {
               e.setCancelled(true);
            }
        } else {
            if (!kingdom.getFlagController().getFlags().contains(Flag.EXPLOSIONS)) {
                e.setCancelled(true);
            }
        }
        
    }
}