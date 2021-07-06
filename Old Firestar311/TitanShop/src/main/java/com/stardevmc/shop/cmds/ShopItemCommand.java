package com.stardevmc.shop.cmds;

import com.firestar311.lib.util.Utils;
import org.bukkit.command.*;

public class ShopItemCommand implements CommandExecutor {
    
    public ShopItemCommand() {
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(Utils.color("&cThis command is not yet implemented."));
        sender.sendMessage(Utils.color("&cPlease use &d/shopadmin &ccreateitem command for now."));
        return true;
    }
}