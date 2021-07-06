package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import org.bukkit.entity.Player;

@CommandInfo(description = "Lists information about an arena.", aliases = {"viewarena", "va"}, usage = "<arena>", permission = "titangames.viewarena")
public class ArenaViewCommand extends GameCommand {
    
    private TitanGames plugin;
    public ArenaViewCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length >= 0)) {
            player.sendMessage(Utils.color("&cYou must provide the arena name to add the spawn."));
            return;
        }
    
        Arena arena = this.getArenaFromArgs(plugin, player, args);
        if (arena == null) return;
    
        player.sendMessage(Utils.color("&6-----Arena Info " + arena.getName() + "-----"));
        player.sendMessage(Utils.color("&7Name: " + arena.getName()));
        player.sendMessage(Utils.color("&7State: " + arena.getState().name()));
        player.sendMessage(Utils.color("&7World: " + arena.getWorld().getName()));
        player.sendMessage(Utils.color("&7Player Count: " + arena.getTributes().size()));
        player.sendMessage(Utils.color("&7Kits In Use: " + arena.getKitsInUse().size()));
        player.sendMessage(Utils.color("&7Total Spawn Count: " + arena.getSpawnpoints().size()));
        player.sendMessage(Utils.color("&7Total Chest Count: " + arena.getChests().size()));
        player.sendMessage(Utils.color("&7Total Block Count: " + arena.getRegion().getTotalBlockSize()));
    }
}