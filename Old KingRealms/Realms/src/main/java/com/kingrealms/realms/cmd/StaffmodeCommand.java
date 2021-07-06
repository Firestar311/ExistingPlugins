package com.kingrealms.realms.cmd;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.channel.channels.StaffChannel;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.staffmode.StaffMode;
import com.starmediadev.lib.pagination.*;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class StaffmodeCommand extends BaseCommand {
    
    private Realms plugin = Realms.getInstance();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(Utils.color("&cYou can only list who is in staff mode."));
            return true;
        }
    
        RealmProfile profile = plugin.getProfileManager().getProfile(sender);
        
        if (!profile.hasPermission("realms.staffmode")) {
            profile.sendMessage("&cYou are not allowed to use that command.");
            return true;
        }
    
        StaffMode staffMode = profile.getStaffMode();
        if (args.length == 0) {
            boolean active = staffMode.toggleStaffMode(profile.getBukkitPlayer());
            StaffChannel staffChannel = plugin.getChannelManager().getStaffChannel();
            staffChannel.sendMessage(profile.getName() + " has toggled staff mode to " + active);
        } else if (Utils.checkCmdAliases(args, 0, "settings", "s")) {
            if (args.length == 1) {
                profile.sendMessage("&iYour current settings for Staff Mode.");
                profile.sendMessage("&jActive: &g" + staffMode.isActive());
                profile.sendMessage("&jItem Pickup: &g" + staffMode.canPickupItems());
                profile.sendMessage("&jAuto-Join Staff Chat: &g" + staffMode.autoJoinStaffChat());
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 1, "itempickup", "itempu", "ipu")) {
                if (!profile.hasPermission("realms.staffmode.settings.itempickup")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                if (args.length == 2) {
                    staffMode.setItemPickup(!staffMode.canPickupItems());
                } else {
                    staffMode.setItemPickup(getValue(args));
                }
                profile.sendMessage("&iYou have toggled &hitem pickup &ito: &j" + staffMode.canPickupItems());
            } else if (Utils.checkCmdAliases(args, 1, "autojoinstaffchat", "autojoinstaff", "ajsc")) {
                if (!profile.hasPermission("realms.staffmode.settings.autojoinstaffchat")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                if (args.length == 2) {
                    staffMode.setAutojoinStaffChat(!staffMode.autoJoinStaffChat());
                } else {
                    staffMode.setAutojoinStaffChat(getValue(args));
                }
                profile.sendMessage("&iYou have toggled &hstaff chat auto join &ito: &j" + staffMode.autoJoinStaffChat());
            } else {
                profile.sendMessage("&cInvalid setting.");
            }
        } else if (Utils.checkCmdAliases(args, 0, "list", "l")) {
            List<String> staffModeList = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("realms.staffmode")) {
                    RealmProfile sm = plugin.getProfileManager().getProfile(player);
                    if (sm.getStaffMode().isActive()) {
                        staffModeList.add(sm.getName() + " for " + Utils.formatTime(System.currentTimeMillis() - sm.getStaffMode().getDate()));
                    }
                }
            }
            
            if (staffModeList.isEmpty()) {
                profile.sendMessage("&cNo one is in staff mode.");
                return true;
            }
            
            Paginator<StringElement> paginator = PaginatorFactory.generateStringPaginator(7, staffModeList, new HashMap<>() {{
                put(DefaultVariables.COMMAND, "staffmode list");
                put(DefaultVariables.TYPE, "those in Staff Mode");
            }});
            
            if (args.length > 1) {
                paginator.display(sender, args[1]);
            } else {
                paginator.display(sender, 1);
            }
        }
        return true;
    }
    
    private boolean getValue(String[] args) {
        if (Utils.checkCmdAliases(args, 2, "true", "t", "on")) {
            return true;
        } else if (Utils.checkCmdAliases(args, 2, "false", "f", "off")) {
            return false;
        } else {
            return false;
        }
    }
}