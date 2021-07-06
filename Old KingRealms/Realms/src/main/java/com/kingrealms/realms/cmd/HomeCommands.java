package com.kingrealms.realms.cmd;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.home.Home;
import com.kingrealms.realms.home.StaffHome;
import com.kingrealms.realms.limits.limit.IntegerLimit;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.territory.base.Territory;
import com.starmediadev.lib.util.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeCommands extends BaseCommand {
    
    private final Realms plugin = Realms.getInstance();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players can do that."));
            return true;
        }
        
        RealmProfile profile = plugin.getProfileManager().getProfile(((Player) sender).getUniqueId());
        if (cmd.getName().equalsIgnoreCase("home")) {
            Home home;
            if (args.length == 0) {
                home = profile.getHome("home");
                if (home == null) {
                    home = profile.getHome("bed");
                }
            
                if (home == null) {
                    StringBuilder sb = new StringBuilder();
                    for (Home h : profile.getHomes()) {
                        sb.append(h.getName()).append(" ");
                    }
                
                    profile.sendMessage("&gHomes: &h" + sb.toString());
                    return true;
                }
            } else {
                home = profile.getHome(args[0]);
                if (home == null) {
                    String[] targetSplit = args[0].split(":");
                    if (targetSplit.length == 2) {
                        if (sender.hasPermission("realms.homes.teleport.other")) {
                            RealmProfile targetProfile = plugin.getProfileManager().getProfile(targetSplit[0]);
                            if (targetProfile == null) {
                                profile.sendMessage("&cCould not find a player by that name.");
                                return true;
                            }
                        
                            Home targetHome = targetProfile.getHome(targetSplit[1]);
                            if (targetHome == null) {
                                profile.sendMessage("&c" + targetProfile.getUser().getLastName() + " does not have a home named " + targetSplit[1]);
                                return true;
                            }
                        
                            profile.teleport(targetHome);
                            profile.sendMessage("&gTeleported to &h" + targetProfile.getUser().getLastName() + "&g's home named &d" + targetHome.getName());
                            return true;
                        }
                    } else if (targetSplit.length == 1) {
                        RealmProfile targetProfile = plugin.getProfileManager().getProfile(targetSplit[0]);
                        if (targetProfile == null) {
                            profile.sendMessage("&cCould not find a player by that name.");
                            return true;
                        }
    
                        StringBuilder sb = new StringBuilder();
                        for (Home h : targetProfile.getHomes()) {
                            sb.append(h.getName()).append(" ");
                        }
    
                        profile.sendMessage("&gHomes for &h" + targetProfile.getName() + "&g: &h" + sb.toString());
                        return true;
                    }
                
                    profile.sendMessage("&cCould not find a home by that name.");
                    return true;
                }
            }
        
            profile.teleport(home);
            profile.sendMessage("&gTeleported to the home &h" + home.getName());
        } else if (cmd.getName().equalsIgnoreCase("sethome")) {
            if (args.length == 0) {
                profile.sendMessage("&cYou must provide a home name.");
                return true;
            } else if (args.length > 1) {
                profile.sendMessage("&cHome names can only have one word.");
                return true;
            }
        
            if (plugin.getSpawn().contains(profile.getLocation())) {
                profile.sendMessage("&cYou cannot create homes in the spawn.");
                if (sender.hasPermission("realms.homes.staffhomes")) {
                    profile.sendMessage("&7&oPlease use a staff home instead.");
                }
                return true;
            }
        
            if (plugin.getWarzone().contains(profile.getLocation())) {
                profile.sendMessage("&cYou cannot create homes in the warzone.");
                if (sender.hasPermission("realms.homes.staffhomes")) {
                    profile.sendMessage("&7&oPlease use a staff home instead.");
                }
                return true;
            }
        
            Territory territory = plugin.getTerritoryManager().getTerritory(profile.getLocation());
            if (territory != null) {
                Territory playerTerritory = plugin.getTerritoryManager().getTerritory((Player) sender);
                if (playerTerritory == null || !playerTerritory.getUniqueId().equalsIgnoreCase(territory.getUniqueId())) {
                    profile.sendMessage("&cYou can only create homes in the wilderness or your claimed land.");
                    if (sender.hasPermission("realms.homes.staffhomes")) {
                        profile.sendMessage("&7&oPlease use a staff home instead.");
                    }
                    return true;
                }
            }
    
            IntegerLimit homeLimit = (IntegerLimit) plugin.getLimitsManager().getLimit("player_home_limit");
            
            if (profile.getHomes().size() >= profile.getLimitValue(homeLimit).intValue()) {
                profile.sendMessage("&cYou have reached the maximum amount of homes allowed.");
                return true;
            }
        
            Home home = profile.getHome(args[0]);
            if (home != null) {
                home.setLocation(profile.getLocation());
                profile.sendMessage("&gChanged &h" + home.getName() + "&g's location to your current location.");
            } else {
                home = new Home(profile.getUniqueId(), args[0], profile.getLocation(), System.currentTimeMillis());
                profile.addHome(home);
                profile.sendMessage("&gCreated a new home named &h" + home.getName() + " &gat your current location.");
            }
        } else if (cmd.getName().equalsIgnoreCase("delhome")) {
            if (!(args.length > 0)) {
                profile.sendMessage("&cYou must provide a home name to remove.");
                return true;
            }
        
            Home home = profile.getHome(args[0]);
            if (home == null) {
                profile.sendMessage("&cYou do not have a home by that name.");
                return true;
            }
        
            profile.removeHome(home);
            profile.sendMessage("&gDeleted the home &h" + home.getName());
        } else if (cmd.getName().equalsIgnoreCase("staffhome")) {
            if (!profile.hasPermission("realms.home.staff")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            
            if (!(args.length > 0)) {
                profile.sendMessage("&cYou must provide more arguments.");
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 0, "set", "create")) {
                if (!(args.length > 1)) {
                    profile.sendMessage("&cYou must provide a home name.");
                    return true;
                }
    
                StaffHome staffHome = profile.getStaffHome(args[1]);
                if (staffHome == null) {
                    staffHome = new StaffHome(profile.getUniqueId(), args[1], profile.getLocation(), System.currentTimeMillis());
                    profile.addHome(staffHome);
                    profile.sendMessage("&iCreated a new staff home called &j" + args[1]);
                } else {
                    staffHome.setLocation(profile.getLocation());
                    profile.sendMessage("&iSet the location of the staff home &j" + args[1] + " &ito your current location.");
                }
            } else if (Utils.checkCmdAliases(args, 0, "delete", "remove")) {
                if (!(args.length > 1)) {
                    profile.sendMessage("&cYou must provide a home name.");
                    return true;
                }
    
                StaffHome staffHome = profile.getStaffHome(args[1]);
                if (staffHome == null) {
                    profile.sendMessage("&cA staff home by that name does not exist");
                    return true;
                }
                
                profile.removeStaffHome(staffHome);
                profile.sendMessage("&iRemoved the staff home &j" + staffHome.getName());
            } else {
                if (!profile.getStaffMode().isActive()) {
                    if (!profile.hasPermission("realms.homes.staffhome.teleport.bypass")) {
                        profile.sendMessage("&cYou must be in staff mode to teleport to staff homes.");
                        return true;
                    }
                }
                
                if (!(args.length > 0)) {
                    profile.sendMessage("&cYou must provide a home name.");
                    return true;
                }
                
                StaffHome home = profile.getStaffHome(args[0]);
                if (home == null) {
                    profile.sendMessage("&cThere is no staff home by that name.");
                    return true;
                }
                
                profile.teleport(home);
                profile.sendMessage("&iTeleported to the staff home &j" + home.getName());
            }
        } else if (cmd.getName().equalsIgnoreCase("renamehome")) {
            if (!(args.length > 1)) {
                sender.sendMessage(Utils.color("&cYou must provide an existing home name and a new home name"));
                return true;
            }
            
            Home home = profile.getHome(args[0]);
            if (home == null) {
                profile.sendMessage("&cThe name you provided did not match a valid home.");
                return true;
            }
            
            String oldName = home.getName();
            String name = args[1];
            if (profile.getHome(name) != null) {
                profile.sendMessage("&cA home by the name of " + name + " already exists.");
                return true;
            }
            
            home.setName(name);
            profile.sendMessage("&gYou set the name of the home &h" + oldName + " &gto &h" + name);
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
        if (cmd.getName().equalsIgnoreCase("home") || cmd.getName().equalsIgnoreCase("delhome")) {
            if (args.length == 1) {
                for (Home home : plugin.getProfileManager().getProfile(((Player) sender).getUniqueId()).getHomes()) {
                    possibleResults.add(home.getName());
                }
            }
            
            results.addAll(getResults(args[0], possibleResults));
        }
        
        return results;
    }
}