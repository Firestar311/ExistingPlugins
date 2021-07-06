package com.kingrealms.realms.cmd;

import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.chat.groups.Group;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProfileCommand extends BaseCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
    
        RealmProfile profile = RealmsAPI.getProfile(sender);
        
        if (cmd.getName().equalsIgnoreCase("setcolor")) {
            Group group = profile.getGroup();
            if (!group.getName().equalsIgnoreCase("owner")) {
                profile.sendMessage("&cYou are not allowed to use that command.");
                return true;
            }
    
            if (!(args.length > 0)) {
                sender.sendMessage(Utils.color("&cYou must provide a value"));
                return true;
            }
            
            String rawColor = args[0];
            if (!rawColor.startsWith("#")) {
                rawColor = "#" + rawColor;
            }
    
            try {
                ChatColor.of(rawColor);
            } catch (Exception e) {
                profile.sendMessage("&cYou provided an invalid hex color value.");
                return true;
            }
            
            profile.setCustomColor(rawColor);
            profile.sendMessage("&gYou set your custom color to " + ChatColor.of(rawColor) + rawColor);
        }
        
        return true;
    }
}