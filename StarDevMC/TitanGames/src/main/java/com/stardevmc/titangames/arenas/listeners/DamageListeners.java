package com.stardevmc.titangames.arenas.listeners;

import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.arenas.Arena.ArenaState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListeners implements Listener {
    
    private TitanGames plugin;
    public DamageListeners(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        cancelPregameDamage(e, player);
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player player = (Player) e.getDamager();
        cancelPregameDamage(e, player);
    }
    
    private void cancelPregameDamage(EntityDamageEvent e, Player player) {
        Arena arena = plugin.getArenaManager().getArena(player);
        if (arena == null) return;
        if (!arena.getState().equals(ArenaState.IN_GAME)) e.setCancelled(true);
    }
}