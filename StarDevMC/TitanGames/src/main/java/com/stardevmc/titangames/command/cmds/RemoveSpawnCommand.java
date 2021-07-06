package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.entity.Player;

@CommandInfo(description = "Remove a spawn from an arena", aliases = {"removespawn", "rs"}, usage = "<arenaName> <spawnId>", permission = "titangames.removespawn")
public class RemoveSpawnCommand extends GameCommand {
    
    private TitanGames plugin;
    public RemoveSpawnCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length >= 1)) {
            player.sendMessage(Utils.color("&cYou must provide the arena name and/or spawn id"));
            return;
        }
        
        Arena arena = plugin.getArenaManager().getArena(args[0]);
        if (arena == null) {
            player.sendMessage(Utils.color("&cAn arena by that name does not exist."));
            return;
        }
        
        int spawnId;
        try {
            spawnId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(Utils.color("&cThe value you provided for the spawn id is not a number."));
            return;
        }
        
        if (arena.removeSpawn(spawnId)) {
            player.sendMessage(Utils.color(Messages.REMOVE_SPAWN(spawnId, arena)));
        } else {
            player.sendMessage(Utils.color("&cYou must have the arena in setup mode in order to remove a spawn."));
        }
    }
}
