package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.entity.Player;

@CommandInfo(description = "Remove an arena", aliases = {"removearena", "ra"}, usage = "<arenaName>", permission = "titangames.removearena")
public class RemoveArenaCommand extends GameCommand {
    
    private TitanGames plugin;
    public RemoveArenaCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length >= 0)) {
            player.sendMessage(Utils.color("&cYou must provide the name of the arena to remove"));
            return;
        }
        
        Arena arena = plugin.getArenaManager().getArena(args[0]);
        if (arena == null) {
            player.sendMessage(Utils.color("&cAn arena does not exist by that name."));
            return;
        }
        
        arena.removeArenaCleanup();
        plugin.getArenaManager().removeArena(arena);
        player.sendMessage(Utils.color(Messages.REMOVE_ARENA(arena)));
    }
}
