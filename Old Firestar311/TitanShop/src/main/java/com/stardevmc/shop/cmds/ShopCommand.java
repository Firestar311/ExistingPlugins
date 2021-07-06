package com.stardevmc.shop.cmds;

import com.firestar311.lib.gui.PaginatedGUI;
import com.firestar311.lib.util.Utils;
import com.stardevmc.shop.TitanShop;
import com.stardevmc.shop.objects.shops.gui.GUIShop;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {
    
    private TitanShop plugin;
    
    public ShopCommand(TitanShop plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
        
        Player player = ((Player) sender);
        
        if (plugin.getShopManager().getGUIShops().isEmpty()) {
            player.sendMessage(Utils.color("&cThere are no shops created yet. Please contact your server administrators"));
            return true;
        }
    
        GUIShop GUIShop;
        
        if (args.length == 0) {
            GUIShop = plugin.getShopManager().getGUIShops().get(0);
        } else {
            GUIShop = plugin.getShopManager().getGUIShop(args[0]);
        }
        
        if (GUIShop == null) {
            player.sendMessage(Utils.color("&cThere was an issue with getting the GUIShop you requested. Please contact your server administrators"));
            return true;
        }
    
        PaginatedGUI gui = GUIShop.getGUI();
        if (gui == null) {
            player.sendMessage(Utils.color("&cThere was an issue generating the GUI for the GUIShop, Please contact your server administrators"));
            return true;
        }
        
        gui.openGUI(player);
    
        return true;
    }
}