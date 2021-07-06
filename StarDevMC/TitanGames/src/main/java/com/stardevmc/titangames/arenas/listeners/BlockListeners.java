package com.stardevmc.titangames.arenas.listeners;

import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.arenas.Arena.ArenaState;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;

public class BlockListeners implements Listener {

    private TitanGames plugin;
    public BlockListeners(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        cancelArenaBlockEvents(player, e);
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        cancelArenaBlockEvents(player, e);
    }
    
    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        cancelArenaBlockEvents(e.getBlock().getLocation(), e);
    }
    
    @EventHandler
    public void onBlockDamage(BlockDamageEvent e) {
        cancelArenaBlockEvents(e.getBlock().getLocation(), e);
    }
    
    @EventHandler
    public void onBlockExplodeEvent(BlockExplodeEvent e) {
        cancelArenaBlockEvents(e.getBlock().getLocation(), e);
    }
    
    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        cancelArenaBlockEvents(e.getBlock().getLocation(), e);
    }
    
    @EventHandler
    public void onBlockFertilize(BlockFertilizeEvent e) {
        cancelArenaBlockEvents(e.getBlock().getLocation(), e);
    }
    
    @EventHandler
    public void onBlockForm(BlockFormEvent e) {
        cancelArenaBlockEvents(e.getBlock().getLocation(), e);
    }
    
    @EventHandler
    public void onBlockGrow(BlockGrowEvent e) {
        cancelArenaBlockEvents(e.getBlock().getLocation(), e);
    }
    
    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e) {
        cancelArenaBlockEvents(e.getBlock().getLocation(), e);
    }
    
    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent e) {
        cancelArenaBlockEvents(e.getBlock().getLocation(), e);
    }
    
    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent e) {
        cancelArenaBlockEvents(e.getBlock().getLocation(), e);
    }
    
    @EventHandler
    public void onBlockSpread(BlockSpreadEvent e) {
        cancelArenaBlockEvents(e.getBlock().getLocation(), e);
    }
    
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        cancelArenaBlockEvents(e.getBlock().getLocation(), e);
    }
    
    @EventHandler
    public void onMoistureChangeEvent(MoistureChangeEvent e) {
        cancelArenaBlockEvents(e.getBlock().getLocation(), e);
    }
    
    private void cancelArenaBlockEvents(Location location, Cancellable e) {
        Arena arena = plugin.getArenaManager().getArena(location);
        if (arena == null) return;
        e.setCancelled(true);
    }
    
    private void cancelArenaBlockEvents(Player player, Cancellable e) {
        Arena arena = plugin.getArenaManager().getArena(player);
        if (arena == null) return;
        if (arena.getState().equals(ArenaState.SETUP)) return;
        e.setCancelled(true);
    }
}