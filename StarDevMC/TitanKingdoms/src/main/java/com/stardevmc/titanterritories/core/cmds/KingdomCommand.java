package com.stardevmc.titanterritories.core.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.enums.Flag;
import com.stardevmc.titanterritories.core.objects.holder.Kingdom;
import com.stardevmc.titanterritories.core.objects.member.Citizen;
import com.stardevmc.titanterritories.core.objects.member.Member;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.*;

public class KingdomCommand {
    
    private TitanTerritories plugin;
    
    public KingdomCommand(TitanTerritories plugin) {
        super();
        this.plugin = plugin;
    }
    
    public void executePlayer(Command cmd, Player player, String[] args) {
        if (!(args.length > 0)) {
            player.sendMessage(Utils.color("&cYou must provide more arguments"));
            return;
        }
        
        if (Utils.checkCmdAliases(args, 0, "help")) {
            sendCmdFormat(player, "create {kingdom}");
            sendCmdFormat(player, "join [kingdom]");
            sendCmdFormat(player, "accept [kingdom]");
            sendCmdFormat(player, "deny [kingdom]");
            sendCmdFormat(player, "disband");
            sendCmdFormat(player, "invite list|{player}");
            sendCmdFormat(player, "announcements create|remove|list|view|edit|start|stop (arguments)");
            sendCmdFormat(player, "claim");
            sendCmdFormat(player, "unclaim");
            sendCmdFormat(player, "deposit {amount}");
            sendCmdFormat(player, "withdraw {amount}");
            sendCmdFormat(player, "transactions");
            sendCmdFormat(player, "balance");
            sendCmdFormat(player, "mail [read|compose|list] [arguments]");
            sendCmdFormat(player, "rank create|delete|edit|view|list (arguments)");
            sendCmdFormat(player, "citizens list|view|remove");
            sendCmdFormat(player, "warps list|create|remove|viewvisits");
            sendCmdFormat(player, "exp");
            sendCmdFormat(player, "transfer {newMonarch}");
            return;
        }
        
        Kingdom kingdom = plugin.getKingdomManager().getKingdom(player);
        Member member = plugin.getMemberManager().getMember(player.getUniqueId());
        if (kingdom == null) {
            if (args.length == 1) {
                if (Utils.checkCmdAliases(args, 0, "accept", "deny")) {
                    Kingdom invitedKingdom = null;
                    for (Kingdom k : plugin.getKingdomManager().getKingdoms()) {
                        if (k.getInviteController().hasBeenInvited(player.getUniqueId())) {
                            if (invitedKingdom == null) {
                                invitedKingdom = k;
                            } else {
                                invitedKingdom = null;
                                break;
                            }
                        }
                    }
                    
                    if (invitedKingdom != null) {
                        List<String> resizedArgs = new ArrayList<>(Arrays.asList(args));
                        resizedArgs.add(invitedKingdom.getName());
                        args = resizedArgs.toArray(new String[0]);
                    }
                } else {
                    member.sendMessage("&cNot enough arguments for non-kingdom member commands.");
                    return;
                }
            }
            
            String name = args[1];
            if (Utils.checkCmdAliases(args, 0, "create", "c")) {
                plugin.getKingdomManager().createKingdom(member, name);
            } else if (Utils.checkCmdAliases(args, 0, "join", "j")) {
                Kingdom targetKingdom = plugin.getKingdomManager().getKingdom(name);
                if (targetKingdom == null) {
                    member.sendMessage("&cThere is no Kingdom by that name.");
                    return;
                }
                
                if (!targetKingdom.getFlagController().getFlags().contains(Flag.OPEN)) {
                    member.sendMessage("&cThat Kingdom is invite only.");
                    return;
                }
                
                Citizen citizen = new Citizen(member, targetKingdom.getUniqueId());
                targetKingdom.getUserController().add(citizen);
                targetKingdom.sendMemberMessage(member.getName() + " has joined the Kingdom!");
            } else if (Utils.checkCmdAliases(args, 0, "deny", "accept")) {
                Kingdom targetKingdom = plugin.getKingdomManager().getKingdom(name);
                if (targetKingdom == null) {
                    member.sendMessage("&cThere is no Kingdom by that name.");
                    return;
                }
                
                targetKingdom.getInviteController().handleCommand(cmd, targetKingdom, member, args);
            } else {
                member.sendMessage("&cYou are not a member of a Kingdom, so the only allowed commands are: create, join and deny");
            }
            return;
        }
        
        Citizen citizen = kingdom.getUserController().get(player.getUniqueId());
        if (citizen == null) {
            player.sendMessage(Utils.color("&cThere was a problem getting your membership information for your Kingdom"));
            return;
        }
        
        if (Utils.checkCmdAliases(args, 0, "disband")) {
            if (!kingdom.isMonarch(player)) {
                citizen.sendMessage("&cOnly the Monarch can use that command.");
                return;
            }
            
            kingdom.disband();
            plugin.getKingdomManager().removeKingdom(kingdom);
            Bukkit.broadcastMessage(Utils.color("&aThe Kingdom " + kingdom.getName() + " has been disbanded."));
            return;
        } else if (Utils.checkCmdAliases(args, 0, "info")) {
            citizen.sendMessage("&6Viewing information for the Kingdom " + kingdom.getName());
            citizen.sendMessage("&7Monarch: " + kingdom.getMonarch().getName());
            citizen.sendMessage("&7Total Claims: " + kingdom.getClaimController().getPlots().size());
            citizen.sendMessage("&7Total Balance: " + kingdom.getEconomyController().getBalance());
            citizen.sendMessage("&7Total Experience: " + kingdom.getExperienceController().getTotalExperience());
            citizen.sendMessage("&7Total Ranks: " + kingdom.getRankController().getRanks().size());
            citizen.sendMessage("&7Total Citizens: " + kingdom.getUserController().getUsers().size());
            citizen.sendMessage("&7Total Warps: " + kingdom.getWarpController().getWarps().size());
            return;
        } else if (Utils.checkCmdAliases(args, 0, "spawn")) {
            citizen.teleport(kingdom.getSpawnpoint());
            citizen.sendMessage("&aTeleported you to the spawnpoint of your kingdom.");
            return;
        } else if (Utils.checkCmdAliases(args, 0, "leave")) {
            kingdom.getUserController().remove(citizen);
            kingdom.sendMemberMessage(citizen.getName() + " has left the kingdom.");
            return;
        } else if (Utils.checkCmdAliases(args, 0, "transfer")) {
            if (!(args.length > 1)) {
                citizen.sendMessage("&cYou must provide a player to transfer your kingdom to.");
                return;
            }
            
            Citizen target = kingdom.getUserController().get(args[1]);
            if (target == null) {
                citizen.sendMessage("&cThe player you provided is not a member of your kingdom");
                return;
            }
            
            kingdom.setLeader(target);
            citizen.setRank(kingdom.getRankController().getDefaultRank());
            target.setRank(kingdom.getRankController().getLeaderRank());
            kingdom.sendMemberMessage("The Monarchy of the Kingdom has been transfered to " + target.getName());
        }
        
        if (Utils.checkCmdAliases(args, 0, "announcements", "a", "ann")) {
            kingdom.getAnnouncementController().handleCommand(cmd, kingdom, citizen, args);
        } else if (Utils.checkCmdAliases(args, 0, "claim", "unclaim")) {
            kingdom.getClaimController().handleCommand(cmd, kingdom, citizen, args);
        } else if (Utils.checkCmdAliases(args, 0, "deposit", "withdraw", "balance", "bal", "transactions", "eco")) {
            kingdom.getEconomyController().handleCommand(cmd, kingdom, citizen, args);
        } else if (Utils.checkCmdAliases(args, 0, "exp")) {
            kingdom.getExperienceController().handleCommand(cmd, kingdom, citizen, args);
        } else if (Utils.checkCmdAliases(args, 0, "flags", "f")) {
            kingdom.getFlagController().handleCommand(cmd, kingdom, citizen, args);
        } else if (Utils.checkCmdAliases(args, 0, "invite")) {
            kingdom.getInviteController().handleCommand(cmd, kingdom, citizen, args);
        } else if (Utils.checkCmdAliases(args, 0, "mail")) {
            kingdom.getMailController().handleCommand(cmd, kingdom, citizen, args);
        } else if (Utils.checkCmdAliases(args, 0, "rank")) {
            kingdom.getRankController().handleCommand(cmd, kingdom, citizen, args);
        } else if (Utils.checkCmdAliases(args, 0, "citizens")) {
            kingdom.getUserController().handleCommand(cmd, kingdom, citizen, args);
        } else if (Utils.checkCmdAliases(args, 0, "shop")) {
            citizen.sendMessage("&cNot yet implemented");
            //kingdom.getShopController().handleCommand(cmd, kingdom, citizen, args);
        } else if (Utils.checkCmdAliases(args, 0, "warps", "warp")) {
            kingdom.getWarpController().handleCommand(cmd, kingdom, citizen, args);
        } else if (Utils.checkCmdAliases(args, 0, "housing")) {
            citizen.sendMessage("&cNot yet implemented");
        } else if (Utils.checkCmdAliases(args, 0, "laws")) {
            citizen.sendMessage("&cNot yet implemented");
        } else if (Utils.checkCmdAliases(args, 0, "exp")) {
            citizen.sendMessage("&cNot yet implemented");
        } else if (Utils.checkCmdAliases(args, 0, "election")) {
            kingdom.getElectionController().handleCommand(cmd, kingdom, citizen, args);
        }
    }
    
    private void sendCmdFormat(Player player, String cmd) {
        player.sendMessage(Utils.color("&7/kingdom " + cmd));
    }
}