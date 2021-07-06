package com.kingrealms.realms.cmd;

import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RepairCommands extends BaseCommand {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
    
        RealmProfile profile = plugin.getProfileManager().getProfile(sender);
        
        if (cmd.getName().equalsIgnoreCase("repair")) {
            profile.sendMessage("&cThis system needs to be redesigned. Will be coming soon.");
        } else if (cmd.getName().equalsIgnoreCase("repairall")) {
            profile.sendMessage("&cThis system needs to be redesigned. Will be coming soon.");
        }
        return true;
    }
}