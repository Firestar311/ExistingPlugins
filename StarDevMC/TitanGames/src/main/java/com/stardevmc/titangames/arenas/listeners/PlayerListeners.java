package com.stardevmc.titangames.arenas.listeners;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.arenas.Arena.ArenaState;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListeners implements Listener {
    
    private TitanGames plugin;
    
    public PlayerListeners(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Arena arena = plugin.getArenaManager().getArena(player);
        if (arena == null) return;
        arena.removePlayer(player);
        arena.sendMessageToAll(Utils.color(Messages.LEAVE_ARENA(player.getName())));
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Arena arena = plugin.getArenaManager().getArena(player);
        if (arena == null) return;
        if (arena.getState().equals(ArenaState.WAITING) || arena.getState().equals(ArenaState.COUNTDOWN)) {
            if (!(e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ())) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Arena arena = plugin.getArenaManager().getArena(player);
        if (arena == null) return;
        String itemName = player.getKiller().getInventory().getItemInMainHand().getType().name().replace("_", " ").toLowerCase();
        arena.sendMessageToAll(Utils.color(Messages.KILLED_BY_PLAYER(player.getKiller(), player, itemName)));
        new BukkitRunnable() {
            public void run() {
                player.spigot().respawn();
                arena.removePlayer(player);
            }
        }.runTaskLater(plugin, 5L);
        e.setDeathMessage(null);
    }
}