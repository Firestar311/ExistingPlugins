package com.kingrealms.realms.cmd;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.moderation.inv.InvseeGui;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class ModerationCommands extends BaseCommand {
    
    private Realms plugin = Realms.getInstance();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
    
        RealmProfile profile = plugin.getProfileManager().getProfile(sender);
        
        if (cmd.getName().equalsIgnoreCase("clearchat")) {
            if (!profile.hasPermission("realms.moderation.clearchat")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
    
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < 200; i++) {
                StringBuilder line = new StringBuilder();
                int chars = new Random().nextInt(40);
                line.append(" ".repeat(Math.max(0, chars)));
                lines.add(line.toString());
            }
            
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.hasPermission("realms.moderation.clearchat.bypass")) {
                    for (String l : lines) {
                        p.sendMessage(l);
                    }
                }
                p.sendMessage(Utils.color("&dThe chat has been cleared by " + profile.getDisplayName()));
            }
        } else if (cmd.getName().equalsIgnoreCase("socialspy")) {
            if (!profile.hasPermission("realms.moderation.socialspy")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            
            if (profile.getSocialSpy().isActive()) {
                profile.getSocialSpy().deactivate();
            } else {
                profile.getSocialSpy().activate();
            }
            
            profile.sendMessage("&iYou have set social spy to &j" + profile.getSocialSpy().isActive());
        } else if (cmd.getName().equalsIgnoreCase("invsee")) {
            if (!profile.hasPermission("realms.moderation.invsee")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            
            if (!(args.length > 0)) {
                sender.sendMessage(Utils.color("&cYou must provide a target name."));
                return true;
            }
            
            RealmProfile target = plugin.getProfileManager().getProfile(args[0]);
            if (target == null) {
                profile.sendMessage("&cThe name you provided did not match a valid player.");
                return true;
            }
            
            if (!target.isOnline()) {
                profile.sendMessage("&cThat player is not online.");
                return true;
            }
            
            new InvseeGui(profile, target).openGUI(profile.getBukkitPlayer());
        }
        
        return true;
    }
}