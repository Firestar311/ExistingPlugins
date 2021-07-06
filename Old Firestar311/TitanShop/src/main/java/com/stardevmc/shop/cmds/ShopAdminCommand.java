package com.stardevmc.shop.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.shop.ShopUtils;
import com.stardevmc.shop.TitanShop;
import com.stardevmc.shop.gui.CategoryEditGUI;
import com.stardevmc.shop.manager.ShopManager;
import com.stardevmc.shop.objects.shops.gui.GUIShop;
import com.stardevmc.shop.objects.shops.gui.GUIShopCategory;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopAdminCommand implements CommandExecutor {
    
    private TitanShop plugin;
    public ShopAdminCommand(TitanShop plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
    
        Player player = ((Player) sender);
        
        if (!player.hasPermission("titanshop.admin")) {
            player.sendMessage(Utils.color("&cYou do not have permission to use that command."));
            return true;
        }
        
        ShopManager shopManager = plugin.getShopManager();
        
        if (args.length == 0) {
            player.sendMessage(Utils.color("&aShopAdmin Subcommands"));
            player.sendMessage(Utils.color("&6    - /" + label + " createshop|cs <name>"));
            player.sendMessage(Utils.color("&6        &7&o- You must also have an item in your hand for the icon."));
            player.sendMessage(Utils.color("&6    - /" + label + " createcategory|cc <name> <parent>"));
            player.sendMessage(Utils.color("&6        &7&o- You must also have an item in your hand for the icon."));
            player.sendMessage(Utils.color("&6    - /" + label + " editcategory|ec <shop> <category>"));
            player.sendMessage(Utils.color("&6    - /" + label + " createitem|ci <name> <buyprice> <sellprice>"));
            player.sendMessage(Utils.color("&6        &7&o- You must also have an item in your hand."));
            player.sendMessage(Utils.color("&6    - /" + label + " viewitems|vi"));
            return true;
        }
        
        if (Utils.checkCmdAliases(args, 0, "createshop", "cs")) {
            if (args.length != 2) {
                player.sendMessage(Utils.color("&cUsage: /" + label + " " + args[0] + " <name>"));
                player.sendMessage(Utils.color("    &7&o- You must also have an item in your hand for the icon."));
                return true;
            }
            
            String name = args[1];
            ItemStack icon = player.getInventory().getItemInMainHand();
            if (icon == null) {
                player.sendMessage(Utils.color("&cYou must be holding an item for the GUIShop Icon!"));
                return true;
            }
            
            if (shopManager.guiShopExists(name)) {
                player.sendMessage(Utils.color("&cA GUIShop with that name already exists."));
                return true;
            }
            
            GUIShop GUIShop = new GUIShop(name, icon);
            shopManager.addGUIShop(GUIShop);
            player.sendMessage(Utils.color("&aYou created a GUIShop with the name &b" + GUIShop.getName()));
        } else if (Utils.checkCmdAliases(args, 0, "createcategory", "cc")) {
            if (args.length != 3) {
                player.sendMessage(Utils.color("&cUsage: /" + label + " " + args[0] + " <name> <parent>"));
                player.sendMessage(Utils.color("    &7&o- You must also have an item in your hand for the icon."));
                return true;
            }
            
            String name = args[1];
            GUIShop parent = shopManager.getGUIShop(args[2]);
            if (parent == null) {
                player.sendMessage(Utils.color("&cThe parent shop does not exist."));
                return true;
            }
            
            ItemStack icon = player.getInventory().getItemInMainHand();
            if (icon == null) {
                player.sendMessage(Utils.color("&cYou must be holding an item for the Category Icon"));
                return true;
            }
            
            GUIShopCategory GUIShopCategory = new GUIShopCategory(parent, name, icon);
            parent.addCategory(GUIShopCategory);
            player.sendMessage(Utils.color("&aYou created the category &b" + name + " &awith the parent shop &b" + parent.getName()));
        } else if (Utils.checkCmdAliases(args, 0, "editcategory", "ec")) {
            if (args.length != 3) {
                player.sendMessage(Utils.color("&cUsage: /" + label + " " + args[0] + " <GUIShop> <category>"));
                return true;
            }
            
            GUIShop guiShop = shopManager.getGUIShop(args[1]);
            if (guiShop == null) {
                player.sendMessage(Utils.color("&cA GUIShop with that name does not exist."));
                return true;
            }
            
            GUIShopCategory category = guiShop.getCategory(args[2]);
            if (category == null) {
                player.sendMessage(Utils.color("&cA category with that name does not exist in GUIShop " + guiShop.getName()));
                return true;
            }
            
            CategoryEditGUI categoryEditGUI = new CategoryEditGUI(category);
            
            categoryEditGUI.openGUI(player);
        } else if (Utils.checkCmdAliases(args, 0, "createitem", "ci")) {
            if (args.length != 4) {
                player.sendMessage(Utils.color("&cUsage: /" + label + " " + args[0] + " <name> <buy> <sell>"));
                player.sendMessage(Utils.color("    &7&o- You must also have an item in your hand."));
                return true;
            }
            
            String name = args[1];
            double buy, sell;
            try {
                buy = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(Utils.color("&cThe value for the buy price is not a valid number."));
                return true;
            }
    
            try {
                sell = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                player.sendMessage(Utils.color("&cThe value for the sell price is not a valid number."));
                return true;
            }
    
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null) {
                player.sendMessage(Utils.color("&cYou must be holding an item."));
                return true;
            }
    
            try {
                item = ShopUtils.generatePreShopItem(plugin.getPlayerManager().getUser(player.getUniqueId()), item, name, buy, sell, null);
            } catch (Exception e) {
                player.sendMessage(Utils.color("&cThere was an error saving the information for the shop to the item. Please contact your server administrators."));
                return true;
            }
    
            player.getInventory().setItemInMainHand(item);
            player.sendMessage(Utils.color("&aSuccessfully saved the information to the item. \n&7    - This information can only be used within the category edit gui. "));
        } else if (Utils.checkCmdAliases(args, 0 , "viewitems", "vi")) {
            plugin.getItemManager().getGUI().openGUI(player);
        }
        
        return true;
    }
}
