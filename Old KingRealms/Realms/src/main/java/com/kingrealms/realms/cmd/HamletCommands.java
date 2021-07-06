package com.kingrealms.realms.cmd;

import com.kingrealms.realms.api.events.HamletCreateEvent;
import com.kingrealms.realms.api.events.HamletJoinEvent;
import com.kingrealms.realms.channel.Channel;
import com.kingrealms.realms.channel.Participant;
import com.kingrealms.realms.channel.enums.Role;
import com.kingrealms.realms.economy.EconomyResponse;
import com.kingrealms.realms.limits.limit.IntegerLimit;
import com.kingrealms.realms.period.TimePeriod;
import com.kingrealms.realms.plot.claimed.ClaimedPlot;
import com.kingrealms.realms.plot.Plot;
import com.kingrealms.realms.profile.ProfileManager;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.territory.TerritoryManager;
import com.kingrealms.realms.territory.base.Invite;
import com.kingrealms.realms.territory.base.Territory;
import com.kingrealms.realms.territory.base.member.Member;
import com.kingrealms.realms.territory.base.response.*;
import com.kingrealms.realms.territory.enums.*;
import com.kingrealms.realms.territory.medievil.Hamlet;
import com.kingrealms.realms.warps.type.TerritoryWarp;
import com.starmediadev.lib.pagination.*;
import com.starmediadev.lib.region.Cuboid;
import com.starmediadev.lib.user.User;
import com.starmediadev.lib.util.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;

public class HamletCommands extends BaseCommand {
    
    private final Set<UUID> disbandConfirm = new HashSet<>();
    private final Map<UUID, UUID> transferConfirm = new HashMap<>();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSeason(sender)) { return true; }
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command. Please use the Admin Command from Console."));
            return true;
        }
        
        ProfileManager profileManager = plugin.getProfileManager();
        TerritoryManager territoryManager = plugin.getTerritoryManager();
        
        RealmProfile player = profileManager.getProfile(((Player) sender).getUniqueId());
        
        if (!(args.length > 0)) {
            player.sendMessage(Utils.color("&cYou must provide a subcommand."));
            return true;
        }
        
        if (cmd.getName().equalsIgnoreCase("hamlets")) {
            if (Utils.checkCmdAliases(args, 0, "create", "c")) {
                if (!(args.length > 1)) {
                    sender.sendMessage(Utils.color("&cYou must provide a hamlet name."));
                    return true;
                }
                
                String name = StringUtils.join(args, " ", 1, args.length);
                if (territoryManager.getTerritory(name) != null) {
                    player.sendMessage(Utils.color("&cThere is already a hamlet by that name."));
                    return true;
                }
                
                Hamlet hamlet = (Hamlet) TimePeriod.MEDEVIL.createSettlement(name);
                hamlet.addMember(player.getUniqueId(), Rank.LEADER);
                hamlet.getChannel().addParticipant(player.getUniqueId(), Role.OWNER);
                hamlet.setSpawnpoint(player.getLocation());
                hamlet.setPrivacy(Privacy.PRIVATE, player.getUniqueId());
                territoryManager.addTerritory(hamlet);
                ClaimResponse response = hamlet.claim(player);
                if (response == ClaimResponse.CLAIMED_OTHER) {
                    player.sendMessage(Utils.color("&cThat plot is already claimed by another hamlet."));
                    player.sendMessage(Utils.color("&cPlease use the /hamlet claim command on an unclaimed plot."));
                }
                plugin.getServer().getPluginManager().callEvent(new HamletCreateEvent(player, hamlet));
                Bukkit.broadcastMessage(Utils.color("&d" + player.getName() + " created a new Hamlet called &e" + hamlet.getName()));
                hamlet.createDefaultAccount();
                String line = Utils.blankLine("&6", Constants.LINE_CHARS);
                String membersLine;
                if (hamlet.getPrivacy() == Privacy.PRIVATE) {
                    membersLine = Utils.getCenteredMessage("&gTo invite members use &h/hamlet invite <player>");
                } else {
                    membersLine = Utils.getCenteredMessage("&gPlayers can join your Hamlet freely.");
                }
                player.sendMessage("", line, Utils.getCenteredMessage("&gYou have created a Hamlet named &h" + hamlet.getName()), Utils.getCenteredMessage("&gYou are the leader of it so you can do anything."), Utils.getCenteredMessage("&gBy default the privacy is &h" + hamlet.getPrivacy().name()), Utils.getCenteredMessage("&gYou can change this by &h/hamlet set privacy <value>"), Utils.getCenteredMessage(membersLine), Utils.getCenteredMessage("&gClaim more land with &h/hamlet claim"), Utils.getCenteredMessage("&gRemove members with &h/hamlet remove member <name>"), line, "");
                return true;
            } else if (Utils.checkCmdAliases(args, 0, "map")) {
                new BukkitRunnable() {
                    public void run() {
                        int zSize = 9, xSize = 25;
                        Plot[][] plotArray = new Plot[zSize][xSize];
    
                        Location location = player.getLocation().clone();
                        int x = location.getBlockX() - (16 * (xSize / 2));
                        location.setZ(location.getBlockZ() - (16 * (zSize / 2.0)));
                        location.setX(x);
                        
                        for (int i = 0; i < plotArray.length; i++) {
                            for (int j = 0; j < plotArray[i].length; j++) {
                                plotArray[i][j] = plugin.getPlotManager().getPlot(location);
                                location.setX(location.getBlockX() + 16);
                            }
                            location.setZ(location.getBlockZ() + 16);
                            location.setX(x);
                        }
    
                        String[][] claimArray = new String[zSize][xSize];
                        for (int i = 0; i < plotArray.length; i++) {
                            for (int j = 0; j < plotArray[i].length; j++) {
                                Plot plot = plotArray[i][j];
                                if (plot.contains(player.getLocation())) {
                                    claimArray[i][j] = "&6+";
                                    continue;
                                }
                                
                                if (plugin.getSpawn().contains(plot.getCenter())) {
                                    claimArray[i][j] = "&dS";
                                    continue;
                                }
    
                                if (plugin.getWarzone().contains(plot.getCenter())) {
                                    claimArray[i][j] = "&4W";
                                    continue;
                                }
            
                                Territory territory = plugin.getTerritoryManager().getTerritory(plot);
                                if (territory == null) {
                                    claimArray[i][j] = "&8-";
                                } else {
                                    if (territory.isMember(player)) {
                                        if (plot.contains(territory.getSpawnpoint())) {
                                            claimArray[i][j] = "&aH";
                                        } else {
                                            claimArray[i][j] = "&a+";
                                        }
                                    } else {
                                        Territory playerTerritory = plugin.getTerritoryManager().getTerritory(player);
                                        if (playerTerritory != null) {
                                            if (playerTerritory.getRelationship(territory) == Relation.NEUTRAL) {
                                                claimArray[i][j] = "&f+";
                                            } else if (playerTerritory.getRelationship(territory) == Relation.ALLY) {
                                                claimArray[i][j] = "&e+";
                                            } else if (playerTerritory.getRelationship(territory) == Relation.ENEMY) {
                                                claimArray[i][j] = "&c#";
                                            }
                                        } else {
                                            claimArray[i][j] = "&f+";
                                        }
                                    }
                                }
                            }
                        }
    
    
                        player.sendMessage("&e&lRealms Claim Map",
                                "&0----- " + StringUtils.join(claimArray[0]) + "    &8- &f= Unclaimed",
                                "&0----- " + StringUtils.join(claimArray[1]) + "    &f+ &f= Claimed (Other)",
                                "&0----- " + StringUtils.join(claimArray[2]) + "    &a+ &f= Claimed (Self)",
                                "&0--&fN&0-- " + StringUtils.join(claimArray[3]) + "    &e+ &f= Claimed (Ally)",
                                "&0-&fW+E&0- " + StringUtils.join(claimArray[4]) + "    &6+ &f= Current Plot",
                                "&0--&fS&0-- " + StringUtils.join(claimArray[5]) + "    &c# &f= Enemy",
                                "&0----- " + StringUtils.join(claimArray[6]) + "    &aH &f= Home Plot",
                                "&0----- " + StringUtils.join(claimArray[7]) + "    &dS &f= Spawn Plot",
                                "&0----- " + StringUtils.join(claimArray[8]) + "    &4W &f= Warzone Plot");
                    }
                }.runTaskAsynchronously(plugin);
                return true;
            }
            
            IntegerLimit memberLimit = (IntegerLimit) plugin.getLimitsManager().getLimit("territory_member_limit");
            
            if (Utils.checkCmdAliases(args, 0, "join", "j", "accept", "a", "deny", "d")) {
                if (!(args.length > 1)) {
                    player.sendMessage(Utils.color("&cYou must provide a hamlet name."));
                    return true;
                }
                
                String name = StringUtils.join(args, " ", 1, args.length);
                Hamlet hamlet = (Hamlet) territoryManager.getTerritory(name);
                Territory playerTerritory = territoryManager.getTerritory(player);
                
                if (playerTerritory != null) {
                    player.sendMessage(Utils.color("&cYou are already a member of a hamlet."));
                    return true;
                }
                
                if (hamlet == null) {
                    player.sendMessage(Utils.color("&cA hamlet by that name does not exist."));
                    return true;
                }
                
                if (Utils.checkCmdAliases(args, 0, "join", "j")) {
                    if (hamlet.getPrivacy() == Privacy.PRIVATE) {
                        player.sendMessage(Utils.color("&cYou must be invited to that hamlet."));
                        player.sendMessage(Utils.color("&7&oThe leader is " + hamlet.getLeader().getName()));
                        return true;
                    }
                    
                    if (memberLimit.getValue() > 0) {
                        int limitValue = hamlet.getLimitValue(memberLimit).intValue();
                        if (hamlet.getMembers().size() >= limitValue) {
                            player.sendMessage("&cThat hamlet has reached the maximum about of members allowed.");
                            return true;
                        }
                    }
                    hamlet.sendMemberMessage("&t" + player.getName() + " &shas joined your hamlet.");
                    hamlet.addMember(player.getUniqueId());
                    plugin.getServer().getPluginManager().callEvent(new HamletJoinEvent(player, hamlet));
                    player.sendMessage(Utils.color("&gYou have joined the hamlet &h" + hamlet.getName()));
                } else if (Utils.checkCmdAliases(args, 0, "leave", "l")) {
                    Member member = hamlet.getMember(player.getUniqueId());
                    if (member.getRank().equals(Rank.LEADER)) {
                        player.sendMessage("&cYou cannot leave a hamlet that you are the leader of.");
                        return true;
                    }
                    hamlet.removeMember(player.getUniqueId(), null);
                    hamlet.sendMemberMessage("&t" + player.getName() + " &shas left your hamlet.");
                    player.sendMessage(Utils.color("&gYou have left the hamlet &h" + hamlet.getName()));
                } else if (Utils.checkCmdAliases(args, 0, "accept", "a")) {
                    InviteResponse response = hamlet.acceptInvite(player.getUniqueId());
                    if (response == InviteResponse.NOT_INVITED) {
                        player.sendMessage(Utils.color("&cYou have not been invited to " + hamlet.getName()));
                        return true;
                    }
                    
                    if (response == InviteResponse.SUCCESS) {
                        Member hamletMember = hamlet.getMember(player.getUniqueId());
                        Member inviter = hamlet.getMember(hamletMember.getInvite().getActor());
                        plugin.getServer().getPluginManager().callEvent(new HamletJoinEvent(player, hamlet));
                        hamlet.sendMemberMessage("&t" + player.getName() + " &shas joined the hamlet by an invite from &t" + inviter.getName());
                        for (Territory territory : territoryManager.getTerritories()) {
                            if (territory instanceof Hamlet) {
                                InviteResponse or = territory.denyInvite(player.getUniqueId());
                                if (or == InviteResponse.SUCCESS) {
                                    player.sendMessage(Utils.color("&gInvite from &h" + territory.getName() + " &gwas denied because you accepted another invite."));
                                }
                            }
                        }
                    } else {
                        player.sendMessage(Utils.color("&cThere was in issue accepting that invite."));
                        return true;
                    }
                } else if (Utils.checkCmdAliases(args, 0, "deny", "d")) {
                    InviteResponse response = hamlet.denyInvite(player.getUniqueId());
                    if (response == InviteResponse.NOT_INVITED) {
                        player.sendMessage(Utils.color("&cYou have not been invited to " + hamlet.getName()));
                        return true;
                    }
                    
                    if (response == InviteResponse.SUCCESS) {
                        player.sendMessage(Utils.color("&gYou have denied the invite from &h" + hamlet.getName()));
                    } else {
                        player.sendMessage(Utils.color("&cThere was in issue denying that invite."));
                        return true;
                    }
                }
                
                return true;
            } else {
                Hamlet hamlet = (Hamlet) territoryManager.getTerritory(player);
                if (hamlet == null) {
                    player.sendMessage(Utils.color("&cYou are not a member of a Hamlet."));
                    return true;
                }
                
                IntegerLimit claimLimit = (IntegerLimit) plugin.getLimitsManager().getLimit("territory_claim_limit");
                int claimLimitValue = hamlet.getLimitValue(claimLimit).intValue();
                
                Map<String, Plot> toClaim = new HashMap<>();
                if (Utils.checkCmdAliases(args, 0, "claim", "c")) {
                    if (hamlet.getPlots().size() >= claimLimitValue) {
                        player.sendMessage("&cYour hamlet has reached the maximum amount of claims allowed.");
                        return true;
                    }
                    
                    if (args.length > 1) {
                        int radius;
                        try {
                            radius = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(Utils.color("&cInvalid whole number: " + args[1]));
                            return true;
                        }
                        
                        Location loc1 = player.getLocation().clone().add((radius * 16), 0, (radius * 16));
                        Location loc2 = player.getLocation().clone().subtract((radius * 16), 0, (radius * 16));
                        
                        Cuboid cuboid = new Cuboid(loc1, loc2);
                        
                        int xMax = cuboid.getMaximum().getBlockX(), xMin = cuboid.getMinimum().getBlockX();
                        int zMax = cuboid.getMaximum().getBlockZ(), zMin = cuboid.getMinimum().getBlockZ();
                        
                        World world = player.getWorld();
                        for (int x = xMin; x < xMax; x++) {
                            for (int z = zMin; z < zMax; z++) {
                                Location location = new Location(world, x, 2, z);
                                Plot plot = plugin.getPlotManager().getPlot(location);
                                toClaim.put(plot.getUniqueId(), plot);
                            }
                        }
                    } else {
                        Plot plot = plugin.getPlotManager().getPlot(player.getLocation());
                        toClaim.put(plot.getUniqueId(), plot);
                    }
                    
                    if (toClaim.size() > 1) {
                        if ((toClaim.size() + hamlet.getPlots().size()) > claimLimitValue) {
                            player.sendMessage(Utils.color("&cYour selection of plots is over the claim limit."));
                            return true;
                        }
                    }
                    
                    for (Entry<String, Plot> entry : toClaim.entrySet()) {
                        ClaimResponse response = hamlet.claim(entry.getValue().getCenter(), player.getUniqueId());
                        if (response == ClaimResponse.CLAIMED_OTHER) {
                            player.sendMessage(Utils.color("&cThat plot is claimed by another Hamlet."));
                        } else if (response == ClaimResponse.ALREADY_CLAIMED) {
                            player.sendMessage(Utils.color("&cThat plot is already claimed by your Hamlet."));
                        } else if (response == ClaimResponse.NO_PERMISSION) {
                            player.sendMessage(Utils.color("&cYou do not have permission to claim for your Hamlet."));
                            break;
                        } else if (response == ClaimResponse.UNKOWN_ERROR) {
                            player.sendMessage(Utils.color("&cThere was an unknown error processing your claim."));
                        } else if (response == ClaimResponse.SUCCESS) {
                            hamlet.sendMemberMessage("&t" + player.getName() + " &shas claimed the plot &t" + hamlet.getPlot(entry.getValue().getCenter()).getPlot().toString());
                            
                            if (hamlet.getPrivacy() == Privacy.PRIVATE) {
                                ClaimedPlot claimedPlot = hamlet.getPlot(player.getLocation());
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (!hamlet.isMember(p)) {
                                        if (claimedPlot.contains(p.getLocation())) {
                                            if (!p.hasPermission("realms.staff.claim.override")) {
                                                p.teleport(plugin.getSpawn().getSpawnpoint());
                                                p.sendMessage(Utils.color("&gYou were teleported to the &dSpawn &gas the plot you were in was claimed by &h" + hamlet.getName()));
                                            }
                                        }
                                    }
                                }
                            }
                            
                        } else if (response == ClaimResponse.IN_SPAWN) {
                            player.sendMessage(Utils.color("&cThat plot is claimed by spawn."));
                        } else if (response == ClaimResponse.IN_WARZONE) {
                            player.sendMessage(Utils.color("&cThat plot is claimed by the warzone."));
                        } else if (response == ClaimResponse.CLAIM_LIMIT) {
                            player.sendMessage(Utils.color("&cYour hamlet has " + hamlet.getPlots().size() + " out of a max of " + plugin.getLimitsManager().getLimit("territory_claim_limit").getValue() + " you cannot claim anymore."));
                            break;
                        }
                    }
                } else if (Utils.checkCmdAliases(args, 0, "unclaim", "uc")) {
                    ClaimedPlot plot = hamlet.getPlot(player.getLocation());
                    if (plot != null) {
                        if (plot.contains(hamlet.getSpawnpoint())) {
                            player.sendMessage(Utils.color("&cThat plot contains the hamlet's spawnpoint, it cannot be unclaimed."));
                            return true;
                        }
                    }
                    ClaimResponse response = hamlet.unclaim(player);
                    if (response == ClaimResponse.NOT_CLAIMED) {
                        player.sendMessage(Utils.color("&cThat plot is not claimed by your Hamlet."));
                        return true;
                    } else if (response == ClaimResponse.NO_PERMISSION) {
                        player.sendMessage(Utils.color("&cYou do not have permission to unclaim for your Hamlet."));
                        return true;
                    } else if (response == ClaimResponse.UNKOWN_ERROR) {
                        player.sendMessage(Utils.color("&cThere was an unknown error processing your unclaim."));
                        return true;
                    } else if (response == ClaimResponse.SUCCESS) {
                        hamlet.sendMemberMessage("&t" + player.getName() + " &shas unclaimed the plot &t" + plot.getPlot().toString());
                        return true;
                    } else if (response == ClaimResponse.CLAIM_ACTOR_HIGHER) {
                        player.sendMessage(Utils.color("&cThe one who claimed that plot is of a higher rank than you."));
                        return true;
                    }
                } else if (Utils.checkCmdAliases(args, 0, "set", "s")) {
                    if (!(args.length > 1)) {
                        player.sendMessage(Utils.color("&cYou must provide a sub command."));
                        return true;
                    }
                    
                    if (Utils.checkCmdAliases(args, 1, "spawnpoint", "spawn", "s")) {
                        SpawnpointResponse response = hamlet.changeSpawnpoint(player);
                        if (response == SpawnpointResponse.NO_PERMISSION) {
                            player.sendMessage(Utils.color("&cYou do not have permissoin to change the spawnpoint"));
                            return true;
                        } else if (response == SpawnpointResponse.NOT_INT_CLAIM) {
                            player.sendMessage(Utils.color("&cThe spawnpoint must be in the Hamlet's claim."));
                            return true;
                        } else if (response == SpawnpointResponse.SUCCESS) {
                            Location spawn = hamlet.getSpawnpoint();
                            int x = spawn.getBlockX(), y = spawn.getBlockY(), z = spawn.getBlockZ();
                            hamlet.sendMemberMessage("&t" + player.getName() + " &shas changed the spawnpoint to &t(" + x + ", " + y + ", " + z + ")");
                        }
                    } else if (Utils.checkCmdAliases(args, 1, "privacy")) {
                        Privacy privacy;
                        try {
                            privacy = Privacy.valueOf(args[2].toUpperCase());
                        } catch (IndexOutOfBoundsException | NullPointerException e) {
                            player.sendMessage(Utils.color("&cYou must provide a value for the privacy type."));
                            return true;
                        } catch (IllegalArgumentException e) {
                            player.sendMessage(Utils.color("&cYou provided an invalid privacy value."));
                            return true;
                        }
                        
                        if (privacy.isDisabled()) {
                            player.sendMessage(Utils.color("&cThat privacy type is currently disabled because it is not yet implemented."));
                            return true;
                        }
                        
                        boolean success = hamlet.changePrivacy(privacy, player);
                        if (!success) {
                            player.sendMessage(Utils.color("&cYou do not have permission to change the privacy."));
                        } else {
                            hamlet.sendMemberMessage("&t" + player.getName() + " &shas changed the privacy to " + hamlet.getPrivacy().getDisplayName());
                        }
                        return true;
                    } else if (Utils.checkCmdAliases(args, 1, "name")) {
                        if (!(args.length > 2)) {
                            player.sendMessage(Utils.color("&cYou must provide a new name."));
                            return true;
                        }
                        
                        String name = StringUtils.join(args, " ", 2, args.length);
                        boolean success = hamlet.changeName(name, player);
                        if (!success) {
                            player.sendMessage(Utils.color("&cYou do not have permisson to change the Hamlet's name"));
                        } else {
                            hamlet.sendMemberMessage("&t" + player.getName() + " &shas changed the name to &t" + hamlet.getName());
                        }
                        return true;
                    }
                } else if (Utils.checkCmdAliases(args, 0, "invite", "i", "invites")) {
                    if (!(args.length > 1)) {
                        player.sendMessage(Utils.color("&cNot enough arguments"));
                        return true;
                    }
                    
                    if (Utils.checkCmdAliases(args, 1, "list", "l")) {
                        Set<Invite> invites = hamlet.getInvites();
                        Paginator<Invite> paginator = PaginatorFactory.generatePaginator(7, invites, new HashMap<>() {{
                            put(DefaultVariables.TYPE, hamlet.getName() + " Invites");
                            put(DefaultVariables.COMMAND, "hamlet invites list");
                        }});
                        
                        if (args.length == 2) {
                            paginator.display(sender, 1);
                        } else {
                            paginator.display(sender, args[2]);
                        }
                    } else if (Utils.checkCmdAliases(args, 1, "remove", "r")) {
                        if (!(args.length > 2)) {
                            player.sendMessage(Utils.color("&cYou must provide a player name."));
                            return true;
                        }
                        
                        RealmProfile targetProfile = profileManager.getProfile(args[2]);
                        if (targetProfile == null) {
                            player.sendMessage(Utils.color("&cCould not find a player by that name, or they have not joined yet."));
                            return true;
                        }
                        
                        InviteResponse response = hamlet.removeInvite(targetProfile.getUser().getUniqueId(), player.getUniqueId());
                        if (response == InviteResponse.NOT_INVITED) {
                            player.sendMessage(Utils.color("&cThat player has not been invited to the hamlet."));
                        } else if (response == InviteResponse.ACTOR_NO_PERMISSION) {
                            player.sendMessage(Utils.color("&cYou do not have permission to manage invites."));
                        } else if (response == InviteResponse.SUCCESS) {
                            player.sendMessage(Utils.color("&gYou have removed the invite for &h" + targetProfile.getUser().getLastName()));
                        }
                        return true;
                    } else {
                        if (memberLimit.getValue() > 0) {
                            int limitValue = hamlet.getLimitValue(memberLimit).intValue();
                            if (hamlet.getMembers().size() >= limitValue) {
                                player.sendMessage("&cYour hamlet has reached the maximum about of members allowed.");
                                return true;
                            }
                        }
                        
                        RealmProfile targetProfile = profileManager.getProfile(args[1]);
                        if (targetProfile == null) {
                            player.sendMessage(Utils.color("&cCould not find a player by that name, or they have not joined yet."));
                            return true;
                        }
                        
                        if (territoryManager.getTerritory(targetProfile.getUniqueId()) != null) {
                            player.sendMessage(Utils.color("&cThat player is a member of another hamlet."));
                            return true;
                        }
                        
                        InviteResponse response = hamlet.addInvite(targetProfile.getUser().getUniqueId(), player.getUniqueId());
                        if (response == InviteResponse.ACTOR_NO_PERMISSION) {
                            player.sendMessage(Utils.color("&cYou do not have permission to invite players."));
                        } else if (response == InviteResponse.ALREADY_INVITED) {
                            player.sendMessage(Utils.color("&cThat player has already been invited to the hamlet."));
                        } else if (response == InviteResponse.SUCCESS) {
                            targetProfile.sendMessage("&g" + player.getName() + " has invited you to the hamlet &h" + hamlet.getName());
                            hamlet.sendMemberMessage("&t" + player.getName() + " &shas invited &t" + targetProfile.getUser().getLastName() + " &sto the hamlet.");
                        }
                        return true;
                    }
                } else if (Utils.checkCmdAliases(args, 0, "transfer", "t")) {
                    if (!(args.length > 1)) {
                        player.sendMessage(Utils.color("&cYou must provide a target or subcommand."));
                        return true;
                    }
                    
                    if (Utils.checkCmdAliases(args, 1, "confirm")) {
                        hamlet.transfer(this.transferConfirm.get(player.getUniqueId()));
                        String line = Utils.blankLine("&6", Constants.LINE_CHARS);
                        hamlet.sendMemberMessage("", line, "&sThe Hamlet leadership has been transfered to &t" + hamlet.getLeader().getName(), line, "");
                    } else if (Utils.checkCmdAliases(args, 1, "cancel")) {
                        if (!this.transferConfirm.containsKey(player.getUniqueId())) {
                            player.sendMessage(Utils.color("&cYou do not have a pending transfer request."));
                        } else {
                            this.transferConfirm.remove(player.getUniqueId());
                            player.sendMessage(Utils.color("&gYou have cancelled the transfer request."));
                        }
                        return true;
                    } else {
                        if (!hamlet.getLeader().getUniqueId().equals(player.getUniqueId())) {
                            player.sendMessage(Utils.color("&cYou must be the leader of the hamlet to do that."));
                            return true;
                        }
                        RealmProfile target = profileManager.getProfile(args[1]);
                        if (target == null) {
                            player.sendMessage(Utils.color("&cCould not find a player by that name, or they have not joined yet."));
                            return true;
                        }
                        
                        Member member = hamlet.getMember(target.getUser().getUniqueId());
                        if (member == null) {
                            player.sendMessage(Utils.color("&cThat player is not a member of your Hamlet."));
                            return true;
                        }
                        
                        this.transferConfirm.put(player.getUniqueId(), target.getUser().getUniqueId());
                        String line = Utils.blankLine("&6", Constants.LINE_CHARS);
                        player.sendMessage("", line, Utils.getCenteredMessage("&gYou are about to transfer leadership of the hamlet to &h" + target.getUser().getLastName()), Utils.getCenteredMessage("&gYou will be set the rank of &hMEMBER&g, you can be &cremoved&g."), Utils.getCenteredMessage("&gAre you sure you want to do this?"), Utils.getCenteredMessage("&gTo confirm, run the command &a/hamlet transfer confirm"), Utils.getCenteredMessage("&gTo cancel, run the command &c/hamlet transfer cancel"), line, "");
                    }
                } else if (Utils.checkCmdAliases(args, 0, "members", "member")) {
                    if (!(args.length > 1)) {
                        player.sendMessage(Utils.color("&cUsage: /hamlet members <target>|list set|remove [args]"));
                        return true;
                    }
                    
                    if (Utils.checkCmdAliases(args, 1, "remove", "r")) {
                        RealmProfile target = profileManager.getProfile(args[2]);
                        if (target == null) {
                            player.sendMessage(Utils.color("&cCould not find a player by that name, or they have not joined yet."));
                            return true;
                        }
                        
                        MemberResponse response = hamlet.removeMember(target.getUser().getUniqueId(), player.getUniqueId());
                        if (response == MemberResponse.NOT_A_MEMBER) {
                            player.sendMessage(Utils.color("&cThat player is not a member of your Hamlet."));
                        } else if (response == MemberResponse.NO_PERMISSION) {
                            player.sendMessage(Utils.color("&cYou do not have permission to remove members from your Hamlet."));
                        } else if (response == MemberResponse.TARGET_HIGHER) {
                            player.sendMessage(Utils.color("&cYou cannot remove players that have the same rank or higher than you."));
                        } else if (response == MemberResponse.SUCCESS) {
                            player.sendMessage(Utils.color("&gYou have removed the player &h" + target.getUser().getLastName() + " &gfrom your hamlet."));
                        }
                        return true;
                    } else if (Utils.checkCmdAliases(args, 1, "list", "l")) {
                        Set<Member> members = hamlet.getMembers();
                        Paginator<Member> paginator = PaginatorFactory.generatePaginator(7, members, new HashMap<>() {{
                            put(DefaultVariables.TYPE, hamlet.getName() + " Members");
                            put(DefaultVariables.COMMAND, "hamlet members list");
                        }});
                        
                        if (args.length == 2) {
                            paginator.display(sender, 1);
                        } else {
                            paginator.display(sender, args[2]);
                        }
                    } else {
                        RealmProfile target = profileManager.getProfile(args[1]);
                        if (target == null) {
                            player.sendMessage(Utils.color("&cCould not find a player by that name, or they have not joined yet."));
                            return true;
                        }
                        
                        if (!(args.length > 4)) {
                            player.sendMessage(Utils.color("&cYou must provide a value to set."));
                            return true;
                        }
                        
                        if (Utils.checkCmdAliases(args, 2, "set", "s")) {
                            if (Utils.checkCmdAliases(args, 3, "rank", "r")) {
                                Rank rank;
                                try {
                                    rank = Rank.valueOf(args[4].toUpperCase());
                                } catch (IndexOutOfBoundsException | NullPointerException e) {
                                    player.sendMessage(Utils.color("&cYou must provide a rank name."));
                                    return true;
                                } catch (IllegalArgumentException e) {
                                    player.sendMessage(Utils.color("&cYou provided an invalid value for the rank."));
                                    return true;
                                }
                                
                                if (rank == Rank.LEADER) {
                                    player.sendMessage(Utils.color("&cThe Leader rank can only be assigned through the /hamlet transfer command."));
                                    return true;
                                }
                                
                                RankResponse response = hamlet.changeMemberRank(target.getUser().getUniqueId(), player.getUniqueId(), rank);
                                if (response == RankResponse.NOT_A_MEMBER) {
                                    player.sendMessage(Utils.color("&cThat player is not a member of your Hamlet."));
                                } else if (response == RankResponse.TARGET_MEMBER_HIGHER) {
                                    player.sendMessage(Utils.color("&cThat player's current rank is equal to or higher than yours."));
                                } else if (response == RankResponse.NEW_RANK_HIGHER_THAN_ACTOR) {
                                    player.sendMessage(Utils.color("&cThe new rank is equal to or higher than yours."));
                                } else if (response == RankResponse.SUCCESS) {
                                    player.sendMessage(Utils.color("&gYou have changed &h" + target.getUser().getLastName() + "&g's rank to &h" + rank.name()));
                                }
                            }
                        }
                        
                        return true;
                    }
                } else if (Utils.checkCmdAliases(args, 0, "spawn")) {
                    player.teleport(hamlet.getSpawnpoint());
                    player.sendMessage(Utils.color("&gTeleported to the Hamlet's spawnpoint."));
                } else if (Utils.checkCmdAliases(args, 0, "disband")) {
                    Territory territory = territoryManager.getTerritory(player);
                    if (territory == null) {
                        player.sendMessage(Utils.color("&cYou are not a member of a Hamlet."));
                        return true;
                    }
                    
                    if (!territory.getLeader().getRealmProfile().getUniqueId().equals(player.getUniqueId())) {
                        player.sendMessage(Utils.color("&cYou must be the leader of the Hamlet in order to disband it."));
                        return true;
                    }
                    
                    if (args.length == 1) {
                        if (this.disbandConfirm.contains(player.getUniqueId())) {
                            player.sendMessage(Utils.color("&cPlease use the /hamlet disband confirm|cancel command to continue."));
                            return true;
                        }
                        this.disbandConfirm.add(player.getUniqueId());
                        String line = Utils.blankLine("&4", Constants.LINE_CHARS);
                        player.sendMessage("", line, Utils.getCenteredMessage("&gYou are about to disband the hamlet &h" + territory.getName()), Utils.getCenteredMessage("&gAll current claims and progress will be &4lost &gand cannot be restored"), //Add more as more is implemented
                                Utils.getCenteredMessage("&gAre you sure you want to do this?"), Utils.getCenteredMessage("&gTo confirm, run the command &a/hamlet disband confirm"), Utils.getCenteredMessage("&gTo cancel, run the command &c/hamlet disband cancel"), line, "");
                    } else {
                        if (Utils.checkCmdAliases(args, 1, "confirm")) {
                            if (!this.disbandConfirm.contains(player.getUniqueId())) {
                                player.sendMessage(Utils.color("&cYou must use the command /hamlet disband before you do that."));
                                return true;
                            }
                            
                            territory.disband();
                            this.disbandConfirm.remove(player.getUniqueId());
                            territoryManager.removeTerritory(territory.getUniqueId());
                            Bukkit.broadcastMessage(Utils.color("&d" + player.getName() + " has disbanded the hamlet " + hamlet.getName()));
                        } else if (Utils.checkCmdAliases(args, 1, "cancel")) {
                            if (!this.disbandConfirm.contains(player.getUniqueId())) {
                                player.sendMessage(Utils.color("&cYou must use the command /hamlet disband before you do that."));
                                return true;
                            }
                            
                            this.disbandConfirm.remove(player.getUniqueId());
                            player.sendMessage(Utils.color("&gHamlet disbanding has been cancelled."));
                        }
                    }
                } else if (Utils.checkCmdAliases(args, 0, "teleport", "tp")) {
                    Territory territory = territoryManager.getTerritory(player);
                    if (territory == null) {
                        player.sendMessage(Utils.color("&cYou must be a part of a hamlet to do that."));
                        return true;
                    }
                    
                    Member member = territory.getMember(player.getUniqueId());
                    
                    if (!territory.contains(player.getLocation())) {
                        player.sendMessage(Utils.color("&cYou must be within the hamlet claim to teleport to hamlet members."));
                        return true;
                    }
                    
                    if (!(args.length > 1)) {
                        player.sendMessage(Utils.color("&cYou must provide a target."));
                        return true;
                    }
                    
                    //TODO Move these checks to the territory class, with the response enum like the other commands
                    
                    RealmProfile targetProfile = profileManager.getProfile(args[1]);
                    if (targetProfile == null) {
                        player.sendMessage(Utils.color("&cCould not find a player with that name, or they have not joined yet."));
                        return true;
                    }
                    
                    Member targetMember = territory.getMember(targetProfile.getUniqueId());
                    if (targetMember == null) {
                        player.sendMessage(Utils.color("&cThat player is not a member of your hamlet."));
                        return true;
                    }
                    
                    if (!targetMember.getRealmProfile().getUser().isOnline()) {
                        player.sendMessage(Utils.color("&cYou cannot teleport to an offline member.")); //Donor perk?
                        return true;
                    }
                    
                    if (targetMember.getRank().getOrder() < member.getRank().getOrder()) {
                        player.sendMessage(Utils.color("&cYou must be of the same rank or higher to teleport to that member.")); //TPA stuff
                        return true;
                    }
                    
                    if (!territory.contains(targetMember.getLocation())) {
                        player.sendMessage(Utils.color("&cYou can only teleport to other members if they are in your hamlet's claim."));
                        return true;
                    }
                    
                    player.teleport(targetMember.getLocation());
                    player.sendMessage(Utils.color("&gTeleported to &h" + targetMember.getName()));
                    targetMember.sendMessage("&g" + player.getName() + " teleported to you."); //Toggleable setting, can be overridden if a higher rank
                } else if (Utils.checkCmdAliases(args, 0, "leave")) {
                    hamlet.removeMember(player.getUniqueId(), null);
                    hamlet.sendMemberMessage("&s" + player.getName() + " &thas left your hamlet.");
                    player.sendMessage("&gYou have left the hamlet &h" + hamlet.getName());
                } else if (Utils.checkCmdAliases(args, 0, "bank")) {
                    Territory territory = territoryManager.getTerritory(player);
                    if (territory == null) {
                        player.sendMessage(Utils.color("&cYou must be a part of a hamlet to do that."));
                        return true;
                    }
                    
                    Member member = territory.getMember(player.getUniqueId());
                    
                    if (!(args.length > 1)) {
                        member.sendMessage("&cYou must provide a subcommand.");
                        return true;
                    }
                    
                    if (Utils.checkCmdAliases(args, 1, "deposit", "withdraw")) {
                        if (!(args.length > 2)) {
                            member.sendMessage("&cYou must provide an amount.");
                            return true;
                        }
                        
                        double amount;
                        try {
                            amount = Double.parseDouble(args[2]);
                        } catch (NumberFormatException e) {
                            member.sendMessage("&cThe amount you provided is not a valid number.");
                            return true;
                        }
                        
                        if (Utils.checkCmdAliases(args, 1, "deposit")) {
                            Pair<EconomyResponse, EconomyResponse> responses = plugin.getEconomyManager().getTransactionHandler().transfer(player, amount, player.getAccount(), hamlet.getAccount(), "Transfer from " + player.getName() + " to " + hamlet.getName() + "'s Account.");
                            EconomyResponse fromResponse = responses.getValue1();
                            EconomyResponse toResponse = responses.getValue2();
                            
                            if (!(fromResponse == EconomyResponse.SUCCESS) || !(toResponse == EconomyResponse.SUCCESS)) {
                                if (fromResponse == EconomyResponse.NOT_ENOUGH_FUNDS) {
                                    member.sendMessage("&cYou do not have enough coins in your account to deposit to your hamlet.");
                                }
                                
                                if (toResponse == EconomyResponse.NOT_ENOUGH_PERMISSION) {
                                    member.sendMessage("&cYou do not have enough permission in your hamlet to deposit to your hamlet.");
                                }
                                
                                return true;
                            }
                            
                            if (fromResponse == EconomyResponse.NOT_ENOUGH_FUNDS) {
                                member.sendMessage("&cYou do not have enough coins in your account to deposit to your hamlet.");
                                return true;
                            }
                            
                            if (toResponse == EconomyResponse.NOT_ENOUGH_PERMISSION) {
                                member.sendMessage("&cYou do not have enough permission in your hamlet to deposit to your hamlet.");
                                return true;
                            }
                            
                            hamlet.sendMemberMessage("&h" + member.getName() + " &ghas deposited &h" + amount + " &ginto the hamlet bank.");
                        } else if (Utils.checkCmdAliases(args, 1, "withdraw")) {
                            Pair<EconomyResponse, EconomyResponse> responses = plugin.getEconomyManager().getTransactionHandler().transfer(player, amount, hamlet.getAccount(), player.getAccount(), "Transfer from " + hamlet.getName() + " to " + player.getName() + "'s Account.");
                            EconomyResponse fromResponse = responses.getValue1();
                            EconomyResponse toResponse = responses.getValue2();
                            
                            if (fromResponse != EconomyResponse.SUCCESS && toResponse != EconomyResponse.SUCCESS) {
                                if (fromResponse == EconomyResponse.NOT_ENOUGH_FUNDS) {
                                    member.sendMessage("&cThe hamlet does not have enough funds for you to withdraw that amount.");
                                }
                                
                                if (toResponse == EconomyResponse.NOT_ENOUGH_PERMISSION) {
                                    member.sendMessage("&cYou do not have enough permission in your hamlet to withdraw from your hamlet.");
                                }
                                
                                return true;
                            }
                            
                            hamlet.sendMemberMessage("&h" + member.getName() + " &ghas withdrawn &h" + amount + " &gfrom the hamlet bank.");
                        }
                    } else if (Utils.checkCmdAliases(args, 1, "balance", "bal")) {
                        member.sendMessage("&g" + hamlet.getName() + "'s balance: &h" + hamlet.getAccount().getBalance());
                    }
                } else if (Utils.checkCmdAliases(args, 0, "warp")) {
                    if (!(args.length > 0)) {
                        player.sendMessage("&cYou must provide a warp name or the set subcommand.");
                        return true;
                    }
                    
                    Member member = hamlet.getMember(player.getUniqueId());
                    
                    Collection<TerritoryWarp> territoryWarps = plugin.getWarpManager().getTerritoryWarps(hamlet);
                    if (args.length == 1) {
                        StringBuilder sb = new StringBuilder();
                        for (TerritoryWarp warp : territoryWarps) {
                            sb.append(warp.getName()).append(" ");
                        }
                        
                        player.sendMessage("&sHamlet Warps: &f" + sb.toString());
                        return true;
                    }
                    
                    if (Utils.checkCmdAliases(args, 1, "set", "s")) {
                        if (!(args.length > 2)) {
                            player.sendMessage("&cYou must provide a warp name");
                            return true;
                        }
                        
                        if (!hamlet.contains(player.getLocation())) {
                            player.sendMessage("&cWarps can only exist within the hamlet claim.");
                            return true;
                        }
                        
                        TerritoryWarp exisiting = plugin.getWarpManager().getTerritoryWarp(hamlet, args[2]);
                        if (exisiting != null) {
                            if (!exisiting.canAccess(player.getUniqueId())) {
                                player.sendMessage("&cYou cannot access that warp.");
                                return true;
                            }
                            
                            if (member.getRank().getOrder() > Rank.MANAGER.getOrder()) {
                                player.sendMessage("&cOnly managers and higher can modify the location of a warp.");
                                return true;
                            }
                            
                            exisiting.setLocation(player.getLocation());
                            hamlet.sendMemberMessage("&sThe warp &t" + exisiting.getName() + "&s's location has been changed by &t" + player.getName());
                            return true;
                        }
                        
                        if (member.getRank().getOrder() > Rank.TRUSTED.getOrder()) {
                            player.sendMessage("&cOnly those of the trusted rank or higher can create warps.");
                            return true;
                        }
                        
                        IntegerLimit warpLimit = (IntegerLimit) plugin.getLimitsManager().getLimit("territory_warp_limit");
                        if (warpLimit != null) {
                            if (warpLimit.getValue() > 0) {
                                if (territoryWarps.size() >= hamlet.getLimitValue(warpLimit).intValue()) {
                                    player.sendMessage("&cYour hamlet has reached the maximum amount of warps allowed.");
                                    return true;
                                }
                            }
                        }
                        
                        TerritoryWarp territoryWarp = new TerritoryWarp(hamlet, args[2], player.getLocation());
                        plugin.getWarpManager().addWarp(territoryWarp);
                        hamlet.sendMemberMessage("&sA new warp named &t" + territoryWarp.getName() + " &shas been created by &t" + player.getName());
                        return true;
                    }
                    
                    TerritoryWarp territoryWarp = plugin.getWarpManager().getTerritoryWarp(hamlet, args[1]);
                    if (territoryWarp == null) {
                        player.sendMessage("&cThe name you provided did not match a warp name.");
                        return true;
                    }
                    
                    if (args.length == 2) {
                        if (territoryWarp.getMinRank().getOrder() < member.getRank().getOrder()) {
                            player.sendMessage("&cYou cannot use that warp because your rank is not high enough.");
                            return true;
                        }
                        
                        player.teleport(territoryWarp);
                        player.sendMessage("&sTeleported you to &t" + territoryWarp.getName());
                        return true;
                    }
                    
                    if (Utils.checkCmdAliases(args, 2, "delete")) {
                        if (member.getRank().getOrder() > Rank.MANAGER.getOrder()) {
                            player.sendMessage("&cYour rank is not high enough to manage warps.");
                            return true;
                        }
                        
                        plugin.getWarpManager().removeWarp(territoryWarp);
                        hamlet.sendMemberMessage("&sThe warp &t" + territoryWarp.getName() + " &swas removed by &t" + player.getName());
                    } else if (Utils.checkCmdAliases(args, 2, "modify")) {
                        player.sendMessage("&cThat command is not implemented yet.");
                    }
                } else if (Utils.checkCmdAliases(args, 0, "chat", "channel", "c")) {
                    Channel channel = hamlet.getChannel();
                    if (args.length == 1) {
                        if (!channel.isParticipant(player)) {
                            channel.addParticipant(player.getUniqueId(), Role.MEMBER);
                        }
                        player.setChannelFocus(channel);
                        player.sendMessage("&gSet your channel focus to " + channel.getDisplayName());
                    } else if (Utils.checkCmdAliases(args, 1, "mute", "role")) {
                        Participant participant = channel.getParticipant(player.getUniqueId());
                        if (participant.getRole().getOrder() > Role.MANAGER.getOrder()) {
                            player.sendMessage("&cYou are not allowed to manage the channel.");
                            return true;
                        }
                        
                        if (!(args.length > 2)) {
                            player.sendMessage(Utils.color("&cYou must provide a player name."));
                            return true;
                        }
                        
                        RealmProfile target = plugin.getProfileManager().getProfile(args[2]);
                        if (target == null) {
                            player.sendMessage("&cThe name you provided does not match a player.");
                            return true;
                        }
                        
                        Participant targetParticipant = channel.getParticipant(target.getUniqueId());
                        if (targetParticipant == null) {
                            player.sendMessage("&cThat player is not a participant of the channel.");
                            return true;
                        }
                        
                        if (targetParticipant.getRole().getOrder() <= participant.getRole().getOrder()) {
                            player.sendMessage("&cThat player has a equal or higher role than yours, you cannot manage them.");
                            return true;
                        }
                        
                        if (Utils.checkCmdAliases(args, 1, "mute")) {
                            channel.mute(target.getUniqueId(), player.getUniqueId());
                            player.sendMessage("&gYou have muted &h" + target.getName() + " &gfrom the channel.");
                            target.sendMessage("&gYou have been muted in the " + channel.getDisplayName() + " &gchannel by &h" + player.getName());
                        } else if (Utils.checkCmdAliases(args, 1, "role")) {
                            if (!(args.length > 3)) {
                                player.sendMessage("&cYou must provide a role name.");
                                return true;
                            }
                            
                            Role role;
                            try {
                                role = Role.valueOf(args[3].toUpperCase());
                            } catch (IllegalArgumentException e) {
                                player.sendMessage("&cThat is not a valid role name.");
                                return true;
                            }
                            
                            if (role.getOrder() <= participant.getRole().getOrder()) {
                                player.sendMessage("&cThe new role is equal to or higher than your role.");
                                return true;
                            }
                            
                            targetParticipant.setRole(role);
                            player.sendMessage("&gYou have set &h" + target.getName() + "&g's role to &h" + role.name());
                            target.sendMessage("&gYour role in the " + channel.getDisplayName() + " &gchannel has been changed to &h" + role.name());
                        } else {
                            player.sendMessage("&cInvalid subcommand.");
                        }
                    } else {
                        String message = StringUtils.join(args, " ", 1, args.length);
                        if (StringUtils.isEmpty(message)) {
                            player.sendMessage("&cYou must provide a message");
                            return true;
                        }
                        
                        channel.sendMessage(player.getUniqueId(), message);
                    }
                } else if (Utils.checkCmdAliases(args, 0, "relations", "relation")) {
                    if (!(args.length > 1)) {
                        player.sendMessage("&cYou must provide a sub command.");
                        return true;
                    }
                    
                    if (Utils.checkCmdAliases(args, 1, "list", "l")) {
                        player.sendMessage("&6" + hamlet.getName() + "'s relationships with others.");
                        hamlet.getRelationships().forEach((t, relation) -> {
                            Hamlet other = (Hamlet) territoryManager.getTerritory(t);
                            String line = " &8- &f" + other.getName() + " is an ";
                            if (relation == Relation.ALLY) {
                                line += "&a";
                            } else if (relation == Relation.ENEMY) {
                                line += "&c";
                            }
                            line += relation.name();
                            player.sendMessage(line);
                        });
                        return true;
                    }
                    
                    Member member = hamlet.getMember(player.getUniqueId());
                    if (!(args.length > 2)) {
                        sender.sendMessage(Utils.color("&cYou must provide more arguments"));
                        return true;
                    }
                    
                    if (!(member.getRank().getOrder() <= Rank.MANAGER.getOrder())) {
                        player.sendMessage("&cYou must be a manager or higher to modify relationships");
                        return true;
                    }
                    
                    String name = StringUtils.join(args, "_", 2, args.length);
                    Territory territory = territoryManager.getTerritory(name);
                    if (territory == null) {
                        player.sendMessage("&cYou provided an invalid territory name.");
                        return true;
                    }
                    
                    if (!(territory instanceof Hamlet)) {
                        player.sendMessage("&cThe territory name you provided did not match a Hamlet.");
                        return true;
                    }
                    
                    if (Utils.checkCmdAliases(args, 1, "ally", "a")) {
                        if (territory.getRelationship(hamlet) == Relation.ENEMY) {
                            player.sendMessage("&cThat hamlet has set your hamlet as their enemy.");
                            return true;
                        }
                        hamlet.setRelationship(territory, Relation.ALLY);
                        hamlet.sendMemberMessage("&t" + territory.getName() + " &shas been added as an ally of the Hamlet.");
                        territory.sendMemberMessage("&t" + hamlet.getName() + " &shas set you as their ally.");
                    } else if (Utils.checkCmdAliases(args, 1, "enemy", "e")) {
                        hamlet.setRelationship(territory, Relation.ENEMY);
                        territory.setRelationship(hamlet, Relation.ENEMY);
                        hamlet.sendMemberMessage("&sYou are now enemies with &t" + territory.getName());
                        territory.sendMemberMessage("&sYou are not enemies with &t" + hamlet.getName());
                    } else if (Utils.checkCmdAliases(args, 1, "remove", "r")) {
                        hamlet.removeRelationship(territory);
                        hamlet.sendMemberMessage("&sYou are now neutral with &t" + territory.getName());
                    }
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("ally")) {
            if (!(args.length > 1)) {
                sender.sendMessage(Utils.color("&cYou must provide more arguments"));
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 0, "spawn", "s")) {
                Hamlet hamlet = (Hamlet) territoryManager.getTerritory(player);
                String name = StringUtils.join(args, " ", 1, args.length);
                Territory territory = territoryManager.getTerritory(name);
                if (territory == null) {
                    player.sendMessage("&cYou did not provide a valid territory name.");
                    return true;
                }
                
                if (!(territory instanceof Hamlet)) {
                    player.sendMessage("&cThe territory name you provided did not match a Hamlet.");
                    return true;
                }
                
                if (!(hamlet.getRelationship(territory) == Relation.ALLY && territory.getRelationship(hamlet) == Relation.ALLY)) {
                    player.sendMessage("&cYou cannot teleport to that Hamlet spawn because the relationship is not mutual.");
                    return true;
                }
                
                player.teleport(territory.getSpawnpoint());
                player.sendMessage("&sYou teleported to &t" + territory.getName() + "&s's spawnpoint.");
            }
        }
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }
        
        TerritoryManager territoryManager = plugin.getTerritoryManager();
        
        List<String> possibleResults = new LinkedList<>();
        int resultArg = 0;
        if (cmd.getName().equalsIgnoreCase("hamlets")) {
            if (args.length == 1) {
                possibleResults.addAll(Arrays.asList("relations", "chat", "channel", "warp", "create", "join", "accept", "deny", "leave", "invite", "claim", "unclaim", "set", "members", "spawn", "teleport", "disband", "bank"));
            } else if (args.length == 2) {
                resultArg = 1;
                if (Utils.checkCmdAliases(args, 0, "set", "s")) {
                    possibleResults.addAll(Arrays.asList("spawnpoint", "privacy", "name"));
                } else if (Utils.checkCmdAliases(args, 0, "teleport", "tp")) {
                    Hamlet hamlet = (Hamlet) territoryManager.getTerritory((Player) sender);
                    Member playerMember = hamlet.getMember(((Player) sender).getUniqueId());
                    for (Member member : hamlet.getMembers()) {
                        if (member.getRealmProfile().getUser().isOnline()) {
                            if (member.getRank().getOrder() >= playerMember.getRank().getOrder()) {
                                possibleResults.add(member.getName());
                            }
                        }
                    }
                } else if (Utils.checkCmdAliases(args, 0, "members")) {
                    possibleResults.addAll(Arrays.asList("list", "remove"));
                    for (Member member : territoryManager.getTerritory((Player) sender).getMembers()) {
                        possibleResults.add(member.getName());
                    }
                } else if (Utils.checkCmdAliases(args, 0, "invites", "invite")) {
                    possibleResults.addAll(Arrays.asList("list", "remove"));
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (territoryManager.getTerritory(player) == null) {
                            possibleResults.add(player.getName());
                        }
                    }
                } else if (Utils.checkCmdAliases(args, 0, "bank")) {
                    possibleResults.addAll(Arrays.asList("deposit", "withdraw", "balance"));
                } else if (Utils.checkCmdAliases(args, 0, "warp")) {
                    possibleResults.addAll(Arrays.asList("set", "delete"));
                } else if (Utils.checkCmdAliases(args, 0, "channel", "chat", "c")) {
                    possibleResults.addAll(Arrays.asList("mute", "role"));
                } else if (Utils.checkCmdAliases(args, 0, "relations", "relation")) {
                    possibleResults.addAll(Arrays.asList("list", "ally", "enemy", "remove"));
                }
            } else if (args.length == 3) {
                resultArg = 2;
                if (Utils.checkCmdAliases(args, 1, "privacy")) {
                    for (Privacy privacy : Privacy.values()) {
                        if (!privacy.isDisabled()) {
                            possibleResults.add(privacy.name());
                        }
                    }
                } else if (Utils.checkCmdAliases(args, 1, "remove")) {
                    if (Utils.checkCmdAliases(args, 0, "members")) {
                        Hamlet hamlet = (Hamlet) territoryManager.getTerritory((Player) sender);
                        Member playerMember = hamlet.getMember(((Player) sender).getUniqueId());
                        for (Member member : hamlet.getMembers()) {
                            if (member.getRank().getOrder() >= playerMember.getRank().getOrder()) {
                                possibleResults.add(member.getName());
                            }
                        }
                    } else if (Utils.checkCmdAliases(args, 0, "invites", "invite")) {
                        Hamlet hamlet = (Hamlet) territoryManager.getTerritory((Player) sender);
                        for (Invite invite : hamlet.getInvites()) {
                            User user = plugin.getUserManager().getUser(invite.getTarget());
                            possibleResults.add(user.getLastName());
                        }
                    }
                } else if (Utils.checkCmdAliases(args, 0, "warp")) {
                    if (Utils.checkCmdAliases(args, 1, "delete", "set")) {
                        Hamlet hamlet = ((Hamlet) plugin.getTerritoryManager().getTerritory((Player) sender));
                        if (hamlet.getMember(((Player) sender).getUniqueId()).getRank().getOrder() <= Rank.MANAGER.getOrder()) {
                            Collection<TerritoryWarp> warps = plugin.getWarpManager().getTerritoryWarps(hamlet);
                            for (TerritoryWarp warp : warps) {
                                possibleResults.add(warp.getName());
                            }
                        }
                    }
                } else if (Utils.checkCmdAliases(args, 0, "channel", "chat", "c")) {
                    RealmProfile profile = plugin.getProfileManager().getProfile(sender);
                    if (Utils.checkCmdAliases(args, 1, "mute", "role")) {
                        Hamlet hamlet = (Hamlet) plugin.getTerritoryManager().getTerritory(profile);
                        if (hamlet != null) {
                            Participant participant = hamlet.getChannel().getParticipant(profile.getUniqueId());
                            for (Participant p : hamlet.getChannel().getParticipants()) {
                                if (p.getUniqueId().equals(participant.getUniqueId())) { continue; }
                                
                                if (p.getRole().getOrder() > participant.getRole().getOrder()) {
                                    possibleResults.add(p.getProfile().getName());
                                }
                            }
                        }
                    }
                }
            } else if (args.length == 4) {
                resultArg = 3;
                if (Utils.checkCmdAliases(args, 0, "channel", "chat", "c")) {
                    if (Utils.checkCmdAliases(args, 1, "role")) {
                        if (StringUtils.isNotEmpty(args[2])) {
                            for (Role role : Role.values()) {
                                if (role != Role.SERVER_STAFF || role != Role.BANNED && role != Role.MUTED) {
                                    possibleResults.add(role.name());
                                }
                            }
                        }
                    }
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("ally")) {
            if (args.length == 1) {
                resultArg = 0;
                possibleResults.add("spawn");
            }
        }
        
        return new ArrayList<>(getResults(args[resultArg], possibleResults));
    }
}