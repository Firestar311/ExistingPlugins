package com.stardevmc.titangames.command;

import com.firestar311.lib.pagination.Paginator;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.block.*;
import org.bukkit.entity.Player;

public abstract class GameCommand {
    public abstract void onCommand(Player player, String[] args);
    
    protected Arena getArenaFromArgs(TitanGames plugin, Player player, String[] args) {
        Arena arena = plugin.getArenaManager().getArena(args[0]);
        if (arena == null) {
            player.sendMessage(Utils.color(Messages.INVALID_ARENA_NAME()));
            return null;
        }
        
        return arena;
    }
    
    protected Block getBlockFromPlayer(Player player) {
        Block block = player.getTargetBlock(null, 10);
        if (block == null) {
            player.sendMessage(Utils.color("&cYou must be looking at a block."));
            return null;
        }
        return block;
    }
    
    protected Chest getChestFromPlayer(Player player) {
        Block block = getBlockFromPlayer(player);
        if (block == null) return null;
        
        if (!(block.getState() instanceof Chest)) {
            player.sendMessage(Utils.color("&cYou must be looking at a chest."));
            return null;
        }
        return ((Chest) block.getState());
    }
    
    protected Sign getSignFromPlayer(Player player) {
        Block block = getBlockFromPlayer(player);
        if (block == null) return null;
        
        if (!(block.getState() instanceof Sign)) {
            player.sendMessage(Utils.color("&cYou must be looking at a sign."));
            return null;
        }
        
        return ((Sign) block.getState());
    }
    
    protected void displayPaginator(Paginator<?> paginator, Player player, int length, String[] args) {
        if (args.length == length) {
            paginator.display(player, 1);
        } else {
            paginator.display(player, args[length]);
        }
    }
}