package com.stardevmc.titangames.signs;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.arenas.Arena.ArenaState;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SignListener implements Listener {
    
    private TitanGames plugin;
    public SignListener(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onSignEdit(SignChangeEvent e) {
        Player player = e.getPlayer();
        if (!e.getLine(0).equalsIgnoreCase("[LobbySign]") || !e.getLine(0).equalsIgnoreCase("[LS]")) return;
        Arena arena = plugin.getArenaManager().getArena(e.getLine(1));
        if (arena == null) {
            player.sendMessage(Utils.color("&cThe arena name for the sign is not valid."));
            return;
        }
        
        new BukkitRunnable() {
            public void run() {
                LobbySign sign = new LobbySign(e.getBlock().getLocation(), arena);
                sign.updateSign();
                plugin.getSignManager().addLobbySign(sign);
                player.sendMessage(Utils.color(Messages.CREATE_LOBBY_SIGN_ACTION(arena)));
            }
        }.runTaskLater(plugin, 10L);
    }
    
    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        if (block == null) return;
        if (!(block.getState() instanceof Sign)) return;
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        LobbySign sign = plugin.getSignManager().getLobbySign(block.getLocation());
        if (sign == null) return;
        if (!sign.getArena().getState().equals(ArenaState.WAITING)) return;
        sign.getArena().addPlayer(player);
        sign.updateSign();
    }
}