package com.stardevmc.titanterritories.core.listeners;

import com.stardevmc.titanterritories.core.TitanTerritories;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class ShopListener implements Listener {
    
    private TitanTerritories plugin = TitanTerritories.getInstance();
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
    
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    
    }
}