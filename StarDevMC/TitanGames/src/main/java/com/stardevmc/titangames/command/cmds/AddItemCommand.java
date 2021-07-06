package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.chests.ChestTier;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.entity.Player;

@CommandInfo(description = "Add an item to a chest tier", aliases = {"additem", "ai"}, usage = "<tierNumber>, <chance>", permission = "titangames.additem")
public class AddItemCommand extends GameCommand {
    
    private TitanGames plugin;
    public AddItemCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    @SuppressWarnings("Duplicates")
    public void onCommand(Player player, String[] args) {
        if (!(args.length > 1)) {
            player.sendMessage(Utils.color("&cYou must provide a chest tier number and/or max items"));
            return;
        }
    
        int number;
        try {
            number = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(Utils.color("&cThe value for the tier is not a valid number"));
            return;
        }
    
        int chance;
        try {
            chance = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(Utils.color("&cThe value for the chance is not a valid number"));
            return;
        }
    
        ChestTier chestTier = plugin.getChestTierManger().getChestTeir(number);
        if (chestTier == null) {
            player.sendMessage(Utils.color("&cThe number for the chest tier is not a valid chest tier"));
            return;
        }
        
        chestTier.addItem(player.getInventory().getItemInMainHand(), chance);
        player.sendMessage(Utils.color(Messages.ADD_ITEM(chestTier, chance)));
    }
}