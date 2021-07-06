package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.region.Cuboid;
import com.firestar311.lib.region.SelectionManager;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.entity.Player;

@CommandInfo(description = "Create an arena", aliases = {"createarena", "ca"}, usage = "<arenaName>", permission = "titangames.createarena")
public class CreateArenaCommand extends GameCommand {
    
    private TitanGames plugin;
    public CreateArenaCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length >= 0)) {
            player.sendMessage(Utils.color("&cYou must provide a name for the arena."));
            return;
        }
        
        String name = args[0];
        
        if (name.length() > 16) {
            player.sendMessage(Utils.color("&cArena names cannot be longer than 16 characters."));
            return;
        }
    
        SelectionManager selManager = plugin.getSelectionManager();
        if (!selManager.hasSelection(player)) {
            player.sendMessage(Utils.color("&cYou must have a selection in order to create an arena."));
            return;
        }
    
        Cuboid region = selManager.getCuboid(player);
    
        Arena arena = new Arena(name, region);
        plugin.getArenaManager().addArena(arena);
        player.sendMessage(Utils.color(Messages.CREATE_ARENA(arena)));
    }
}
