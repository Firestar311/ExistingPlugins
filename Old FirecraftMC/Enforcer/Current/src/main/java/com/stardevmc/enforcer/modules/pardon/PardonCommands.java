package com.stardevmc.enforcer.modules.pardon;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.manager.PunishmentManager;
import com.stardevmc.enforcer.objects.Flag;
import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.enums.Visibility;
import com.stardevmc.enforcer.objects.punishment.Punishment;
import com.stardevmc.enforcer.util.*;
import com.starmediadev.lib.user.User;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.*;

import java.util.*;

public class PardonCommands implements CommandExecutor {
    
    private Enforcer plugin;
    
    public PardonCommands(Enforcer plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Actor actor = plugin.getActorModule().getManager().getActor(sender);
        if (actor == null) {
            sender.sendMessage(Utils.color(Messages.ONLY_PLAYERS_AND_CONSOLE_CMD));
            return true;
        }
        
        User info = plugin.getPlayerManager().getUser(args[0]);
        if (info == null) {
            sender.sendMessage(Utils.color(Messages.COULD_NOT_FIND_PLAYER));
            return true;
        }
        
        Visibility visibility = Visibility.STAFF_ONLY;
        EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);
    
        for (String arg : args) {
            Flag flag = Flag.matchFlag(arg);
            if (flag != null) {
                flags.add(flag);
            }
        }
        
        String reason = EnforcerUtils.getReason(1, args);
        boolean ignoreReason = false;
        for (Flag flag : flags) {
            if (flag == Flag.PUBLIC) {
                visibility = Visibility.PUBLIC;
            }
            if (flag == Flag.SILENT) {
                visibility = Visibility.SILENT;
            }
            if (flag == Flag.NORMAL) {
                visibility = Visibility.STAFF_ONLY;
            }
            if (flag == Flag.IGNORE_REASON) {
                if (sender.hasPermission(Perms.FLAG_IGNORE_REASON)) {
                    ignoreReason = true;
                }
            }
        }
        
        if (!ignoreReason && StringUtils.isEmpty(reason)) {
            sender.sendMessage(Utils.color(Messages.NO_REASON));
            return true;
        }
        
        Set<Punishment> IPunishments = new HashSet<>();
        PunishmentManager punishmentManager = plugin.getPunishmentModule().getManager();
        if (cmd.getName().equalsIgnoreCase("unban")) {
            if (!sender.hasPermission(Perms.UNBAN)) {
                sender.sendMessage(Messages.noPermissionCommand(Perms.UNBAN));
                return true;
            }
            IPunishments.addAll(punishmentManager.getActiveBans(info.getUniqueId()));
            if (IPunishments.isEmpty()) {
                sender.sendMessage(Utils.color(Messages.noActivePunishment("bans")));
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("unmute")) {
            if (!sender.hasPermission(Perms.UNMUTE)) {
                sender.sendMessage(Messages.noPermissionCommand(Perms.UNMUTE));
                return true;
            }
            IPunishments.addAll(punishmentManager.getActiveMutes(info.getUniqueId()));
            if (IPunishments.isEmpty()) {
                sender.sendMessage(Utils.color(Messages.noActivePunishment("mutes")));
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("unjail")) {
            if (!sender.hasPermission(Perms.UNJAIL)) {
                sender.sendMessage(Messages.noPermissionCommand(Perms.UNJAIL));
                return true;
            }
            IPunishments.addAll(punishmentManager.getActiveJails(info.getUniqueId()));
            if (IPunishments.isEmpty()) {
                sender.sendMessage(Utils.color(Messages.noActivePunishment("jails")));
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("pardon")) {
            if (!sender.hasPermission(Perms.PARDON)) {
                sender.sendMessage(Messages.noPermissionCommand(Perms.PARDON));
                return true;
            }
            
            IPunishments.addAll(punishmentManager.getActivePunishments(info.getUniqueId()));
            if (IPunishments.isEmpty()) {
                sender.sendMessage(Utils.color(Messages.noActivePunishment("punishments")));
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("unblacklist")) {
            if (!sender.hasPermission(Perms.UNBLACKLIST)) {
                sender.sendMessage(Messages.noPermissionCommand(Perms.UNBLACKLIST));
                return true;
            }
            
            IPunishments.addAll(punishmentManager.getActiveBlacklists(info.getUniqueId()));
            if (IPunishments.isEmpty()) {
                sender.sendMessage(Utils.color(Messages.noActivePunishment("blacklists")));
                return true;
            }
        }
        for (Punishment IPunishment : IPunishments) {
            if (plugin.getTrainingModule().getManager().isTrainingMode(IPunishment.getActor())) {
                if (!IPunishment.isTrainingPunishment()) {
                    continue;
                }
            }
            IPunishment.setPardonVisibility(visibility);
            IPunishment.reversePunishment(actor, System.currentTimeMillis(), reason);
        }
    
        return true;
    }
}