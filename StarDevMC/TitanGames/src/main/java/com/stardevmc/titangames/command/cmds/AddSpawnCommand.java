package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.arenas.Spawn;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandInfo(description = "Add a spawn to an arena", aliases = {"addspawn", "as"}, usage = "<arenaName>", permission = "titangames.addspawn")
public class AddSpawnCommand extends GameCommand {
    
    private TitanGames plugin;
    public AddSpawnCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length >= 0)) {
            player.sendMessage(Utils.color("&cYou must provide the arena name to add the spawn."));
            return;
        }
        
        Arena arena = this.getArenaFromArgs(plugin, player, args);
        if (arena == null) return;
        
        if (!arena.getRegion().contains(player.getLocation())) {
            player.sendMessage(Utils.color("&cThe location you are at is not in the arena."));
            return;
        }
    
        Location location = player.getLocation();
        int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
        location = new Location(location.getWorld(), x + .5, y + .5, z + .5, location.getYaw(), location.getPitch());
        
        int spawnId = arena.addSpawn(new Spawn(location));
        if (spawnId != -1) {
            player.sendMessage(Utils.color(Messages.ADD_SPAWN(arena)));
        } else {
            player.sendMessage(Utils.color("&cYou must have the arena in setup mode in order to add a spawnpoint."));
        }
    }
}
