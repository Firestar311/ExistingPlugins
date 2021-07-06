package com.stardevmc.enforcer.modules.prison;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.manager.PrisonManager;
import com.stardevmc.enforcer.objects.Variables;
import com.stardevmc.enforcer.objects.prison.Inmate;
import com.stardevmc.enforcer.objects.prison.Prison;
import com.stardevmc.enforcer.objects.punishment.JailPunishment;
import com.stardevmc.enforcer.objects.punishment.Punishment;
import com.stardevmc.enforcer.util.Messages;
import com.stardevmc.enforcer.util.Perms;
import com.starmediadev.lib.pagination.Paginator;
import com.starmediadev.lib.pagination.PaginatorFactory;
import com.starmediadev.lib.region.Selection;
import com.starmediadev.lib.region.SelectionManager;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

public class PrisonCommand implements CommandExecutor, Listener {
    
    private Enforcer plugin;
    
    public PrisonCommand(Enforcer plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color(Messages.ONLY_PLAYERS_CMD));
            return true;
        }
        
        Player player = ((Player) sender);
        SelectionManager selectionManager = plugin.getServer().getServicesManager().getRegistration(SelectionManager.class).getProvider();
    
        if (!(args.length > 0)) {
            player.sendMessage(Utils.color(Messages.NOT_ENOUGH_ARGS));
            return true;
        }
        
        if (!player.hasPermission(Perms.PRISON_MAIN)) {
            player.sendMessage(Messages.noPermissionCommand(Perms.PRISON_MAIN));
            return true;
        }
    
        PrisonManager prisonManager = plugin.getPrisonModule().getManager();
        
        if (Utils.checkCmdAliases(args, 0, "create", "c")) {
            if (!player.hasPermission(Perms.PRISON_ADD)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.PRISON_ADD));
                return true;
            }
            
            if (!(args.length > 1)) {
                player.sendMessage(Utils.color("&cUsage: /prison create <maxplayers> [id]"));
                return true;
            }
            
            int maxPlayers = 5;
            try {
                maxPlayers = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(Utils.color(Messages.INVALID_PRISON_AMOUNT));
            }
            
            int id = -1;
            if (args.length > 2) {
                try {
                    id = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(Utils.color(Messages.INVALID_PRISON_ID));
                }
            }
            
            Location location = player.getLocation();
            
            Prison prison;
            if (!selectionManager.hasSelection(player)) {
                prison = new Prison(id, location, maxPlayers);
            } else {
                Selection selection = selectionManager.getSelection(player);
                if (selection.getPointA() != null && selection.getPointB() != null) {
                    prison = new Prison(id, location, maxPlayers, selection.getPointA(), selection.getPointB());
                } else {
                    prison = new Prison(id, location, maxPlayers);
                }
            }
            prisonManager.addPrison(prison);
            String message = Messages.PRISON_CREATE;
            message = message.replace(Variables.JAIL_ID, prison.getDisplayName());
            sendOutputMessage(player, message);
            
            Set<Prison> prisons = prisonManager.getPrisonsWithOverflow();
            Set<Inmate> playersToAdd = new HashSet<>();
            
            for (Prison pr : prisons) {
                List<Inmate> inmates = new LinkedList<>(pr.getInmates());
                calculateOverflow(prison, playersToAdd, pr, inmates);
            }
            
            for (Inmate uuid : playersToAdd) {
                Player inhabitant = changePunishmentInfo(prison, uuid);
                if (inhabitant != null) {
                    inhabitant.teleport(prison.getLocation());
                    inhabitant.sendMessage(Utils.color(Messages.CREATE_PRISON_OVERFLOW));
                }
            }
            return true;
        }
        if (Utils.checkCmdAliases(args, 0, "pos1")) {
            if (!player.hasPermission(Perms.PRISON_SELECTION)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.PRISON_SELECTION));
                return true;
            }
            
            selectionManager.setPointA(player, player.getLocation());
        } else if (Utils.checkCmdAliases(args, 0, "pos2")) {
            if (!player.hasPermission(Perms.PRISON_SELECTION)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.PRISON_SELECTION));
                return true;
            }
            
            selectionManager.setPointB(player, player.getLocation());
        } else if (Utils.checkCmdAliases(args, 0, "list")) {
            if (!player.hasPermission(Perms.PRISON_LIST)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.PRISON_LIST));
                return true;
            }
            
            if (prisonManager.getPrisons().isEmpty()) {
                player.sendMessage(Utils.color("&cThere are no prisons created."));
                return true;
            }
            
            PaginatorFactory<Prison> factory = new PaginatorFactory<>();
            factory.setMaxElements(7).setHeader("&7-=List of Prisons=- &e({pagenumber}/{totalpages})").setFooter("&7Type /prison list {nextpage} for more");
            for (Prison prison : prisonManager.getPrisons()) {
                factory.addElement(prison);
            }
            Paginator<Prison> paginator = factory.build();
            if (args.length == 1) {
                paginator.display(player, 1);
            } else {
                int page;
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(Utils.color(Messages.INVALID_NUMBER));
                    return true;
                }
                paginator.display(player, page);
            }
            return true;
        } else if (Utils.checkCmdAliases(args, 0, "clearselection", "cs")) {
            if (!player.hasPermission(Perms.PRISON_CLEAR_SELECTION)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.PRISON_CLEAR_SELECTION));
                return true;
            }
            if (!selectionManager.hasSelection(player)) {
                player.sendMessage(Utils.color(Messages.NO_SELECTION));
                return true;
            }
    
            selectionManager.clearSelection(player);
            player.sendMessage(Utils.color(Messages.CLEAR_SELECTION));
            return true;
        }
    
        Prison prison = prisonManager.getPrisonFromString(player, args[0]);
        if (prison == null) return true;
        
        if (Utils.checkCmdAliases(args, 1, "setlocation", "sl")) {
            if (!player.hasPermission(Perms.SET_PRISON_LOCATION)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.SET_PRISON_LOCATION));
                return true;
            }
            
            Location newLocation = player.getLocation();
            if (!prison.contains(newLocation)) {
                player.sendMessage(Utils.color(Messages.LOCATION_NOT_IN_PRISON));
                return true;
            }
            prison.setLocation(newLocation);
            
            String message = Messages.PRISON_SET_SPAWN;
            message = message.replace(Variables.JAIL_ID, prison.getDisplayName());
            sendOutputMessage(player, message);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (plugin.getPunishmentModule().getManager().isJailed(p.getUniqueId())) {
                    if (prison.isInmate(p.getUniqueId())) {
                        p.teleport(newLocation);
                        p.sendMessage(Utils.color(Messages.prisonLocationChanged(player.getName())));
                    }
                }
            }
        } else if (Utils.checkCmdAliases(args, 1, "setmaxplayers", "smp")) {
            if (!player.hasPermission(Perms.PRISON_SET_MAX_PLAYERS)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.PRISON_SET_MAX_PLAYERS));
                return true;
            }
            
            if (args.length != 3) {
                player.sendMessage(Utils.color("&cUsage: /prison <id|name> setmaxplayers|smp <amount>"));
                return true;
            }
            
            int amount = 5;
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(Utils.color(Messages.INVALID_PRISON_AMOUNT));
            }
            
            if (amount == prison.getMaxPlayers()) {
                player.sendMessage(Utils.color(Messages.MAX_AMOUNT_SAME));
                return true;
            }
            
            if (amount > prison.getMaxPlayers()) {
                Set<Prison> prisons = prisonManager.getPrisonsWithOverflow();
                Set<Inmate> playersToAdd = new HashSet<>();
                for (Prison pr : prisons) {
                    List<Inmate> inhabitants = new LinkedList<>(pr.getInmates());
                    if (inhabitants.isEmpty()) continue;
                    calculateOverflow(prison, playersToAdd, pr, inhabitants);
                }
                
                for (Inmate uuid : playersToAdd) {
                    Player inhabitant = changePunishmentInfo(prison, uuid);
                    if (inhabitant != null) {
                        inhabitant.teleport(prison.getLocation());
                        inhabitant.sendMessage(Utils.color(Messages.CREATE_PRISON_OVERFLOW));
                    }
                }
                prison.setMaxPlayers(amount);
            } else if (amount < prison.getMaxPlayers()) {
                Set<Inmate> playersToRemove = new HashSet<>();
                List<Inmate> inhabitants = new LinkedList<>(prison.getInmates());
                if (!inhabitants.isEmpty()) {
                    int removalAmount = prison.getMaxPlayers() - amount;
                    for (int i = 0; i < removalAmount; i++) {
                        int index = inhabitants.size() - 1 - i;
                        playersToRemove.add(inhabitants.get(index));
                    }
                }
                prison.setMaxPlayers(amount);
                for (Inmate removed : playersToRemove) {
                    Prison newPrison = prisonManager.findPrison();
                    if (newPrison == null) continue;
                    prison.removeInmate(removed);
                    for (Punishment IPunishment : plugin.getPunishmentModule().getManager().getActiveJails(removed.getUuid())) {
                        ((JailPunishment) IPunishment).setPrisonId(newPrison.getId());
                    }
                    Player inhabitant = Bukkit.getPlayer(removed.getUuid());
                    if (inhabitant != null) {
                        inhabitant.teleport(newPrison.getLocation());
                        inhabitant.sendMessage(Utils.color(Messages.MAX_AMOUNT_CHANGED_MOVED));
                    }
                }
            }
            String message = Messages.PRISON_SET_MAX_PLAYERS;
            message = message.replace(Variables.JAIL_ID, prison.getDisplayName());
            message = message.replace(Variables.MAX_PLAYERS, amount + "");
            sendOutputMessage(player, message);
        } else if (Utils.checkCmdAliases(args, 1, "remove", "r")) {
            if (!player.hasPermission(Perms.PRISON_REMOVE)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.PRISON_REMOVE));
                return true;
            }
            
            if (args.length != 2) {
                player.sendMessage(Utils.color("&cUsage: /prison <id|name> remove|r"));
                return true;
            }
            
            Set<Inmate> inhabitants = new HashSet<>(prison.getInmates());
            prisonManager.removePrison(prison.getId());
            for (Inmate inhabitant : inhabitants) {
                Prison newPrison = prisonManager.findPrison();
                Player jailedUser = changePunishmentInfo(newPrison, inhabitant);
                if (jailedUser != null) {
                    jailedUser.teleport(newPrison.getLocation());
                    jailedUser.sendMessage(Utils.color(Messages.MOVE_PRISON_REMOVED));
                }
            }
            String message = Messages.PRISON_REMOVE;
            message = message.replace(Variables.JAIL_ID, prison.getId() + "");
            sendOutputMessage(player, message);
        } else if (Utils.checkCmdAliases(args, 1, "teleport", "tp")) {
            if (!player.hasPermission(Perms.PRISON_TELEPORT)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.PRISON_TELEPORT));
                return true;
            }
            player.teleport(prison.getLocation());
            player.sendMessage(Utils.color("&aYou were teleported to the spawn location of the prison &b" + prison.getDisplayName()));
        } else if (Utils.checkCmdAliases(args, 1, "setname", "sn")) {
            if (!player.hasPermission(Perms.PRISON_SET_NAME)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.PRISON_SET_NAME));
                return true;
            }
            
            if (args.length != 3) {
                player.sendMessage(Utils.color("&cUsage: /prison <id|name> setname|sn <newname>"));
                return true;
            }
            
            for (Prison pr : prisonManager.getPrisons()) {
                if (pr.getId() != prison.getId()) {
                    if (pr.getName() != null) {
                        if (pr.getName().equalsIgnoreCase(args[2])) {
                            player.sendMessage(Utils.color("&cA prison with that name already exists. Please choose another name."));
                            return true;
                        }
                    }
                }
            }
            
            prison.setName(args[2]);
            
            String message = Messages.PRISON_SET_NAME;
            message = message.replace(Variables.DISPLAY, prison.getName());
            message = message.replace(Variables.JAIL_ID, prison.getId() + "");
            sendOutputMessage(player, message);
        } else if (Utils.checkCmdAliases(args, 1, "redefine")) {
            if (!player.hasPermission(Perms.PRISON_REDEFINE)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.PRISON_REDEFINE));
                return true;
            }
            if (!selectionManager.hasSelection(player)) {
                player.sendMessage(Utils.color("&cYou do not have a selection to redefine the region"));
                return true;
            }
            
            Selection selection = selectionManager.getSelection(player);
            
            prison.setBounds(selection.getPointA(), selection.getPointB());
            player.sendMessage(Utils.color("&aSet the bounds of the prison &b" + prison.getDisplayName() + " &ato the current selection."));
            if (!prison.contains(prison.getLocation())) {
                player.sendMessage(Utils.color("&cThe spawn location of the prison is not in the new prison area."));
            }
            
            String message = Messages.PRISON_REDEFINE;
            message = message.replace(Variables.DISPLAY, prison.getName());
            sendOutputMessage(player, message);
        }
        
        return true;
    }
    
    private Player changePunishmentInfo(Prison prison, Inmate inmate) {
        prison.addInmate(inmate);
        for (Punishment IPunishment : plugin.getPunishmentModule().getManager().getActiveJails(inmate.getUuid())) {
            ((JailPunishment) IPunishment).setPrisonId(prison.getId());
        }
        return Bukkit.getPlayer(inmate.getUuid());
    }
    
    private void calculateOverflow(Prison prison, Set<Inmate> playersToAdd, Prison pr, List<Inmate> inhabitants) {
        int amountOver = inhabitants.size() - pr.getMaxPlayers();
        for (int i = 0; i < amountOver; i++) {
            if (!(playersToAdd.size() >= prison.getMaxPlayers())) {
                int index = inhabitants.size() - 1 - i;
                playersToAdd.add(inhabitants.get(index));
                pr.removeInmate(inhabitants.get(index));
            }
        }
    }
    
    private void sendOutputMessage(Player player, String message) {
        Messages.sendOutputMessage(player, message, plugin);
    }
}