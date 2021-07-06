package com.stardevmc.enforcer.modules.history;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.manager.*;
import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.history.PlayerHistory;
import com.stardevmc.enforcer.objects.history.StaffHistory;
import com.stardevmc.enforcer.objects.target.Target;
import com.stardevmc.enforcer.util.Messages;
import com.stardevmc.enforcer.util.Perms;
import com.starmediadev.lib.user.User;
import com.starmediadev.lib.util.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class HistoryCommands implements CommandExecutor {
    
    private Enforcer plugin;
    
    private Map<UUID, Target> targets = new HashMap<>();
    private Map<UUID, Actor> actors = new HashMap<>();
    
    public HistoryCommands(Enforcer plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color(Messages.ONLY_PLAYERS_CMD));
            return true;
        }
        
        Player player = ((Player) sender);
        
        if (!(args.length > 0)) {
            player.sendMessage(Utils.color(Messages.NO_NAME_PROVIDED));
            return true;
        }
    
        HistoryManager historyManager = plugin.getHistoryModule().getManager();
        TargetManager targetManager = plugin.getTargetModule().getManager();
        ActorManager actorManager = plugin.getActorModule().getManager();
        
        if (cmd.getName().equalsIgnoreCase("history")) {
            if (!player.hasPermission(Perms.PLAYER_HISTORY)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.PLAYER_HISTORY));
                return true;
            }
    
            
            if (args.length == 1) {
                Target target = targetManager.getTarget(args[0]);
                this.targets.put(player.getUniqueId(), target);
                PlayerHistory playerHistory = historyManager.getPlayerHistory(target);
                playerHistory.getPaginator().display(sender, 1, "history");
            } else if (args.length == 2) {
                if (Utils.checkCmdAliases(args, 0, "page", "p")) {
                    Target target = this.targets.get(player.getUniqueId());
                    if (target == null) {
                        player.sendMessage(Utils.color("&cPlease use the command /history <target> to select a target."));
                        return true;
                    }
                    int page = getPage(player, args[1]);
                    if (page == -1) return true;
                    PlayerHistory playerHistory = historyManager.getPlayerHistory(target);
                    playerHistory.getPaginator().display(sender, page, "history");
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("staffhistory")) {
            if (!player.hasPermission(Perms.STAFF_HISTORY)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.STAFF_HISTORY));
                return true;
            }
            if (args.length == 1) {
                Actor actor = actorManager.getActor(args[0]);
                this.actors.put(player.getUniqueId(), actor);
                StaffHistory staffHistory = historyManager.getStaffHistory(actor);
                staffHistory.getPaginator().display(sender, 1, "staffhistory");
            } else if (args.length == 2) {
                if (Utils.checkCmdAliases(args, 0, "page", "p")) {
                    Actor actor = this.actors.get(player.getUniqueId());
                    if (actor == null) {
                        player.sendMessage(Utils.color("&cPlease use the command /staffhistory <actor> to select an actor."));
                        return true;
                    }
                    int page = getPage(player, args[1]);
                    if (page == -1) return true;
                    StaffHistory staffHistory = historyManager.getStaffHistory(actor);
                    staffHistory.getPaginator().display(sender, page, "staffhistory");
                }
            }
        }
        
        return true;
    }
    
    private int getPage(Player player, String stringPage) {
        try {
            return Integer.parseInt(stringPage);
        } catch (NumberFormatException e) {
            player.sendMessage(Utils.color(Messages.INVALID_NUMBER));
            return -1;
        }
    }
    
    private User getPlayerInfo(String string, Player player) {
        User info = plugin.getPlayerManager().getUser(string);
        if (info == null) {
            player.sendMessage(Utils.color(Messages.PLAYER_NEVER_JOINED));
            return null;
        }
        return info;
    }
}
