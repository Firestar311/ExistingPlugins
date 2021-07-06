package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.arenas.chests.ArenaChest;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

@CommandInfo(description = "Add a chest to an arena", aliases = {"addchest", "ac"}, usage = "<arenaName> <chestTeir>", permission = "titangames.addchest")
public class AddChestCommand extends GameCommand {
    
    private TitanGames plugin;
    
    public AddChestCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length > 1)) {
            player.sendMessage(Utils.color("&cYou must provide the arena name and/or chest teir"));
            return;
        }
        
        Arena arena = this.getArenaFromArgs(plugin, player, args);
        if (arena == null) return;
        
        int chestTeir;
        try {
            chestTeir = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(Utils.color("&cThe value you provided for the chest teir is not a valid number."));
            return;
        }
        
        if (plugin.getChestTierManger().getChestTeir(chestTeir) == null) {
            player.sendMessage(Utils.color("&cThe chest teir number is not a valid chest teir."));
            return;
        }
        
        Chest chest = this.getChestFromPlayer(player);
        if (chest == null) return;
        
        if (!arena.getRegion().contains(chest.getLocation())) {
            player.sendMessage(Utils.color("&cThe chest you are looking at is not in the arena."));
            return;
        }
        
        ArenaChest arenaChest = new ArenaChest(chest.getLocation(), chestTeir);
        if (arena.addChest(arenaChest)) {
            player.sendMessage(Utils.color(Messages.CHEST_ADD(arena)));
        } else {
            player.sendMessage(Utils.color("&cYou must have the arena in setup mode in order to add a chest."));
        }
    }
}
