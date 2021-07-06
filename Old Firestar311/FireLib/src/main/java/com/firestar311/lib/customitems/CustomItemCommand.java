package com.firestar311.lib.customitems;

import com.firestar311.lib.FireLib;
import com.firestar311.lib.customitems.api.ICustomItem;
import com.firestar311.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

final class CustomItemCommand implements CommandExecutor {
    
    private FireLib plugin;
    
    CustomItemCommand(FireLib plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
        
        Player player = ((Player) sender);
        
        if (!player.hasPermission("customitems.command.main")) {
            player.sendMessage(Utils.color("&cYou do not have permission to use that command."));
            return true;
        }
        
        if (!(args.length > 0)) {
            player.sendMessage(Utils.color("&cYou do not have enough arguments."));
            return true;
        }
        
        if (args[0].equalsIgnoreCase("gui")) {
            if (!player.hasPermission("customitems.command.gui")) {
                player.sendMessage(Utils.color("&cYou do not have permission to use the gui."));
                return true;
            }
            
            plugin.getItemManager().openGUI(player);
            player.sendMessage(Utils.color("&aYou opened the custom items gui."));
        } else {
            String itemName = StringUtils.join(args, '_', 0, args.length);
            ICustomItem item = plugin.getItemManager().getCustomItem(itemName);
            if (item == null) {
                player.sendMessage(Utils.color("&cYou provided an invalid item name."));
                return true;
            }
            
            if (!player.hasPermission(item.getPermission())) {
                player.sendMessage(Utils.color("&cYou do not have permission to summon that item."));
                return true;
            }
            
            player.getInventory().addItem(item.getItemStack());
            player.sendMessage(Utils.color("&aYou have been given the custom item &b" + item.getName()));
        }
        return true;
    }
}