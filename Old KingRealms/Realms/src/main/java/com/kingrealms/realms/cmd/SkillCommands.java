package com.kingrealms.realms.cmd;

import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.skills.SkillType;
import com.kingrealms.realms.skills.base.gui.*;
import com.starmediadev.lib.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillCommands extends BaseCommand  {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
        
        if (!checkSeason(sender)) return true;
    
        RealmProfile profile = plugin.getProfileManager().getProfile(sender);
        SkillType type = null;
        if (cmd.getName().equalsIgnoreCase("slayer")) {
           type = SkillType.SLAYER;
        } else if (cmd.getName().equalsIgnoreCase("mining")) {
            type = SkillType.MINING;
        } else if (cmd.getName().equalsIgnoreCase("farming")) {
            type = SkillType.FARMING;
        } else if (cmd.getName().equalsIgnoreCase("skills")) {
            new SkillMainGui(profile).openGUI(profile.getBukkitPlayer());
        } else if (cmd.getName().equalsIgnoreCase("woodcutting")) {
            type = SkillType.WOODCUTTING;
        }
    
        if (type != null) {
            new SkillTypeGui(profile, type).openGUI(profile.getBukkitPlayer());
        }
        
        return true;
    }
}