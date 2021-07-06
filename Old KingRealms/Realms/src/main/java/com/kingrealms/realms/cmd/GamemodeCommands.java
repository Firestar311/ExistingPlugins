package com.kingrealms.realms.cmd;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.profile.ServerProfile;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GamemodeCommands extends BaseCommand {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        RealmProfile profile = plugin.getProfileManager().getProfile(sender);
        if (!sender.hasPermission("realms.gamemode")) {
            if (!profile.getStaffMode().isActive()) {
                sender.sendMessage(Utils.color("&cYou are not allowed to use that command."));
                return true;
            }
        }
        
        RealmProfile target = null;
        GameMode gameMode = null;
        if (cmd.getName().equalsIgnoreCase("gamemode")) {
            if (!(args.length > 0)) {
                profile.sendMessage("&cYou must provide a gamemode.");
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 0, "creative", "c")) {
                gameMode = GameMode.CREATIVE;
            } else if (Utils.checkCmdAliases(args, 0, "survival", "s")) {
                gameMode = GameMode.SURVIVAL;
            } else if (Utils.checkCmdAliases(args, 0, "spectator", "sp")) {
                gameMode = GameMode.SPECTATOR;
            } else if (Utils.checkCmdAliases(args, 0, "adventure", "a")) {
                gameMode = GameMode.ADVENTURE;
            }
            
            if (args.length > 1) {
                target = plugin.getProfileManager().getProfile(args[1]);
            } else {
                target = profile;
            }
        } else if (cmd.getName().equalsIgnoreCase("gmc") || cmd.getName().equalsIgnoreCase("gms") || cmd.getName().equalsIgnoreCase("gmsp") || cmd.getName().equalsIgnoreCase("gma")) {
            if (cmd.getName().equalsIgnoreCase("gmc")) {
                gameMode = GameMode.CREATIVE;
            } else if (cmd.getName().equalsIgnoreCase("gms")) {
                gameMode = GameMode.SURVIVAL;
            } else if (cmd.getName().equalsIgnoreCase("gmsp")) {
                gameMode = GameMode.SPECTATOR;
            } else if (cmd.getName().equalsIgnoreCase("gma")) {
                gameMode = GameMode.ADVENTURE;
            }
    
            if (args.length > 0) {
                target = plugin.getProfileManager().getProfile(args[0]);
            } else {
                if (!profile.hasPermission("realms.gamemode.others")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                target = profile;
            }
        }
        
        if (gameMode == null) {
            profile.sendMessage("&cYou provided an invalid gamemode type.");
            return true;
        }
        
        if (gameMode == GameMode.CREATIVE) {
            if (profile.isActiveQuestLine(Realms.getInstance().getQuestManager().getNetherQuestLine())) {
                profile.resetProgress(Realms.getInstance().getQuestManager().getNetherQuestLine());
                profile.sendMessage("&4&lPortal Keeper &cYou have entered CREATIVE while doing the nether quest line. Progress Reset!");
            }
        }
        
        if (profile.getStaffMode().isActive()) {
            if (!profile.hasPermission("realms.staffmode.gamemode." + gameMode.name().toLowerCase())) {
                profile.sendMessage("&cYou do not have permission to use " + gameMode.name() + " in staff mode.");
                return true;
            }
        }
        
        if (!profile.hasPermission("realms.gamemode." + gameMode.name().toLowerCase())) {
            profile.sendMessage("&cYou do not have permission to use that gamemode.");
            return true;
        }
        
        if (target == null) {
            profile.sendMessage("&cYou provided an invalid target name.");
            return true;
        }
        
        if (target instanceof ServerProfile) {
            profile.sendMessage("&cConsole cannot be in a gamemode, please provide a target.");
            return true;
        }
        
        if (!target.isOnline()) {
            profile.sendMessage("&cThat player is not online.");
            return true;
        }
        target.setGamemode(gameMode);
        if (target.getStaffMode().isActive()) {
            target.getBukkitPlayer().setAllowFlight(true);
        }
        target.sendMessage("&iYour gamemode has been changed to &j" + gameMode.name().toLowerCase());
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> possibleResults = new ArrayList<>(), results = new ArrayList<>();
        if (cmd.getName().equalsIgnoreCase("gamemode")) {
            if (args.length == 1) {
                for (GameMode gameMode : GameMode.values()) {
                    possibleResults.add(gameMode.name().toLowerCase());
                }
                
                results.addAll(Utils.getResults(args[0], possibleResults));
            }
        } else if (cmd.getName().equalsIgnoreCase("gmc") || cmd.getName().equalsIgnoreCase("gms") || cmd.getName().equalsIgnoreCase("gmsp") || cmd.getName().equalsIgnoreCase("gma")) {
            if (args.length == 1) {
                if (sender.hasPermission("realms.gamemode.others")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        possibleResults.add(player.getName());
                    }
                }
    
                results.addAll(Utils.getResults(args[0], possibleResults));
            }
        }
        
        return results;
    }
}