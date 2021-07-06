package com.kingrealms.realms.cmd;

import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.profile.RealmProfile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListCommands extends BaseCommand {
    
    private List<String> managementRanks = List.of("owner", "manager"), administrationRanks = List.of("headadmin", "admin", "trialadmin"), moderationRanks = List.of("srmod", "mod", "helper"), specialRanks = List.of("builder", "vip", "beta"), donorRanks = List.of("god", "marshall", "knight", "soldier");
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        RealmProfile senderProfile = plugin.getProfileManager().getProfile(sender);
        if (cmd.getName().equalsIgnoreCase("list")) {
            int online = Bukkit.getOnlinePlayers().size(), max = Bukkit.getServer().getMaxPlayers();
            senderProfile.sendMessage("&gThere are &h" + online + " &gplayers out of &h" + max + " &gplayers online.");
            
            List<RealmProfile> management = new ArrayList<>(), administration = new ArrayList<>(), moderation = new ArrayList<>(), special = new ArrayList<>(), donor = new ArrayList<>(), regular = new ArrayList<>();
            
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                RealmProfile profile = RealmsAPI.getProfile(onlinePlayer);
                String group = profile.getPermissionGroup().toLowerCase();
                if (managementRanks.contains(group)) {
                    management.add(profile);
                } else if (administrationRanks.contains(group)) {
                    administration.add(profile);
                } else if (moderationRanks.contains(group)) {
                    moderation.add(profile);
                } else if (specialRanks.contains(group)) {
                    special.add(profile);
                } else if (donorRanks.contains(group)) {
                    donor.add(profile);
                } else {
                    regular.add(profile);
                }
            }
            
            senderProfile.sendMessage("&aOnline Players");
            if (!management.isEmpty()) {
                senderProfile.sendMessage("   &6&l&oMANAGEMENT");
                for (RealmProfile profile : management) {
                    senderProfile.sendMessage("    &8&l- &r" + profile.getDisplayName());
                }
            }
    
            if (!administration.isEmpty()) {
                senderProfile.sendMessage("   &4&l&oADMINISTRATION");
                for (RealmProfile profile : administration) {
                    senderProfile.sendMessage("    &8&l- &r" + profile.getDisplayName());
                }
            }
    
            if (!moderation.isEmpty()) {
                senderProfile.sendMessage("   &5&l&oMODERATION");
                for (RealmProfile profile : moderation) {
                    senderProfile.sendMessage("    &8&l- &r" + profile.getDisplayName());
                }
            }
    
            if (!special.isEmpty()) {
                senderProfile.sendMessage("   &e&l&oSPECIAL");
                for (RealmProfile profile : special) {
                    senderProfile.sendMessage("    &8&l- &r" + profile.getDisplayName());
                }
            }
    
            if (!donor.isEmpty()) {
                senderProfile.sendMessage("   &9&l&oDONOR");
                for (RealmProfile profile : donor) {
                    senderProfile.sendMessage("    &8&l- &r" + profile.getDisplayName());
                }
            }
    
            if (!regular.isEmpty()) {
                senderProfile.sendMessage("   &b&l&oDEFAULT");
                for (RealmProfile profile : regular) {
                    senderProfile.sendMessage("    &8&l- &r" + profile.getDisplayName());
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("stafflist")) {
            int onlineStaff = 0;
            StringBuilder sb = new StringBuilder();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("realms.staff")) {
                    RealmProfile profile = plugin.getProfileManager().getProfile(p);
                    if (profile.getStaffMode().isActive() && sender.hasPermission("realms.staffmode")) {
                        onlineStaff++;
                        sb.append(p.getName()).append(" ");
                    }
                }
            }
            
            if (onlineStaff == 0) {
                senderProfile.sendMessage("&cThere are no staff online.");
            } else {
                senderProfile.sendMessage("&gThere are &h" + onlineStaff + " &gtotal staff online.");
                senderProfile.sendMessage("&aStaff Members: &f" + sb.toString());
            }
        }
        
        return true;
    }
}