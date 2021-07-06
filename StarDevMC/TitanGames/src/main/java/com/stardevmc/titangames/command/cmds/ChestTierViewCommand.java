package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.chests.ChestTier;
import com.stardevmc.titangames.arenas.chests.ChestTierViewGUI;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import org.bukkit.entity.Player;

@CommandInfo(description = "View the contents of a chest tier", aliases = {"viewchesttier", "vct"}, usage = "<chesttier>")
public class ChestTierViewCommand extends GameCommand {
    
    private TitanGames plugin;
    public ChestTierViewCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(Utils.color("&cYou did not provide a chest tier number."));
            return;
        }
    
        int tier;
        try {
            tier = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(Utils.color("&cThe value for the chest tier is not a valid number"));
            return;
        }
        ChestTier chestTier = plugin.getChestTierManger().getChestTeir(tier);
        if (chestTier == null) {
            player.sendMessage(Utils.color("&cThe value does not match a valid chest tier."));
            return;
        }
    
        new ChestTierViewGUI(chestTier).openGUI(player);
    }
}
