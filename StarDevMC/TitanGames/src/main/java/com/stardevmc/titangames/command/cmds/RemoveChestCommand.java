package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

@CommandInfo(description = "Remove a chest from an arena", aliases = {"removechest", "rc"}, usage = "<arenaName>", permission = "titangames.removechest")
public class RemoveChestCommand extends GameCommand {
    
    private TitanGames plugin;
    public RemoveChestCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length >= 0)) {
            player.sendMessage(Utils.color("&cYou must provide the name of the arena to remove a chest from."));
            return;
        }
        
        Arena arena = plugin.getArenaManager().getArena(args[0]);
        if (arena == null) {
            player.sendMessage(Utils.color("&cAn arena does not exist by that name."));
            return;
        }
    
        Chest chest = this.getChestFromPlayer(player);
        if (chest == null) return;
        
        if (!arena.hasChest(chest.getLocation())) {
            player.sendMessage(Utils.color("&cThe arena provided does not contain the chest you are looking at."));
            return;
        }
        
        if (arena.removeChest(chest.getLocation())) {
            player.sendMessage(Utils.color(Messages.REMOVE_CHEST(arena)));
        } else {
            player.sendMessage(Utils.color("&cYou must have the arena in setup mode in order to remove chests."));
        }
    }
}
