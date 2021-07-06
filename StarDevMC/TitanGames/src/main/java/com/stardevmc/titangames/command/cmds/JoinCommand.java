package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.entity.Player;

@CommandInfo(description = "Join an arena", aliases = {"join", "j"}, usage = "<arenaName>")
public class JoinCommand extends GameCommand {
    
    private TitanGames plugin;
    public JoinCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length >= 0)) {
            player.sendMessage(Utils.color("&cYou must provide the arena name to join"));
            return;
        }
        
        Arena arena = this.getArenaFromArgs(plugin, player, args);
        if (arena == null) return;
    
        player.sendMessage(Utils.color(Messages.JOIN_ARENA_COMMAND(arena)));
        arena.addPlayer(player);
    }
}
