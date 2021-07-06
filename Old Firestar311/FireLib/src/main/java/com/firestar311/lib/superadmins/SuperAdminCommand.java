package com.firestar311.lib.superadmins;

import com.firestar311.lib.player.User;
import com.firestar311.lib.player.PlayerManager;
import com.firestar311.lib.util.Result;
import com.firestar311.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;

import java.util.*;
import java.util.Map.Entry;

public class SuperAdminCommand implements CommandExecutor {
    
    private SuperAdminManager superAdminManager;
    
    public SuperAdminCommand(SuperAdminManager superAdminManager) {
        this.superAdminManager = superAdminManager;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(Utils.color("&cOnly the console may issue that command."));
            return true;
        }
        
        if (!(args.length > 0)) {
            sender.sendMessage(Utils.color("&cYou must provide at least one name or uuid to change."));
            return true;
        }
        
        if (Utils.checkCmdAliases(args, 0, "global")) {
            boolean value = !superAdminManager.isGlobal();
            superAdminManager.setGlobal(value);
            sender.sendMessage(Utils.color("&aToggled SuperAdmin global status to " + value));
            return true;
        } else if (Utils.checkCmdAliases(args, 0, "enable")) {
            if (superAdminManager.isEnabled()) {
                sender.sendMessage("&aSuperAdmins feature is already enabled");
                return true;
            }
            
            superAdminManager.setEnabled(true);
            Bukkit.getServer().getPluginManager().callEvent(new SuperAdminToggleEvent(true));
            sender.sendMessage("&aToggled SuperAdmin feature to true");
            return true;
        } else if(Utils.checkCmdAliases(args, 0, "disable")) {
            if (!superAdminManager.isEnabled()) {
                sender.sendMessage("&aSuperAdmins feature is already disabled");
                return true;
            }
    
            superAdminManager.setEnabled(false);
            Bukkit.getServer().getPluginManager().callEvent(new SuperAdminToggleEvent(false));
            sender.sendMessage("&aToggled SuperAdmin feature to false");
            return true;
        } else if (Utils.checkCmdAliases(args, 0, "list")) {
            PlayerManager playerManager = Bukkit.getServicesManager().getRegistration(PlayerManager.class).getProvider();
            Map<User, List<String>> superAdmins = new HashMap<>();
            if (!superAdminManager.isEnabled()) {
                sender.sendMessage(Utils.color("&cThe SuperAdmin feature is not enabled"));
                return true;
            }
            if (superAdminManager.isGlobal()) {
                for (UUID uuid : superAdminManager.getGlobalSuperAdmins()) {
                    User info = playerManager.getUser(uuid);
                    if (superAdmins.containsKey(info)) {
                        superAdmins.get(info).add("Global");
                    } else {
                        superAdmins.put(info, Collections.singletonList("Global"));
                    }
                }
            }
            
            if (!superAdminManager.getProviders().isEmpty()) {
                for (SuperAdminProvider provider : superAdminManager.getProviders()) {
                    for (UUID uuid : provider.getSuperAdmins()) {
                        User info = playerManager.getUser(uuid);
                        if (superAdmins.containsKey(info)) {
                            superAdmins.get(info).add(provider.getPlugin().getName());
                        }else {
                            superAdmins.put(info, Collections.singletonList(provider.getPlugin().getName()));
                        }
                    }
                }
            }
            
            if (!superAdmins.isEmpty()) {
                sender.sendMessage(Utils.color("&7List of SuperAdmins"));
                for (Entry<User, List<String>> entry : superAdmins.entrySet()) {
                    User info = entry.getKey();
                    List<String> types = entry.getValue();
                    
                    String typeString = StringUtils.join(types, ", ");
                    sender.sendMessage(Utils.color(" &8- &b" + info.getLastName() + "&a is a &eSuperAdmin&a in &d" + typeString));
                }
            } else {
                sender.sendMessage(Utils.color("&cThere are no SuperAdmins"));
            }
            return true;
        }
        
        if (!superAdminManager.isEnabled()) {
            sender.sendMessage(Utils.color("&cSuperAdmin feature is currently disabled"));
            return true;
        }
        
        if (!superAdminManager.isGlobal()) {
            sender.sendMessage(Utils.color("&cSuperAdmin feature is not globally used."));
            return true;
        }
        
        if (Utils.checkCmdAliases(args, 0, "add", "a")) {
            new Thread(() -> {
                Result<List<User>, List<String>> result = extractPlayers(args);
                List<User> superAdmins = result.getSuccess();
                List<String> invalid = result.getFail();
                if (!superAdmins.isEmpty()) {
                    for (User info : superAdmins) {
                        sender.sendMessage(Utils.color("&aAdded &b" + info.getLastName() + " &aas a &eSuperAdmin"));
                        superAdminManager.addGlobalSuperAdmin(info.getUniqueId());
                    }
                }
                
                if (!invalid.isEmpty()) {
                    invalid.forEach(s -> sender.sendMessage(Utils.color("&cThe entry " + s + " did not match a player.")));
                }
            }).start();
        } else if (Utils.checkCmdAliases(args, 0, "remove", "r")) {
            Result<List<User>, List<String>> result = extractPlayers(args);
            List<User> toremove = result.getSuccess();
            List<String> invalid = result.getFail();
    
            if (!toremove.isEmpty()) {
                toremove.forEach(info -> {
                    sender.sendMessage(Utils.color("&aRemoved &b" + info.getLastName() + " &aas a &eSuperAdmin"));
                    superAdminManager.removeGlobalSuperAdmin(info.getUniqueId());
                });
            }
    
            if (!invalid.isEmpty()) {
                invalid.forEach(s -> sender.sendMessage(Utils.color("&cThe entry " + s + " did not match a player.")));
            }
        }
        
        return true;
    }
    
    private Result<List<User>, List<String>> extractPlayers(String[] args) {
        PlayerManager playerManager = Bukkit.getServicesManager().getRegistration(PlayerManager.class).getProvider();
        List<String> rawsa = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
        List<User> players = new ArrayList<>();
        List<String> invalid = new ArrayList<>();
        for (String sa : rawsa) {
            User info;
            UUID uuid;
            try {
                uuid = UUID.fromString(sa);
                info = playerManager.getUser(uuid);
            } catch (Exception e) {
                info = playerManager.getUser(sa);
            }
            if (info == null) {
                invalid.add(sa);
            } else {
                players.add(info);
            }
        }
        
        return new Result<>(players, invalid);
    }
}