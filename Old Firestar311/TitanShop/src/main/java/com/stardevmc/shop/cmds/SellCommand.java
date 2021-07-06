package com.stardevmc.shop.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.shop.ShopUtils;
import com.stardevmc.shop.gui.SellGUI;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class SellCommand implements CommandExecutor {
    
    public SellCommand() {
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!Utils.isPlayer(sender)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
    
        Player player = ((Player) sender);
        
        if (args.length == 0) {
            new SellGUI().openGUI(player);
        } else if (Utils.checkCmdAliases(args, 0, "all")) {
            ShopUtils.sellAllInventory(player, player.getInventory(), false);
        } else {
            player.sendMessage(Utils.color("&cInvalid subcommand."));
        }
        return true;
    }
}
