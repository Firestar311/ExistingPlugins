package com.kingrealms.realms.cmd;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.warps.type.*;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class WarpCommands extends BaseCommand {
    
    private final Realms plugin = Realms.getInstance();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
        
        RealmProfile player = plugin.getProfileManager().getProfile(sender);
        
        if (cmd.getName().equalsIgnoreCase("warp")) {
            if (args.length == 0) {
                List<Warp> warps = new ArrayList<>();
                for (Warp warp : plugin.getWarpManager().getWarps()) {
                    if (warp instanceof TerritoryWarp) { continue; }
                    if (warp instanceof ServerWarp) {
                        ServerWarp serverWarp = (ServerWarp) warp;
                        if (serverWarp.canAccess(player.getUniqueId())) {
                            warps.add(warp);
                        }
                    }
                }
                
                if (warps.isEmpty()) {
                    player.sendMessage("&cCould not find any warps of that type.");
                    return true;
                }
                
                StringBuilder sb = new StringBuilder();
                for (Warp warp : warps) {
                    if (warp instanceof TerritoryWarp) {
                        sb.append(((TerritoryWarp) warp).getOwner().getName()).append(":").append(warp.getName());
                    } else {
                        sb.append(warp.getName()).append(" ");
                    }
                }
                
                player.sendMessage("&gList of Server Warps");
                player.sendMessage("&hWarps: &f" + sb.toString());
                return true;
            }
            
            Warp targetWarp = null;
            String name = args[0];
            
            String[] nameSplit = name.split(":");
            if (nameSplit.length == 2) {
                targetWarp = plugin.getWarpManager().getWarp(nameSplit[0], nameSplit[1]);
            }
            
            List<Warp> warps = new ArrayList<>(plugin.getWarpManager().getWarps());
            if (targetWarp == null) {
                if (warps.isEmpty()) {
                    player.sendMessage("&cCould not find any warps.");
                    return true;
                }
                
                for (Warp warp : warps) {
                    if (warp.getName().equalsIgnoreCase(name)) {
                        targetWarp = warp;
                    }
                }
            }
            
            if (targetWarp == null) {
                player.sendMessage("&cCould not find a warp by that name.");
                return true;
            }
            
            if (!targetWarp.canAccess(player.getUniqueId())) {
                player.sendMessage("&cYou are not allowed to use that warp.");
                return true;
            }
            
            if (targetWarp instanceof TerritoryWarp) {
                player.sendMessage("&cYou must use the territory specific commands to modify that warp.");
                return true;
            }
            
            if (args.length > 1) {
                if (Utils.checkCmdAliases(args, 1, "modify")) {
                    if (!(args.length > 3)) {
                        player.sendMessage("&cUsage: /" + label + " " + args[0] + " modify <subcommand> <value>");
                        return true;
                    }
    
                    if (Utils.checkCmdAliases(args, 2, "name")) {
                        String newName = args[3];
                        for (Warp warp : warps) {
                            if (warp.getName().equalsIgnoreCase(newName)) {
                                player.sendMessage("&cThere is already a warp with that name.");
                                return true;
                            }
                        }
        
                        targetWarp.setName(newName);
                        player.sendMessage("&gYou set the name of the warp named &h" + name + "&g to &h" + newName);
                    } else if (Utils.checkCmdAliases(args, 2, "description")) {
                        String description = StringUtils.join(args, " ", 3, args.length);
                        targetWarp.setDescription(description);
                        player.sendMessage("&gSet the description of the warp &h" + name + " &gto &h" + description);
                    } else if (Utils.checkCmdAliases(args, 2, "permission")) {
                        if (!(targetWarp instanceof ServerWarp)) {
                            player.sendMessage("&cOnly warps owned by the server can have permissions.");
                            return true;
                        }
        
                        String permission = args[3];
                        targetWarp.setPermission(permission);
                        player.sendMessage("&gSet the permission of the warp &h" + name + " &gto &h" + permission);
                    }
                }
            }
            
            player.teleport(targetWarp);
            player.sendMessage("&gYou have been teleported to the warp named &h" + targetWarp.getName());
        } else if (cmd.getName().equalsIgnoreCase("setwarp") || cmd.getName().equalsIgnoreCase("setplayerwarp")) {
            if (!(args.length > 0)) {
                player.sendMessage("&cYou must provide a warp name.");
                return true;
            }
            
            String name = args[0];
            Warp warp;
            if (!player.hasPermission("realms.warps.server.create")) {
                player.sendMessage("&cYou do not have permission to create server warps.");
                return true;
            }
            
            ServerWarp existing = plugin.getWarpManager().getServerWarp(name);
            if (existing != null) {
                existing.setLocation(player.getLocation());
                player.sendMessage("&gSet the location of the warp &h" + existing.getName() + " &gto your current location.");
                return true;
            }
            
            warp = new ServerWarp(name, player.getLocation());
            plugin.getWarpManager().addWarp(warp);
            player.sendMessage("&gCreated a Server Warp with the name &h" + name);
        } else if (cmd.getName().equalsIgnoreCase("delwarp") || cmd.getName().equalsIgnoreCase("delplayerwarp")) {
            if (!(args.length > 0)) {
                player.sendMessage("&cYou must provide a warp name.");
                return true;
            }
            
            Warp targetWarp = plugin.getWarpManager().getServerWarp(args[0]);
            
            if (targetWarp == null) {
                player.sendMessage("&cCould not find a warp with that name.");
                return true;
            }
            
            if (!targetWarp.canAccess(player.getUniqueId())) {
                player.sendMessage("&cYou do not have access to that warp.");
                return true;
            }
            
            plugin.getWarpManager().removeWarp(targetWarp);
            player.sendMessage("&gYou have removed the warp &h" + targetWarp.getName());
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }
        
        List<String> results = new ArrayList<>();
        List<String> possibleResults = new ArrayList<>();
        if (cmd.getName().equalsIgnoreCase("warp") || cmd.getName().equalsIgnoreCase("delwarp")) {
            if (args.length == 1) {
                if (cmd.getName().equalsIgnoreCase("warp") || cmd.getName().equalsIgnoreCase("delwarp")) {
                    for (ServerWarp serverWarp : plugin.getWarpManager().getServerWarps()) {
                        possibleResults.add(serverWarp.getName());
                    }
                }
                results.addAll(getResults(args[0], possibleResults));
            } else if (args.length == 2) {
                if (cmd.getName().equalsIgnoreCase("warp")) {
                    if (!StringUtils.isEmpty(args[0])) {
                        possibleResults.add("modify");
                    }
                }
                results.addAll(getResults(args[1], possibleResults));
            } else if (args.length == 3) {
                if (cmd.getName().equalsIgnoreCase("warp")) {
                    if (!StringUtils.isEmpty(args[0])) {
                        if (Utils.checkCmdAliases(args, 1, "modify")) {
                            possibleResults.addAll(Arrays.asList("name", "description", "permission"));
                        }
                    }
                }
                results.addAll(getResults(args[2], possibleResults));
            }
        }
        
        return results;
    }
}