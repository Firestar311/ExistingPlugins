package com.stardevmc.shop.cmds;

import com.firestar311.lib.util.Result;
import com.firestar311.lib.util.Utils;
import com.stardevmc.shop.TitanShop;
import com.stardevmc.shop.worth.StackWorth;
import com.stardevmc.shop.worth.WorthIngredient;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WorthCommand implements CommandExecutor {
    
    private boolean enabled = false;
    
    private TitanShop plugin;
    public WorthCommand(TitanShop plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!enabled) {
            sender.sendMessage(Utils.color("This command is currently disabled as the feature is not yet implemented"));
            return true;
        }
        
        if (!Utils.isPlayer(sender)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
    
        Player player = ((Player) sender);
        
        if (!player.hasPermission("titanshop.worth.manage")) {
            player.sendMessage(Utils.color("&cYou do not have permission to use that command."));
            return true;
        }
        
        if (args.length == 0) {
            return true;
        }
        
        if (Utils.checkCmdAliases(args, 0, "setworth", "sw")) {
            //worth setworth <material|hand> <amount>
            
            if (args.length != 3) {
                player.sendMessage(Utils.color("&cNot enough arguments."));
                return true;
            }
    
            Material material;
            if (args[1].equalsIgnoreCase("hand")) {
                ItemStack handItem = player.getInventory().getItemInMainHand();
                if (handItem != null) {
                    material = handItem.getType();
                } else {
                    player.sendMessage(Utils.color("&cYou must be holding an item."));
                    return true;
                }
            } else {
                try {
                    material = Material.valueOf(args[1].toUpperCase());
                } catch (Exception e) {
                    player.sendMessage(Utils.color("&cYou provided an invalid material name."));
                    return true;
                }
            }
            
            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(Utils.color("&cYou provided an invalid number for the worth amount."));
                return true;
            }
            
            plugin.getWorthManager().setWorth(material, amount);
            player.sendMessage(Utils.color("&aSet the worth of the material &b" + material.name() + " &ato &b$" + amount));
        } else if (Utils.checkCmdAliases(args, 0, "calculateworth", "cw")) {
            ItemStack handItem = player.getInventory().getItemInMainHand();
            if (handItem == null) {
                player.sendMessage(Utils.color("&cYou must be holding an item."));
                return true;
            }
    
            Result<StackWorth, String> result = plugin.getWorthManager().calculateWorth(handItem);
            if (result.getSuccess() == null) {
                player.sendMessage(Utils.color("&cThere was a problem getting the worth of that item: " + result.getFail()));
                return true;
            }
            
            StackWorth worth = result.getSuccess();
            
            player.sendMessage(Utils.color("&aTotal worth for &b" + handItem.getType().name().toLowerCase() + " &ais &b$" + worth.getTotalWorth()));
            for (WorthIngredient ingredient : worth.getIngredients()) {
                player.sendMessage(Utils.color("&8 - &d" + ingredient.getAmount() + " &7of &a" + ingredient.getMaterial().name() + " &7at &e$" + ingredient.getPrice() + " &7each."));
            }
        }
    
        return true;
    }
}