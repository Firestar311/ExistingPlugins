package com.stardevmc.titangames.kits;

import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.arenas.Arena.ArenaState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class KitSelectorListener implements Listener {
    
    private TitanGames plugin;
    public KitSelectorListener(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;
        ItemStack handItem = e.getItem();
        if (handItem == null) return;
        if (!handItem.hasItemMeta()) return;
        if (handItem.getItemMeta().getDisplayName() == null) return;
        if (!handItem.getItemMeta().getDisplayName().toLowerCase().contains("kit selector")) return;
        e.setCancelled(true);
        Arena arena = plugin.getArenaManager().getArena(player);
        if (arena == null) return;
        if (arena.getState().equals(ArenaState.WAITING) || arena.getState().equals(ArenaState.COUNTDOWN)) {
            player.openInventory(new KitSelector().getInventory());
        }
    }
}