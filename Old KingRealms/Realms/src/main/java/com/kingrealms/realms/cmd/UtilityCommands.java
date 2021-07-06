package com.kingrealms.realms.cmd;

import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.profile.gui.MailboxGui;
import com.starmediadev.lib.user.PlaySession;
import com.starmediadev.lib.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class UtilityCommands extends BaseCommand {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        RealmProfile profile = plugin.getProfileManager().getProfile(sender);
        if (cmd.getName().equalsIgnoreCase("seen")) {
            if (!(args.length > 0)) {
                profile.sendMessage("&cYou must provide a player name.");
                return true;
            }
            
            RealmProfile target = plugin.getProfileManager().getProfile(args[0]);
            if (target == null) {
                profile.sendMessage("&cThe name you provided does not match a valid player name.");
                return true;
            }
    
            PlaySession latestSession = target.getUser().getLatestSession();
            if (latestSession == null) {
                profile.sendMessage("&cThat player has not joined the server yet.");
                return true;
            }
            
            if (target.isOnline()) {
                if (target.hasPermission("realms.staff") && !profile.hasPermission("realms.staff")) {
                    /*
                    These conditions are temporary as the staff mode will be used here instead, this is mostly for testing
                    to see if this works, then it will be commented out until I have the staff mode system in place.
                     */
                    PlaySession session = null;
                    for (PlaySession s : target.getUser().getPlaySessions()) {
                        if (session == null) {
                            session = s;
                        } else {
                            if (s.getLogoutTime() > session.getLogoutTime() && s.getLogoutTime() < latestSession.getLoginTime()) {
                                session = s;
                            }
                        }
                    }
                    
                    if (session == null) {
                        profile.sendMessage("&gThat player has not joined the server yet.");
                    } else {
                        String onlineTime = Utils.formatTime(System.currentTimeMillis() - session.getLogoutTime());
                        profile.sendMessage("&h" + target.getName() + " &ghas been online for " + onlineTime);
                    }
                } else {
                    String onlineTime = Utils.formatTime(System.currentTimeMillis() - latestSession.getLoginTime());
                    profile.sendMessage("&h" + target.getName() + " &ghas been &aonline &gfor &h" + onlineTime);
                }
            } else {
                String offlineTime = Utils.formatTime(System.currentTimeMillis() - latestSession.getLogoutTime());
                profile.sendMessage("&h" + target.getName() + " &ghas been &coffline &gfor &h" + offlineTime);
            }
        } else if (cmd.getName().equals("craft")) {
            if (!profile.hasPermission("realms.command.craft")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
    
            profile.getBukkitPlayer().openWorkbench(null, true);
            profile.sendMessage("&gHere is a workbench.");
        } else if (cmd.getName().equals("mailbox")) {
            new MailboxGui(profile).openGUI(profile.getBukkitPlayer());
        }
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return super.onTabComplete(sender, cmd, label, args);
    }
}