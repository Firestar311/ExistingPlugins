package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.entity.Player;

@CommandInfo(description = "Force start an arena", aliases = {"start", "s"}, usage = "<arenaName>", permission = "titangames.forcestart")
public class ForceStartCommand extends GameCommand {
    
    private TitanGames plugin;
    public ForceStartCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length >= 0)) {
            player.sendMessage(Utils.color("&cYou must provide the arena name to force start."));
            return;
        }
        
        Arena arena = this.getArenaFromArgs(plugin, player, args);
        if (arena == null) return;
    
        arena.forceStart(player);
        player.sendMessage(Utils.color(Messages.FORCE_START_COMMAND(arena)));
    }
}
