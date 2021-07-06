package com.kingrealms.realms.cmd;

import com.kingrealms.realms.api.events.KitClaimEvent;
import com.kingrealms.realms.kits.*;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitCommands extends BaseCommand {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
        
        RealmProfile profile = plugin.getProfileManager().getProfile(sender);
        
        if (cmd.getName().equalsIgnoreCase("createkit")) {
            if (!(args.length > 0)) {
                sender.sendMessage(Utils.color("&cYou must provide a name for the kit."));
                return true;
            }
            
            if (!sender.hasPermission("realms.kits.create")) {
                profile.sendMessage("&cYou do not have permission to create kits.");
                return true;
            }
            
            String name = args[0];
            if (plugin.getKitManager().getKit(name) != null) {
                profile.sendMessage("&cA kit by that name already exists.");
                return true;
            }
            
            Kit kit = new Kit(name);
            KitTier tier = new KitTier(0);
            Inventory inventory = profile.getInventory();
            for (int i = 0; i < inventory.getContents().length; i++) {
                ItemStack itemStack = profile.getInventory().getItem(i);
                if (itemStack != null) {
                    tier.addItem(i, itemStack.clone());
                }
            }
            kit.addTier(tier);
            plugin.getKitManager().addKit(kit);
            profile.sendMessage("&iCreated a kit named &j" + kit.getName());
        } else if (cmd.getName().equalsIgnoreCase("deletekit")) {
            if (!(args.length > 0)) {
                sender.sendMessage(Utils.color("&cYou must provide a kit name."));
                return true;
            }
            
            if (!sender.hasPermission("realms.kits.delete")) {
                profile.sendMessage("&cYou do not have permission to delete kits.");
                return true;
            }
            
            String name = args[0];
            Kit kit = plugin.getKitManager().getKit(name);
            if (kit == null) {
                profile.sendMessage("&cThere is not a kit by that name.");
                return true;
            }
            
            plugin.getKitManager().removeKit(kit);
            profile.sendMessage("&iRemoved the kit &j" + kit.getName());
        } else if (cmd.getName().equalsIgnoreCase("kit")) {
            if (!(args.length > 0)) {
                sender.sendMessage(Utils.color("&cYou must provide a kit name."));
                return true;
            }
            
            Kit kit = plugin.getKitManager().getKit(args[0]);
            if (kit == null) {
                profile.sendMessage("&cA kit by that name does not exist.");
                return true;
            }
            
            if (!sender.hasPermission("realms.kits.redeem." + kit.getName().toLowerCase())) {
                profile.sendMessage("&cYou do not have permission to claim that kit.");
                return true;
            }
            
            KitResponse response = kit.redeemKit(profile);
            switch (response) {
                case UNKNOWN_ERROR -> profile.sendMessage("&cThere was an unknown error while redeeming that kit.");
                case NOT_SETUP -> profile.sendMessage("&cThat kit is not properly setup, contact an Admin+");
                case COOLDOWN_ACTIVE -> profile.sendMessage("&cThat kit is still on cooldown.");
                case SUCCESS -> {
                    profile.sendMessage("&gSuccessfully redeemed the kit &h" + kit.getName());
                    plugin.getServer().getPluginManager().callEvent(new KitClaimEvent(profile, kit));
                }
                case NO_MORE_USES -> profile.sendMessage("&cYou cannot use that kit anymore.");
            }
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> results = new ArrayList<>(), possibleResults = new ArrayList<>();
        if (cmd.getName().equalsIgnoreCase("kit") || cmd.getName().equalsIgnoreCase("deletekit")) {
            if (args.length == 0) {
                for (Kit kit : plugin.getKitManager().getKits()) {
                    possibleResults.add(kit.getName());
                }
                results.addAll(Utils.getResults(args[0], possibleResults));
            }
        }
        return results;
    }
}