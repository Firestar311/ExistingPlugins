package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.entity.Player;

@CommandInfo(description = "Leave an arena", aliases = {"leave", "l"}, usage = "")
public class LeaveCommand extends GameCommand {
    
    private TitanGames plugin;
    public LeaveCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        Arena arena = plugin.getArenaManager().getArena(player);
        if (arena == null) {
            player.sendMessage(Utils.color("&cYou are not in an arena."));
            return;
        }
    
        arena.removePlayer(player);
        player.sendMessage(Utils.color(Messages.LEAVE_ARENA_COMMAND(arena)));
    }
}
