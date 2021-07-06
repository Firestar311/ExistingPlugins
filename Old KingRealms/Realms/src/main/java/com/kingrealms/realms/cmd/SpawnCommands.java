package com.kingrealms.realms.cmd;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommands extends BaseCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players can do that."));
            return true;
        }
        
        if (cmd.getName().equalsIgnoreCase("spawn")) {
            if (args.length == 1) {
                if (!sender.hasPermission("realms.spawn.others")) {
                    sender.sendMessage(Utils.color("&cYou do not have enough permission to do that."));
                    return true;
                }
        
                RealmProfile profile = plugin.getProfileManager().getProfile(args[0]);
                if (profile == null) {
                    sender.sendMessage(Utils.color("&cA player by that name does not exist."));
                    return true;
                }
        
                if (!profile.isOnline()) {
                    sender.sendMessage(Utils.color("&cThat player is not online."));
                    return true;
                }
        
                profile.teleport(plugin.getSpawn().getSpawnpoint());
                profile.sendMessage("&gYou were teleported to the spawnpoint by " + sender.getName());
            }
    
            ((Player) sender).teleport(Realms.getInstance().getSpawn().getSpawnpoint());
            plugin.getProfileManager().getProfile(sender).sendMessage("&gTeleported to the spawn.");
        } else if (cmd.getName().equalsIgnoreCase("setspawn")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.color("&cOnly players can use that command."));
                return true;
            }
            
            if (!sender.hasPermission("realms.admin.setspawnpoint")) {
                sender.sendMessage(Utils.color("&cYou do not have enough permission to set the spawnpoint."));
                return true;
            }
            
            RealmProfile profile = plugin.getProfileManager().getProfile(sender);
            
            Location loc = profile.getLocation();
            if (!plugin.getSpawn().contains(loc)) {
                profile.sendMessage(Utils.color("&cThe spawnpoint must be in the spawn region."));
                return true;
            }
    
            plugin.getSpawn().setSpawnpoint(loc);
            profile.sendMessage("&iSet the spawnpoint to your current location.");
        }
        
        return true;
    }
}