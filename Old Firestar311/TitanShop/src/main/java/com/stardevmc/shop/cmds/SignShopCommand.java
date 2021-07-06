package com.stardevmc.shop.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.shop.TitanShop;
import org.bukkit.command.*;

public class SignShopCommand implements CommandExecutor {
    
    private TitanShop plugin;
    public SignShopCommand(TitanShop plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(Utils.color("&cThis command is disabled because the feature is not yet complete."));
        return true;
    }
}