package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.arenas.Arena.ArenaState;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import org.bukkit.entity.Player;

@CommandInfo(description = "Setup mode for an arena", aliases = {"setup", "su"}, usage = "<arenaName>", permission = "titangames.setup")
public class SetupCommand extends GameCommand {
    
    private TitanGames plugin;
    public SetupCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length >= 0)) {
            player.sendMessage(Utils.color("&cYou must provide the arena name."));
            return;
        }
        
        Arena arena = plugin.getArenaManager().getArena(args[0]);
        if (arena == null) {
            player.sendMessage(Utils.color("&cAn arena by that name does not exist."));
            return;
        }
        
        if (arena.getState().equals(ArenaState.SETUP)) {
            arena.allowPlayers();
            player.sendMessage(Utils.color("&aYou changed &b" + arena.getName() + " &ato waiting mode."));
        } else {
            arena.setup();
            player.sendMessage(Utils.color("&aSet the arena &b" + arena.getName() + " &ato setup mode."));
        }
    }
}
