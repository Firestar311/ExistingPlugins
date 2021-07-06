package com.kingrealms.realms.cmd;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.ServerMode;
import com.kingrealms.realms.api.events.HamletJoinEvent;
import com.kingrealms.realms.entities.CustomEntities;
import com.kingrealms.realms.entities.type.ICustomEntity;
import com.kingrealms.realms.items.CustomItem;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.items.gui.ItemGuiMain;
import com.kingrealms.realms.kits.Kit;
import com.kingrealms.realms.kits.KitTier;
import com.kingrealms.realms.limits.group.LimitGroup;
import com.kingrealms.realms.limits.limit.*;
import com.kingrealms.realms.plot.Plot;
import com.kingrealms.realms.plot.claimed.ClaimedPlot;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.serverclaim.ServerClaim;
import com.kingrealms.realms.serverclaim.spawn.Spawn;
import com.kingrealms.realms.serverclaim.warzone.Warzone;
import com.kingrealms.realms.skills.farming.CropBlock;
import com.kingrealms.realms.skills.mining.MysticalBlock;
import com.kingrealms.realms.spawners.CustomSpawner;
import com.kingrealms.realms.spawners.MobStack;
import com.kingrealms.realms.territory.base.Territory;
import com.kingrealms.realms.territory.base.member.Member;
import com.kingrealms.realms.territory.enums.Privacy;
import com.kingrealms.realms.territory.enums.Rank;
import com.kingrealms.realms.territory.medievil.Hamlet;
import com.kingrealms.realms.whitelist.Whitelist;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.pagination.*;
import com.starmediadev.lib.region.Cuboid;
import com.starmediadev.lib.region.Selection;
import com.starmediadev.lib.user.User;
import com.starmediadev.lib.util.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class RealmsCommand extends BaseCommand {
    
    private final Realms plugin = Realms.getInstance();
    
    @SuppressWarnings("DuplicatedCode")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender.hasPermission("realms.admin"))) {
            sender.sendMessage(Utils.color("&cYou do not have permission to use that command."));
            return true;
        }
        
        if (!(args.length > 0)) {
            sender.sendMessage(Utils.color("&cYou must provide a subcommand."));
            return true;
        }
        
        RealmProfile profile = plugin.getProfileManager().getProfile(sender);
        
        if (Utils.checkCmdAliases(args, 0, "hamlet", "hamlets", "h")) {
            if (!(args.length > 1)) {
                profile.sendMessage(Utils.color("&cYou must provide more arguments."));
                return true;
            }
            
            if (!profile.hasPermission("realms.admin.hamlets")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 1, "list", "l")) {
                Set<Territory> territories = plugin.getTerritoryManager().getTerritories();
                Paginator<Territory> paginator = PaginatorFactory.generatePaginator(7, territories, new HashMap<>() {{
                    put(DefaultVariables.TYPE, " Hamlets");
                    put(DefaultVariables.COMMAND, "realms hamlets list");
                }});
                
                if (!profile.hasPermission("realms.admin.hamlets.list")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                
                if (args.length > 2) {
                    paginator.display(sender, args[2]);
                } else {
                    paginator.display(sender, 1);
                }
            } else if (Utils.checkCmdAliases(args, 1, "info", "i")) {
                if (!(args.length > 2)) {
                    profile.sendMessage(Utils.color("&cYou must provide a hamlet name."));
                    return true;
                }
                
                if (!profile.hasPermission("realms.admin.hamlets.info")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                
                String name = args[2].replace("_", " ");
                
                Hamlet hamlet = (Hamlet) plugin.getTerritoryManager().getTerritory(name);
                if (hamlet == null) {
                    profile.sendMessage(Utils.color("&cThe name you provided does not match a hamlet."));
                    return true;
                }
                
                profile.sendMessage(Utils.color("&dHamlet " + hamlet.getName() + "'s Information"));
                profile.sendMessage(Utils.color("&iID: &j" + hamlet.getUniqueId()));
                profile.sendMessage(Utils.color("&iLeader: &j" + hamlet.getLeader().getName()));
                int x = hamlet.getSpawnpoint().getBlockX(), y = hamlet.getSpawnpoint().getBlockY(), z = hamlet.getSpawnpoint().getBlockZ();
                profile.sendMessage(Utils.color("&iSpawnpoint: &j" + x + ", " + y + ", " + z));
                profile.sendMessage(Utils.color("&iClaimed Plots: &j" + hamlet.getPlots().size() + "/" + plugin.getLimitsManager().getLimit("territory_claim_limit").getValue()));
                profile.sendMessage(Utils.color("&iPrivacy: &j" + hamlet.getPrivacy().getDisplayName()));
                if (hamlet.getPrivacy() != Privacy.OPEN) {
                    profile.sendMessage(Utils.color("&iActive Invites: &j" + hamlet.getInvites().size()));
                }
                profile.sendMessage(Utils.color("&iMembers: &j" + hamlet.getMembers().size()));
                profile.sendMessage(Utils.color("&iCreated: &j") + Constants.DATE_FORMAT.format(new Date(hamlet.getCreatedDate())));
                profile.sendMessage(Utils.color("&iAccount: &j" + hamlet.getAccount().getAccountNumber()));
            } else if (Utils.checkCmdAliases(args, 1, "modify")) {
                if (!(args.length > 2)) {
                    profile.sendMessage(Utils.color("&cYou must provide more arguments."));
                    return true;
                }
                
                if (!profile.hasPermission("realms.admin.hamlets.modify")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                
                String name = args[2].replace("_", " ");
                
                Hamlet hamlet = (Hamlet) plugin.getTerritoryManager().getTerritory(name);
                if (hamlet == null) {
                    profile.sendMessage(Utils.color("&cThe name you provided does not match a hamlet."));
                    return true;
                }
                
                if (Utils.checkCmdAliases(args, 3, "members")) {
                    if (!profile.hasPermission("realms.admin.hamlets.modify.members")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    if (!(args.length > 5)) {
                        profile.sendMessage(Utils.color("&cNot enough arguments"));
                        return true;
                    }
                    
                    if (Utils.checkCmdAliases(args, 4, "add")) {
                        if (!profile.hasPermission("realms.admin.hamlets.modify.members.add")) {
                            profile.sendMessage("&cYou do not have permission to use that command.");
                            return true;
                        }
                        RealmProfile target = plugin.getProfileManager().getProfile(args[5]);
                        if (target == null) {
                            profile.sendMessage(Utils.color("&cThe name you provided did not match a valid player."));
                            return true;
                        }
                        
                        Territory territory = plugin.getTerritoryManager().getTerritory(target.getUniqueId());
                        if (territory != null) {
                            if (territory.getLeader().getRealmProfile().getUniqueId().equals(target.getUniqueId())) {
                                profile.sendMessage(Utils.color("&cYou cannot do that as that player is the leader of another hamlet."));
                                return true;
                            }
                            
                            territory.removeMember(target.getUniqueId());
                            plugin.addAuditEntry(profile.getName() + " removed " + target.getName() + " from the territory " + territory.getUniqueId() + " as a result of adding to another territory.");
                        }
                        hamlet.addMember(target.getUniqueId());
                        plugin.addAuditEntry(profile.getName() + " added " + target.getName() + " to the territory " + hamlet.getUniqueId());
                        profile.sendMessage("&iAdded &j" + target.getName() + " &ito &j" + hamlet.getName());
                        Bukkit.getPluginManager().callEvent(new HamletJoinEvent(target, hamlet));
                    } else if (Utils.checkCmdAliases(args, 4, "remove")) {
                        if (!profile.hasPermission("realms.admin.hamlets.modify.members.remove")) {
                            profile.sendMessage("&cYou do not have permission to use that command.");
                            return true;
                        }
                        RealmProfile target = plugin.getProfileManager().getProfile(args[5]);
                        if (target == null) {
                            profile.sendMessage(Utils.color("&cThe name you provided did not match a valid player."));
                            return true;
                        }
                        
                        if (!hamlet.isMember(target)) {
                            profile.sendMessage(Utils.color("&cThat player is not a member of that hamlet."));
                            return true;
                        }
                        
                        hamlet.removeMember(target.getUniqueId());
                        plugin.addAuditEntry(profile.getName() + " removed " + target.getName() + " from the territory " + hamlet.getUniqueId());
                        profile.sendMessage(Utils.color("&iRemoved &j" + target.getName() + " &ifrom &j" + hamlet.getName()));
                    } else if (Utils.checkCmdAliases(args, 4, "setrank", "sr")) {
                        if (!profile.hasPermission("realms.admin.hamlets.modify.members.setrank")) {
                            profile.sendMessage("&cYou do not have permission to use that command.");
                            return true;
                        }
                        RealmProfile target = plugin.getProfileManager().getProfile(args[5]);
                        if (target == null) {
                            profile.sendMessage(Utils.color("&cThe name you provided did not match a valid player."));
                            return true;
                        }
                        
                        Rank rank;
                        try {
                            rank = Rank.valueOf(args[6].toUpperCase());
                        } catch (ArrayIndexOutOfBoundsException e) {
                            profile.sendMessage(Utils.color("&cYou must provide a rank argument."));
                            return true;
                        } catch (IllegalArgumentException e) {
                            profile.sendMessage(Utils.color("&cInvalid rank: " + args[5]));
                            return true;
                        }
                        
                        if (rank == Rank.LEADER) {
                            profile.sendMessage(Utils.color("&cYou cannot set the leader rank directly. Use /realms hamlet modify " + hamlet.getUniqueId() + " set leader <name>"));
                            return true;
                        }
                        
                        hamlet.getMember(target.getUniqueId()).setRank(rank);
                        plugin.addAuditEntry(profile.getName() + " set " + target.getName() + "'s rank in the territory " + hamlet.getUniqueId() + " to " + rank.name());
                        profile.sendMessage(Utils.color("&iSet &j" + target.getName() + "&i's rank to &j" + rank.name() + " &iin hamlet &j" + hamlet.getName()));
                    }
                } else if (Utils.checkCmdAliases(args, 3, "claim")) {
                    if (!(sender instanceof Player)) {
                        profile.sendMessage(Utils.color("&cOnly players may use this command."));
                        return true;
                    }
                    
                    if (!profile.hasPermission("realms.admin.hamlets.modify.claim")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    
                    Plot plot = plugin.getPlotManager().getPlot(profile.getLocation());
                    Territory currentTerritory = plugin.getTerritoryManager().getTerritory(profile.getLocation());
                    if (currentTerritory != null) {
                        currentTerritory.unclaim(profile.getLocation());
                        hamlet.sendMemberMessage("&j" + profile.getName() + " &ihas forcefully unclaimed the plot &j" + plot.toString());
                        plugin.addAuditEntry(profile.getName() + " removed the plot " + plot.getUniqueId() + " from the territory " + currentTerritory.getUniqueId());
                    }
                    
                    hamlet.claim(profile.getLocation(), sender);
                    hamlet.sendMemberMessage("&j" + profile.getName() + " &ihas forcefully claimed the plot &j" + plot.toString());
                    plugin.addAuditEntry(profile.getName() + " added the plot " + plot.getUniqueId() + " to the territory " + hamlet.getUniqueId());
                    profile.sendMessage(Utils.color("&iForcefully added the plot &j" + plot.toString() + " &ifor the hamlet &j" + hamlet.getName()));
                } else if (Utils.checkCmdAliases(args, 3, "unclaim")) {
                    if (!(sender instanceof Player)) {
                        profile.sendMessage(Utils.color("&cOnly players may use this command."));
                        return true;
                    }
                    
                    if (!profile.hasPermission("realms.admin.hamlets.modify.unclaim")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    
                    Plot plot = plugin.getPlotManager().getPlot(profile.getLocation());
                    
                    hamlet.unclaim(profile.getLocation());
                    hamlet.sendMemberMessage("&j" + profile.getName() + " &ihas forcefully unclaimed the plot &j" + plot.toString());
                    plugin.addAuditEntry(profile.getName() + " removed the plot " + plot.getUniqueId() + " from the territory " + hamlet.getUniqueId());
                    profile.sendMessage(Utils.color("&iForcefully removed the plot &j" + plot.toString() + " &ifrom the hamlet &j" + hamlet.getName()));
                } else if (Utils.checkCmdAliases(args, 3, "set")) {
                    if (!(args.length > 4)) {
                        profile.sendMessage(Utils.color("&cYou must provide a command."));
                        return true;
                    }
                    
                    if (!profile.hasPermission("realms.admin.hamlets.modify.set")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    
                    if (Utils.checkCmdAliases(args, 4, "spawnpoint")) {
                        if (!(sender instanceof Player)) {
                            profile.sendMessage(Utils.color("&cOnly players may use this command."));
                            return true;
                        }
                        
                        if (!profile.hasPermission("realms.admin.hamlets.modify.set.spawnpoint")) {
                            profile.sendMessage("&cYou do not have permission to use that command.");
                            return true;
                        }
                        
                        boolean claim = false;
                        if (args.length > 5) {
                            if (args[5].equalsIgnoreCase("-c")) {
                                claim = true;
                            }
                        }
                        
                        if (!hamlet.contains(profile.getLocation()) && !claim) {
                            profile.sendMessage(Utils.color("&cThe spawnpoint must be set in the hamlet claim."));
                            profile.sendMessage(Utils.color("&cUse the -c flag to claim the plot as well.`"));
                            return true;
                        }
                        
                        if (claim) {
                            hamlet.claim(profile.getLocation(), sender);
                            Plot plot = plugin.getPlotManager().getPlot(profile.getLocation());
                            plugin.addAuditEntry(profile.getName() + " added the plot " + plot.getUniqueId() + " to the territory " + hamlet.getUniqueId());
                        }
                        
                        Location spawn = profile.getLocation();
                        hamlet.setSpawnpoint(spawn);
                        int x = spawn.getBlockX(), y = spawn.getBlockY(), z = spawn.getBlockZ();
                        String coords = "(" + x + ", " + y + ", " + z + ")";
                        hamlet.sendMemberMessage("&j" + profile.getName() + " &ihas forcefully set the spawnpoint to &j" + coords);
                        plugin.addAuditEntry(profile.getName() + " changed " + hamlet.getUniqueId() + "'s spawnpoint to " + coords);
                        profile.sendMessage(Utils.color("&iForcefully changed &j" + hamlet.getName() + "&i's spawnpoint to &j" + coords));
                    } else if (Utils.checkCmdAliases(args, 4, "name")) {
                        if (!(args.length > 5)) {
                            profile.sendMessage(Utils.color("&cYou must provide a new name."));
                            return true;
                        }
                        
                        if (!profile.hasPermission("realms.admin.hamlets.modify.set.name")) {
                            profile.sendMessage("&cYou do not have permission to use that command.");
                            return true;
                        }
                        
                        String newName = StringUtils.join(args, " ", 5, args.length);
                        hamlet.setName(newName);
                        hamlet.sendMemberMessage("&iThe hamlet name was changed to &j" + newName + " &iby &j" + profile.getName());
                        plugin.addAuditEntry(profile.getName() + " changed " + hamlet.getUniqueId() + "'s name to " + newName);
                        profile.sendMessage(Utils.color("&iSet the name of &j" + hamlet.getName() + " &ito &j" + newName));
                    } else if (Utils.checkCmdAliases(args, 4, "leader")) {
                        if (!(args.length > 5)) {
                            profile.sendMessage(Utils.color("&cYou must provide a new leader."));
                            return true;
                        }
                        
                        if (!profile.hasPermission("realms.admin.hamlets.modify.set.leader")) {
                            profile.sendMessage("&cYou do not have permission to use that command.");
                            return true;
                        }
                        
                        RealmProfile target = plugin.getProfileManager().getProfile(args[5]);
                        if (target == null) {
                            profile.sendMessage(Utils.color("&cThat name does not match a valid player."));
                            return true;
                        }
                        
                        boolean addMember = false;
                        if (args.length > 6) {
                            if (args[6].equalsIgnoreCase("-a")) {
                                addMember = true;
                            }
                        }
                        
                        if (!hamlet.isMember(target) && !addMember) {
                            profile.sendMessage(Utils.color("&cThat player is not a member of that hamlet."));
                            profile.sendMessage(Utils.color("&cUse the flag -a to add the member as well."));
                            return true;
                        }
                        
                        if (addMember) {
                            Territory territory = plugin.getTerritoryManager().getTerritory(target.getUniqueId());
                            if (territory != null) {
                                if (territory.getLeader().getRealmProfile().getUniqueId().equals(target.getUniqueId())) {
                                    profile.sendMessage(Utils.color("&cYou cannot do that as that player is the leader of another hamlet."));
                                    return true;
                                }
                                
                                territory.removeMember(target.getUniqueId());
                                plugin.addAuditEntry(profile.getName() + " removed " + target.getName() + " from the territory " + territory.getUniqueId() + " as a result of adding to another territory.");
                            }
                            hamlet.addMember(target.getUniqueId());
                            plugin.addAuditEntry(profile.getName() + " added " + target.getName() + " to the territory " + hamlet.getUniqueId());
                        }
                        
                        String oldLeader = "NONE";
                        try {
                            oldLeader = hamlet.getLeader().getRealmProfile().getName();
                        } catch (Exception e) {}
                        
                        hamlet.transfer(target.getUniqueId());
                        hamlet.sendMemberMessage("&iThe leader of the hamlet was changed from &j" + oldLeader + " &ito &j" + target.getName());
                        plugin.addAuditEntry(profile.getName() + " changed the leadership of " + hamlet.getUniqueId() + " from " + oldLeader + " to " + target.getName());
                        profile.sendMessage(Utils.color("&iChanged &j" + hamlet.getName() + "&i's leader from &j" + oldLeader + " &ito &j" + target.getName()));
                    }
                }
            } else if (Utils.checkCmdAliases(args, 1, "delete")) {
                if (!(args.length > 2)) {
                    profile.sendMessage(Utils.color("&cYou must provide a hamlet name."));
                    return true;
                }
                
                if (!profile.hasPermission("realms.admin.hamlets.delete")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                
                String name = args[2].replace("_", " ");
                
                Hamlet hamlet = (Hamlet) plugin.getTerritoryManager().getTerritory(name);
                if (hamlet == null) {
                    profile.sendMessage(Utils.color("&cThe name you provided does not match a hamlet."));
                    return true;
                }
                
                hamlet.disband();
                plugin.addAuditEntry(profile.getName() + " deleted the territory " + hamlet.getName());
                plugin.getTerritoryManager().removeTerritory(hamlet.getUniqueId());
                profile.sendMessage(Utils.color("&iDeleted the hamlet &j" + hamlet.getName()));
            } else if (Utils.checkCmdAliases(args, 1, "view")) {
                if (!(args.length > 3)) {
                    profile.sendMessage(Utils.color("&cYou must provide more arguments."));
                    return true;
                }
                
                if (!profile.hasPermission("realms.admin.hamlets.view")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                
                String name = args[2].replace("_", " ");
                
                Hamlet hamlet = (Hamlet) plugin.getTerritoryManager().getTerritory(name);
                if (hamlet == null) {
                    profile.sendMessage(Utils.color("&cThe name you provided does not match a hamlet."));
                    return true;
                }
                
                if (Utils.checkCmdAliases(args, 3, "members")) {
                    if (!profile.hasPermission("realms.admin.hamlets.view.members")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    Set<Member> members = hamlet.getMembers();
                    Paginator<Member> paginator = PaginatorFactory.generatePaginator(7, members, new HashMap<>() {{
                        put(DefaultVariables.TYPE, hamlet.getName() + " Members");
                        put(DefaultVariables.COMMAND, "realms hamlet view members");
                    }});
                    
                    if (args.length == 4) {
                        paginator.display(sender, 1);
                    } else {
                        paginator.display(sender, args[4]);
                    }
                } else if (Utils.checkCmdAliases(args, 3, "plots")) {
                    if (!profile.hasPermission("realms.admin.hamlets.view.plots")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    Paginator<ClaimedPlot> paginator = PaginatorFactory.generatePaginator(7, hamlet.getPlots(), new HashMap<>() {{
                        put(DefaultVariables.TYPE, hamlet.getName() + " Plots");
                        put(DefaultVariables.COMMAND, "realms hamlet view plots");
                    }});
                    
                    if (args.length == 4) {
                        paginator.display(sender, 1);
                    } else {
                        paginator.display(sender, args[4]);
                    }
                }
            } else if (Utils.checkCmdAliases(args, 1, "teleport", "tp")) {
                if (!profile.hasPermission("realms.admin.hamlets.teleport")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                
                if (!(args.length > 3)) {
                    profile.sendMessage(Utils.color("&cYou must provide more arguments."));
                    return true;
                }
                
                if (!(sender instanceof Player)) {
                    profile.sendMessage(Utils.color("&cOnly players may do that."));
                    return true;
                }
                
                String name = args[2].replace("_", " ");
                
                Hamlet hamlet = (Hamlet) plugin.getTerritoryManager().getTerritory(name);
                if (hamlet == null) {
                    profile.sendMessage(Utils.color("&cThe name you provided does not match a hamlet."));
                    return true;
                }
                
                if (Utils.checkCmdAliases(args, 3, "spawn")) {
                    if (!profile.hasPermission("realms.admin.hamlets.teleport.spawn")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    profile.teleport(hamlet.getSpawnpoint());
                    profile.sendMessage(Utils.color("&iTeleported to &j" + hamlet.getName() + "&i's spawnpoint."));
                } else if (Utils.checkCmdAliases(args, 3, "plot")) {
                    if (!profile.hasPermission("realms.admin.hamlets.teleport.plot")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    if (!(args.length > 4)) {
                        profile.sendMessage(Utils.color("&cYou must provide a plot id."));
                        return true;
                    }
                    
                    ClaimedPlot plot = hamlet.getPlot(args[4]);
                    if (plot == null) {
                        profile.sendMessage(Utils.color("&cThat is either not a valid plot id, or that hamlet has not claimed it."));
                        return true;
                    }
                    
                    Location location = plot.getPlot().getCenter();
                    for (int y = 255; y >= 0; y--) {
                        location.setY(y);
                        Block block = location.getBlock();
                        if (block.getType() != Material.AIR) {
                            break;
                        }
                    }
                    
                    location.setY(location.getBlockY() + 1);
                    profile.teleport(location);
                    profile.sendMessage(Utils.color("&iTeleported to the plot &j" + plot.getPlot().getUniqueId()));
                }
            }
        } else if (Utils.checkCmdAliases(args, 0, "spawn", "s", "warzone", "wz", "w")) {
            if (!profile.hasPermission("realms.admin.serverclaim")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            if (!(args.length > 1)) {
                profile.sendMessage(Utils.color("&cYou must provide more arguments."));
                return true;
            }
            
            if (!(sender instanceof Player)) {
                profile.sendMessage(Utils.color("&cOnly players can use that command."));
                return true;
            }
            
            ServerClaim claim = null;
            if (Utils.checkCmdAliases(args, 0, "spawn", "s")) {
                if (!profile.hasPermission("realms.admin.serverclaim.spawn")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                claim = plugin.getSpawn();
            } else if (Utils.checkCmdAliases(args, 0, "warzone", "wz", "w")) {
                if (!profile.hasPermission("realms.admin.serverclaim.warzone")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                claim = plugin.getWarzone();
            }
            
            if (Utils.checkCmdAliases(args, 1, "claim", "c")) {
                Plot plot = plugin.getPlotManager().getPlot(profile.getLocation());
                Territory territory = plugin.getTerritoryManager().getTerritory(plot.getCenter());
                if (territory != null) {
                    profile.sendMessage(Utils.color("&iThat plot contains a territory, claiming for spawn overrides the claim."));
                    territory.sendMemberMessage("&cThe plot " + plot.toString() + " is being claimed for Spawn.");
                    territory.unclaim(plot.getCenter());
                }
                
                claim.addPlot(plot);
            } else if (Utils.checkCmdAliases(args, 1, "unclaim")) {
                Plot plot = plugin.getPlotManager().getPlot(profile.getLocation());
                claim.removePlot(plot);
            } else if (Utils.checkCmdAliases(args, 1, "pos1")) {
                plugin.getSelectionManager().setPointA(profile.getUniqueId(), profile.getLocation());
                profile.sendMessage(Utils.color("&iSet " + claim.getName() + " pos1"));
            } else if (Utils.checkCmdAliases(args, 1, "pos2")) {
                plugin.getSelectionManager().setPointB(profile.getUniqueId(), profile.getLocation());
                profile.sendMessage(Utils.color("&iSet " + claim.getName() + " pos2"));
            } else if (Utils.checkCmdAliases(args, 1, "claimall")) {
                Selection selection = plugin.getSelectionManager().getSelection(profile.getUniqueId());
                if (selection == null) {
                    profile.sendMessage(Utils.color("&cYou do not have an active selection."));
                    return true;
                }
                
                Cuboid cuboid = plugin.getSelectionManager().getCuboid(profile.getUniqueId());
                if (cuboid == null) {
                    profile.sendMessage(Utils.color("&cInvalid selection."));
                    return true;
                }
                
                int xMax = cuboid.getMaximum().getBlockX(), xMin = cuboid.getMinimum().getBlockX();
                int zMax = cuboid.getMaximum().getBlockZ(), zMin = cuboid.getMinimum().getBlockZ();
                
                int y = 1, plotsClaimed = 0, warzoneUnclaimed = 0;
                World world = profile.getWorld();
                for (int x = xMin; x < xMax; x++) {
                    for (int z = zMin; z < zMax; z++) {
                        if (!claim.contains(world, x, y, z)) {
                            Location location = new Location(world, x, y, z);
                            Plot plot = plugin.getPlotManager().getPlot(location);
                            Territory territory = plugin.getTerritoryManager().getTerritory(plot.getCenter());
                            if (territory != null) {
                                territory.sendMemberMessage("&cThe plot " + plot.toString() + " is being claimed for " + claim.getName() + ".");
                                territory.unclaim(plot.getCenter());
                            }
                            
                            if (claim instanceof Warzone) {
                                if (plugin.getSpawn().contains(location)) {
                                    continue;
                                }
                            } else if (claim instanceof Spawn) {
                                if (plugin.getWarzone().contains(location)) {
                                    warzoneUnclaimed++;
                                    plugin.getWarzone().removePlot(plot);
                                }
                            }
                            
                            claim.addPlot(plot);
                            plotsClaimed++;
                        }
                    }
                }
                
                profile.sendMessage(Utils.color("&iClaimed &j" + plotsClaimed + " &iplot(s) for the &j" + claim.getName().toLowerCase()));
                if (warzoneUnclaimed != 0) {
                    profile.sendMessage(Utils.color("&iUnclaimed &j" + warzoneUnclaimed + " &iwarzone plot(s) because spawn is a higher priority."));
                }
            } else if (Utils.checkCmdAliases(args, 1, "setspawnpoint", "setspawn", "ssp")) {
                if (!profile.hasPermission("realms.admin.setspawnpoint")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                if (claim instanceof Spawn) {
                    Location loc = profile.getLocation();
                    if (!plugin.getSpawn().contains(loc)) {
                        profile.sendMessage(Utils.color("&cThe spawnpoint must be in the spawn region."));
                        return true;
                    }
                    
                    plugin.getSpawn().setSpawnpoint(loc);
                    profile.sendMessage("&iSet the spawnpoint to your current location.");
                }
            }
        } else if (Utils.checkCmdAliases(args, 0, "season")) {
            User firestar311 = plugin.getUserManager().getUser("Firestar311");
            
            if (!(sender instanceof Player)) {
                profile.sendMessage(Utils.color("&cOnly a player may use that command."));
                return true;
            }
            
            Player player = (Player) sender;
            if (!player.getUniqueId().equals(firestar311.getUniqueId())) {
                player.sendMessage(Utils.color("&cOnly Firestar311 can use that command."));
                return true;
            }
            
            if (!(args.length > 1)) {
                player.sendMessage(Utils.color("&cYou must provide a subcommand and arguments."));
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 1, "setactive", "sa")) {
                boolean value;
                try {
                    value = Boolean.parseBoolean(args[2]);
                } catch (Exception e) {
                    player.sendMessage(Utils.color("&cInvalid argument, must be true or false."));
                    return true;
                }
                
                plugin.getSeason().setActive(value);
                if (value) {
                    Bukkit.broadcastMessage(Utils.color("&aKingRealms " + plugin.getSeason().getType().toString() + " Season #" + plugin.getSeason().getNumber() + " has been activated!"));
                } else {
                    Bukkit.broadcastMessage(Utils.color("&cKingRealms " + plugin.getSeason().getType().toString() + " Season #" + plugin.getSeason().getNumber() + " has been deactivated!"));
                }
            } else {
                player.sendMessage(Utils.color("&cInvalid subcommand."));
                return true;
            }
        } else if (Utils.checkCmdAliases(args, 0, "spawners")) {
            if (!profile.hasPermission("realms.admin.spawners")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            if (args.length == 1) {
                profile.sendMessage("&iTotal amount of spawners: &j" + plugin.getSpawnerManager().getSpawners().size());
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 1, "list")) {
                Paginator<CustomSpawner> spawners = PaginatorFactory.generatePaginator(7, plugin.getSpawnerManager().getSpawners(), new HashMap<>() {{
                    put(DefaultVariables.COMMAND, "realms spawners list");
                    put(DefaultVariables.TYPE, "Spawners");
                }});
                if (args.length == 2) {
                    spawners.display(sender, 1);
                } else {
                    spawners.display(sender, args[2]);
                }
            } else if (Utils.checkCmdAliases(args, 1, "info")) {
                Block targetBlock = profile.getTargetBlock(10);
                if (targetBlock == null) {
                    profile.sendMessage("&cYou are not looking at a valid block.");
                    return true;
                }
                
                CustomSpawner spawner = plugin.getSpawnerManager().getSpawner(targetBlock.getLocation());
                if (spawner == null) {
                    profile.sendMessage("&cYou are not looking at a Custom Spawner.");
                    return true;
                }
                
                profile.sendMessage("&iDisplaying Information about the spawner you are looking at.");
                spawner.getDisplayMap().forEach((key, value) -> profile.sendMessage("&i" + key + "&8: &j" + value));
            }
        } else if (Utils.checkCmdAliases(args, 0, "mobstack")) {
            if (!profile.hasPermission("realms.admin.mobstack")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            if (!(sender instanceof Player)) {
                profile.sendMessage(Utils.color("&cOnly a player may use that command."));
                return true;
            }
            
            if (!(args.length > 0)) {
                profile.sendMessage(Utils.color("&cYou must provide a sub command."));
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 1, "spawn")) {
                if (!(args.length > 2)) {
                    profile.sendMessage(Utils.color("&cYou must provide an entity type."));
                    return true;
                }
                
                EntityType type;
                try {
                    type = EntityType.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException e) {
                    profile.sendMessage(Utils.color("&cInvalid entity type: " + args[1]));
                    return true;
                }
                
                if (!type.isSpawnable()) {
                    profile.sendMessage(Utils.color("&cYou provided an entity type that cannot be spawned."));
                    return true;
                }
                
                if (!type.isAlive()) {
                    profile.sendMessage(Utils.color("&cYou provided an entity type that is not a living entity."));
                    return true;
                }
                
                if (!profile.hasPermission("realms.admin.mobstack." + type.name().toLowerCase() + ".spawn")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                
                int amount = 1;
                if (args.length > 3) {
                    try {
                        amount = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        profile.sendMessage(Utils.color("&cInvalid number " + args[2]));
                        return true;
                    }
                }
                
                if (!CustomEntities.REGISTRY.containsKey(type)) {
                    profile.sendMessage("&cThat type is not allowed yet.");
                    return true;
                }
                
                LivingEntity entity = (LivingEntity) profile.getWorld().spawnEntity(profile.getLocation(), type);
                ICustomEntity customEntity = CustomEntities.getCustomEntity(entity);
                customEntity.setCustom(true);
                MobStack mobStack = new MobStack(entity.getUniqueId(), amount);
                mobStack.updateName(entity);
                plugin.getSpawnerManager().addMobStack(mobStack);
                profile.sendMessage(Utils.color("&iCreated a mob stack with type &j" + EntityNames.getName(type) + " &iand a count of &j" + amount));
                return true;
            }
            
            Entity target = Utils.getTargetEntity((Player) sender);
            
            if (target == null) {
                profile.sendMessage(Utils.color("&cYou are not looking at an entity."));
                return true;
            }
            
            if (!(target instanceof LivingEntity)) {
                profile.sendMessage(Utils.color("&cInvalid entity type for a mob stack."));
                return true;
            }
            
            LivingEntity livingEntity = (LivingEntity) target;
            
            MobStack mobStack = plugin.getSpawnerManager().getMobStack(target.getUniqueId());
            if (mobStack == null) {
                profile.sendMessage(Utils.color("&cThat entity is not a valid mob stack."));
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 1, "setamount")) {
                if (!profile.hasPermission("realms.admin.mobstack.setamount")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                if (!(args.length > 1)) {
                    profile.sendMessage(Utils.color("&cYou must provide an amount."));
                    return true;
                }
                
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (IllegalArgumentException e) {
                    profile.sendMessage(Utils.color("&cYou provided an invalid number."));
                    return true;
                }
                
                mobStack.setCount(amount);
                mobStack.updateName(livingEntity);
                profile.sendMessage(Utils.color("&iSet the mob stack amount to &j" + amount));
            } else if (Utils.checkCmdAliases(args, 1, "remove")) {
                if (!profile.hasPermission("realms.admin.mobstack.remove")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                plugin.getSpawnerManager().removeMobStack(mobStack.getEntityId());
                livingEntity.remove();
                profile.sendMessage(Utils.color("&iYou removed that mob stack."));
            }
        } else if (Utils.checkCmdAliases(args, 0, "limits")) {
            if (!profile.hasPermission("realms.admin.limits")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            if (!(args.length > 0)) {
                for (LimitGroup limitGroup : plugin.getLimitsManager().getLimitGroups()) {
                    profile.sendMessage("&eLimit Group: &b" + limitGroup.getName());
                    for (Limit limit : limitGroup.getLimits()) {
                        profile.sendMessage(" &8- &d" + limit.getName() + "&7: &a" + limit.getValue().toString());
                    }
                }
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 0, "set")) {
                if (!profile.hasPermission("realms.admin.limits.set")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                if (!(args.length > 1)) {
                    profile.sendMessage("&cYou must provide a limit id.");
                    return true;
                }
                
                Limit limit = plugin.getLimitsManager().getLimit(args[1]);
                if (limit == null) {
                    profile.sendMessage("&cYou provided an invalid limit id.");
                    return true;
                }
                
                if (!(args.length > 2)) {
                    profile.sendMessage("&cYou must provide a value.");
                    return true;
                }
                
                Number value = null;
                if (limit instanceof IntegerLimit) {
                    try {
                        value = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        profile.sendMessage("&cInvalid number for that limit value type: whole number");
                        return true;
                    }
                } else if (limit instanceof DoubleLimit) {
                    try {
                        value = Double.parseDouble(args[2]);
                    } catch (NumberFormatException e) {
                        profile.sendMessage("&cInvalid number for that limit value type: decimal number");
                        return true;
                    }
                }
                
                if (value == null) {
                    profile.sendMessage("&cInvalid value or argument type.");
                    return true;
                }
                
                limit.setValue(value);
                profile.sendMessage("&iSet the limit &j" + limit.getName() + " &ito &j" + value.toString());
            }
        } else if (Utils.checkCmdAliases(args, 0, "kits")) {
            if (!profile.hasPermission("realms.admin.kits")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            if (!(args.length > 1)) {
                sender.sendMessage(Utils.color("&cYou must provide a sub command."));
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 1, "list")) {
                Paginator<Kit> paginator = PaginatorFactory.generatePaginator(7, plugin.getKitManager().getKits(), new HashMap<>() {{
                    put(DefaultVariables.TYPE, "Kits");
                    put(DefaultVariables.COMMAND, "realms kits list");
                }});
    
                if (args.length == 2) {
                    paginator.display(sender, 1);
                } else {
                    paginator.display(sender, args[2]);
                }
            } else if (Utils.checkCmdAliases(args, 1, "modify")) {
                if (!profile.hasPermission("realms.admin.kits.modify")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                if (!(args.length > 2)) {
                    sender.sendMessage(Utils.color("&cYou must provide a kit to modify"));
                    return true;
                }
    
                Kit kit = plugin.getKitManager().getKit(args[2]);
                if (kit == null) {
                    profile.sendMessage("&cThe name you provided did not match a kit.");
                    return true;
                }
                
                if (!(args.length > 3)) {
                    sender.sendMessage(Utils.color("&cYou must provide a value type to modify"));
                    return true;
                }
                
                if (Utils.checkCmdAliases(args, 3, "name")) {
                    if (!profile.hasPermission("realms.admin.kits.modify.name")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    if (!(args.length > 4)) {
                        sender.sendMessage(Utils.color("&cYou must provide a new kit name."));
                        return true;
                    }
                    
                    String oldName = kit.getName();
                    
                    kit.setName(args[4]);
                    profile.sendMessage("&iYou set &j" + oldName + " &iname to &j" + kit.getName());
                } else if (Utils.checkCmdAliases(args, 3, "cooldown")) {  
                    if (!profile.hasPermission("realms.admin.kits.modify.cooldown")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    if (!(args.length > 4)) {
                        sender.sendMessage(Utils.color("&cYou must provide a value for the cooldown"));
                        return true;
                    }
                    
                    if (args[4].equalsIgnoreCase("none")) {
                        kit.setCooldown(-1);
                        profile.sendMessage("&iThe kit &j" + kit.getName() + " &inow has no cooldown.");
                        return true;
                    }
                    
                    long time = Utils.parseTime(args[4]);
                    if (time == 0) {
                        profile.sendMessage("&cYou provided an invalid time format.");
                        return true;
                    }
                    
                    kit.setCooldown(time);
                    profile.sendMessage("&iSet the cooldown of the kit &j" + kit.getName() + " &ito &j" + Utils.formatTime(time));
                }
            } else if (Utils.checkCmdAliases(args, 1, "tiers")) {
                if (!profile.hasPermission("realms.admin.kits.tiers")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                if (!(args.length > 2)) {
                    sender.sendMessage(Utils.color("&cYou must provide a kit name."));
                    return true;
                }
                
                Kit kit = plugin.getKitManager().getKit(args[2]);
                if (kit == null) {
                    profile.sendMessage("&cThat name did not match a kit.");
                    return true;
                }
                
                if (!(args.length > 3)) {
                    sender.sendMessage(Utils.color("&cYou must provide a subcommand"));
                    return true;
                }

                if (Utils.checkCmdAliases(args, 3, "list")) {
                    if (!profile.hasPermission("realms.admin.kits.tiers.list")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    Paginator<Kit> paginator = PaginatorFactory.generatePaginator(7, plugin.getKitManager().getKits(), new HashMap<>() {{
                        put(DefaultVariables.TYPE, "Kits");
                        put(DefaultVariables.COMMAND, "realms kit tiers " + kit.getName() + " list");
                    }});
                    
                    if (args.length > 4) {
                        paginator.display(sender, args[4]);
                    } else {
                        paginator.display(sender, 1);
                    }
                    return true;
                }
    
                if (Utils.checkCmdAliases(args, 3, "add")) {
                    if (!profile.hasPermission("realms.admin.kits.tiers.add")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    if (!(args.length > 4)) {
                        sender.sendMessage(Utils.color("&cYou must provide a tier postion, 'next' can be used"));
                        return true;
                    }
                    
                    int position;
                    if (args[4].equalsIgnoreCase("next")) {
                        position = plugin.getKitManager().getNextPosition();
                    } else {
                        try {
                            position = Integer.parseInt(args[4]);
                        } catch (NumberFormatException e) {
                            profile.sendMessage("&cYou must provide a valid number.");
                            return true;
                        }
                    }
                    
                    KitTier tier = new KitTier(position);
                    for (int i = 0; i < profile.getInventory().getContents().length; i++) {
                        ItemStack itemStack = profile.getInventory().getItem(i);
                        if (itemStack != null) {
                            tier.addItem(i, profile.getInventory().getItem(i).clone());
                        }
                    }
                    
                    kit.addTier(tier);
                    profile.sendMessage("&iAdded a tier for the kit " + kit.getName() + " at position " + position);
                    return true;
                }
                
                if (!(args.length > 4)) {
                    sender.sendMessage(Utils.color("&cYou must provide a tier number."));
                    return true;
                }
    
                int t;
                try {
                    t = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    profile.sendMessage("&cYou did not provide a valid number.");
                    return true;
                }
                KitTier tier = kit.getTier(t);
                
                if (tier == null) {
                    profile.sendMessage("&cInvalid tier number");
                    return true;
                }
                
                if (Utils.checkCmdAliases(args, 3, "remove")) {
                    if (!profile.hasPermission("realms.admin.kits.tiers.remove")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    boolean organize = false;
                    if (args.length > 5) {
                        organize = args[5].equalsIgnoreCase("-o");
                    }
                    
                    kit.removeTier(tier);
                    profile.sendMessage("&iYou have removed tier &j" + tier.getPosition() + " &ifrom the kit &j" + kit.getName());
                    if (organize) {
                        kit.fixTierPositions();
                        profile.sendMessage("&iOrganized all tier positions after a tier removal.");
                    }
                } else if (Utils.checkCmdAliases(args, 3, "view")) {
                    if (!profile.hasPermission("realms.admin.kits.tiers.view")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    Inventory inv = Bukkit.createInventory(null, 45, kit.getName() + " Tier #" + tier.getPosition());
                    tier.getItems().forEach(inv::setItem);
                    profile.getBukkitPlayer().openInventory(inv);
                }
            }
        } else if (Utils.checkCmdAliases(args, 0, "items", "item", "i")) {
            if (!profile.hasPermission("realms.admin.items")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            if (!(args.length > 1)) {
                sender.sendMessage(Utils.color("&cYou must provide an item id."));
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 1, "gui")) {
                if (!profile.hasPermission("realms.admin.items.gui")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                new ItemGuiMain().openGUI(profile.getBukkitPlayer());
                return true;
            }
    
            CustomItem item = CustomItemRegistry.REGISTRY.get(new ID(args[1]));
            if (item == null) {
                profile.sendMessage("&cThe value you provided did not match a valid id.");
                return true;
            }
            
            if (!profile.hasPermission("realms.admin.items.give")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
    
            ItemStack itemStack = item.getItemStack();
            try {
                itemStack = NBTWrapper.addNBTString(itemStack, "commandsummoned", "true");
            } catch (Exception e) {
                profile.sendMessage("&cThere was an error giving you that item.");
                return true;
            }
            
            profile.getInventory().addItem(itemStack);
            profile.sendMessage("&iGave you the item " + item.getDisplayName());
        } else if(Utils.checkCmdAliases(args, 0, "mode")) {
            if (!profile.hasPermission("realms.admin.mode")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            
            if (!(args.length > 1)) {
                sender.sendMessage(Utils.color("&cYou must provide a mode type."));
                return true;
            }
            
            ServerMode serverMode;
            try {
                serverMode = ServerMode.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                profile.sendMessage("&cInvalid server mode type.");
                return true;
            }
            
            if (!profile.hasPermission("realms.admin.mode." + serverMode.name().toLowerCase())) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            
            ServerMode oldMode = plugin.getServerMode();
            if (oldMode == serverMode) {
                profile.sendMessage("&cThe old mode and the one you provided is the same.");
                return true;
            }
            plugin.setServerMode(serverMode);
            profile.sendMessage("&iYou changed the server mode from &j" + oldMode.name() + " &ito &j" + serverMode.name());
        } else if (Utils.checkCmdAliases(args, 0, "spawnmob")) {
            if (!(args.length > 1)) {
                sender.sendMessage(Utils.color("&cYou must provide a mob type"));
                return true;
            }
            
            EntityType type;
            try {
                type = EntityType.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                profile.sendMessage("&cYou provided an invalid entity type.");
                return true;
            }
            
            int amount = 1;
            if (args.length > 2) {
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (IllegalArgumentException e) {
                    profile.sendMessage("&cYou provided an invalid amount.");
                    return true;
                }
            }
            
            List<Entity> entities = new ArrayList<>();
            for (int i = 0; i < amount; i++) {
                Entity entity = profile.getWorld().spawnEntity(profile.getLocation(), type);
                entity.setInvulnerable(true);
                entities.add(entity);
            }
    
            int finalAmount = amount;
            new BukkitRunnable() {
                @Override
                public void run() {
                    entities.forEach(entity -> entity.setInvulnerable(false));
                    profile.sendMessage("&iSpawned &j" + finalAmount + " &h" + EntityNames.getName(type).toLowerCase() + "(s)");
                }
            }.runTaskLater(plugin, 10L);
    
        } else if (Utils.checkCmdAliases(args, 0, "whitelist")) {
            if (!profile.hasPermission("realms.admin.whitelist")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            
            if (!(args.length > 1)) {
                sender.sendMessage(Utils.color("&cYou must provide a whitelist name or sub command"));
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 1, "create")) {
                if (!profile.hasPermission("realms.admin.whitelist.create")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                if (!(args.length > 2)) {
                    sender.sendMessage(Utils.color("&cYou must provide a whitelist name."));
                    return true;
                }
                
                String name = StringUtils.join(args, " ", 2, args.length);
                Whitelist existing = plugin.getWhitelistManager().getWhitelist(name);
                if (existing != null) {
                    profile.sendMessage("&cThere is already a whitelist with that name.");
                    return true;
                }
                
                Whitelist whitelist = new Whitelist(name);
                plugin.getWhitelistManager().addWhitelist(whitelist);
                profile.sendMessage("&iCreated a whitelist with the name &j" + whitelist.getName());
                return true;
            } else if (Utils.checkCmdAliases(args, 1, "setactivewhitelist", "setactive")) {
                if (!profile.hasPermission("realms.admin.whitelist.setactivewhitelist")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                if (!(args.length > 2)) {
                    sender.sendMessage(Utils.color("&cYou must provide a whitelist name."));
                    return true;
                }
                
                if (Utils.checkCmdAliases(args, 2, "default", "vanilla")) {
                    plugin.getWhitelistManager().setActiveWhitelist(-1);
                    sender.sendMessage("&cYou set the whitelist settings to vanilla behavior");
                    return true;
                }
    
                Whitelist whitelist = plugin.getWhitelistManager().getWhitelist(args[2]);
                if (whitelist == null) {
                    profile.sendMessage("&cThe identifier you provided did not match a valid whitelist.");
                    return true;
                }
                
                plugin.getWhitelistManager().setActiveWhitelist(whitelist.getId());
                profile.sendMessage("&iYou set the active whitelist to &j" + whitelist.getName());
                return true;
            }
            
            Whitelist whitelist = plugin.getWhitelistManager().getWhitelist(args[1]);
            if (whitelist == null) {
                profile.sendMessage("&cThe name you provided did not match a valid whitelist.");
                return true;
            }
            
            if (!(args.length > 2)) {
                sender.sendMessage(Utils.color("&cYou must provide a sub command."));
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 2, "setname", "sn", "setdescription", "sd")) {
                if (!(args.length > 3)) {
                    sender.sendMessage(Utils.color("&cYou must provide a value"));
                    return true;
                }
                
                String value = StringUtils.join(args, " ", 3, args.length);
                String type;
                if (Utils.checkCmdAliases(args, 2, "setname", "sn")) {
                    if (!profile.hasPermission("realms.admin.whitelist.setname")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    if (plugin.getWhitelistManager().getWhitelist(value) != null) {
                        profile.sendMessage("&cA whitelist by that name already exists.");
                        return true;
                    }
                    whitelist.setName(value);
                    type = "name";
                } else {
                    if (!profile.hasPermission("realms.admin.whitelist.setdescription")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    whitelist.setDescription(value);
                    type = "description";
                }
    
                profile.sendMessage("&iSet the " + type + " of the whitelist &j" + whitelist.getId() + " &ito &j" + value);
            } else if (Utils.checkCmdAliases(args, 2, "addplayer", "ap", "removeplayer", "remove")) {
                if (!(args.length > 3)) {
                    sender.sendMessage(Utils.color("&cYou must provide a player name"));
                    return true;
                }
                
                RealmProfile target = plugin.getProfileManager().getProfile(args[3]);
                if (target == null) {
                    profile.sendMessage("&cYou provided an invalid player name.");
                    return true;
                }
                
                String action, verb;
                if (Utils.checkCmdAliases(args, 2, "addplayer", "ap")) {
                    if (!profile.hasPermission("realms.admin.whitelist.addplayer")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    whitelist.addPlayer(target);
                    action = "added";
                    verb = "to";
                } else {
                    if (!profile.hasPermission("realms.admin.whitelist.removeplayer")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    whitelist.removePlayer(target);
                    Whitelist active = plugin.getWhitelistManager().getActiveWhitelist();
                    if (active != null) {
                        if (active.getId() == whitelist.getId()) {
                            target.getBukkitPlayer().kickPlayer(Utils.color("&cYou have been removed from the active whitelist."));
                        }
                    }
                    action = "removed";
                    verb = "from";
                }
                
                profile.sendMessage("&iYou &h" + action + " &j" + target.getName() + " &i" + verb + " the whitelist &j" + whitelist.getName());
            } else if (Utils.checkCmdAliases(args, 2, "delete")) {
                if (!profile.hasPermission("realms.admin.whitelist.delete")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                
                plugin.getWhitelistManager().removeWhitelist(whitelist);
                profile.sendMessage("&iYou removed the whitelist &j" + whitelist.getName());
            }
        } else if (Utils.checkCmdAliases(args, 0, "cropblocks", "cb")) {
            if (!profile.hasPermission("realms.admin.cropblocks")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            if (args.length == 1) {
                profile.sendMessage("&iTotal amount of Crop Blocks: &j" + plugin.getFarmingManager().getCropBlocks().size());
                return true;
            }
    
            if (Utils.checkCmdAliases(args, 1, "list")) {
                Paginator<CropBlock> cropblocks = PaginatorFactory.generatePaginator(7, plugin.getFarmingManager().getCropBlocks(), new HashMap<>() {{
                    put(DefaultVariables.COMMAND, "realms cropblocks list");
                    put(DefaultVariables.TYPE, "Crop Blocks");
                }});
                if (args.length == 2) {
                    cropblocks.display(sender, 1);
                } else {
                    cropblocks.display(sender, args[2]);
                }
            } else if (Utils.checkCmdAliases(args, 1, "info")) {
                Block target = profile.getTargetBlock(10);
                if (target == null) {
                    profile.sendMessage("&cYou are not looking at a block.");
                    return true;
                }
                
                CropBlock cropBlock = plugin.getFarmingManager().getCropBlock(target.getLocation());
                if (cropBlock == null) {
                    profile.sendMessage("&cYou are not looking at a Crop Block");
                    return true;
                }
                
                profile.sendMessage("&iDisplaying Information about the Crop Block you are looking at.");
                cropBlock.getDisplayMap().forEach((key, value) -> profile.sendMessage("&i" + key + "&8: &j" + value));
            }
        } else if (Utils.checkCmdAliases(args, 0, "mysticalresources", "mr")) {
            if (!profile.hasPermission("realms.admin.mysticalresources")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            if (args.length == 1) {
                profile.sendMessage("&iTotal amount of Mystical Resources: &j" + plugin.getMiningManager().getMysticalBlocks().size());
                return true;
            }
    
            if (Utils.checkCmdAliases(args, 1, "list")) {
                Paginator<MysticalBlock> mysticalblocks = PaginatorFactory.generatePaginator(7, plugin.getMiningManager().getMysticalBlocks(), new HashMap<>() {{
                    put(DefaultVariables.COMMAND, "realms mysticalresources list");
                    put(DefaultVariables.TYPE, "Mystical Resources");
                }});
                if (args.length == 2) {
                    mysticalblocks.display(sender, 1);
                } else {
                    mysticalblocks.display(sender, args[2]);
                }
            } else if (Utils.checkCmdAliases(args, 1, "info")) {
                Block target = profile.getTargetBlock(10);
                if (target == null) {
                    profile.sendMessage("&cYou are not looking at a block.");
                    return true;
                }
                
                MysticalBlock mysticalBlock = plugin.getMiningManager().getMysticalBlock(target.getLocation());
                if (mysticalBlock == null) {
                    profile.sendMessage("&cYou are not looking at a Mystical Resource Block");
                    return true;
                }
    
                profile.sendMessage("&iDisplaying Information about the Mystical Resource Block you are looking at.");
                mysticalBlock.getDisplayMap().forEach((key, value) -> profile.sendMessage("&i" + key + "&8: &j" + value));
            }
        } else if (Utils.checkCmdAliases(args, 0, "version")) {
            if (!profile.hasPermission("realms.admin.version")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            
            profile.sendMessage("&iJava Version: &j" + Runtime.version().toString());
            profile.sendMessage("&iStarLib Version: &j" + Bukkit.getPluginManager().getPlugin("StarLib").getDescription().getVersion());
            profile.sendMessage("&iRealms Version: &j" + plugin.getDescription().getVersion());
        }

        else {
            profile.sendMessage(Utils.color("&cInvalid sub command."));
            return true;
        }
        
        return true;
    }
    
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> results = new ArrayList<>();
        
        List<String> possibleResults = new ArrayList<>();
        if (args.length == 1) {
            possibleResults.addAll(Arrays.asList("hamlets", "hamlet", "spawn", "warzone", "setspawnpoint", "setspawn", "spawners", "mobstack", "limit", "items", "mode", "whitelist", "cropblocks", "mysticalresources"));
            if (sender instanceof Player) {
                User firestar311 = plugin.getUserManager().getUser("Firestar311");
                if (((Player) sender).getUniqueId().equals(firestar311.getUniqueId())) {
                    possibleResults.add("season");
                }
            }
            results.addAll(getResults(args[0], possibleResults));
        } else if (args.length == 2) {
            if (Utils.checkCmdAliases(args, 0, "hamlets", "hamlet", "h")) {
                possibleResults = new ArrayList<>(Arrays.asList("list", "info", "modify", "delete", "view", "teleport"));
            } else if (Utils.checkCmdAliases(args, 0, "spawn", "warzone")) {
                possibleResults.addAll(Arrays.asList("claim", "unclaim", "pos1", "pos2", "claimall"));
            } else if (Utils.checkCmdAliases(args, 0, "spawner")) {
                possibleResults.addAll(Arrays.asList("give", "set"));
            } else if (Utils.checkCmdAliases(args, 0, "mobstack")) {
                possibleResults.addAll(Arrays.asList("spawn", "remove", "setamount"));
            } else if (Utils.checkCmdAliases(args, 0, "items", "item", "i")) {
                for (ID id : CustomItemRegistry.REGISTRY.keySet()) {
                    possibleResults.add(id.toString());
                }
            } else if (Utils.checkCmdAliases(args, 0, "season")) {
                User firestar311 = plugin.getUserManager().getUser("Firestar311");
                if (((Player) sender).getUniqueId().equals(firestar311.getUniqueId())) {
                    possibleResults.add("setactive");
                }
            } else if (Utils.checkCmdAliases(args, 0, "mode")) {
                for (ServerMode serverMode : ServerMode.values()) {
                    possibleResults.add(serverMode.name().toLowerCase());
                }
            } else if (Utils.checkCmdAliases(args, 0, "whitelist")) {
                possibleResults.addAll(List.of("create", "setactivewhitelist"));
                if (!plugin.getWhitelistManager().getWhitelists().isEmpty()) {
                    for (Whitelist whitelist : plugin.getWhitelistManager().getWhitelists()) {
                        possibleResults.add(whitelist.getName().toLowerCase().replace(" ", "_"));
                    }
                }
            }
            results.addAll(getResults(args[1], possibleResults));
        } else if (args.length == 3) {
            if (Utils.checkCmdAliases(args, 0, "hamlets", "hamlet", "h")) {
                if (Utils.checkCmdAliases(args, 1, "info", "i", "modify", "view", "teleport", "tp", "delete")) {
                    for (Territory territory : plugin.getTerritoryManager().getTerritories()) {
                        possibleResults.add(territory.getName().toLowerCase().replace(" ", "_"));
                    }
                }
            } else if (Utils.checkCmdAliases(args, 0, "spawner")) {
                if (Utils.checkCmdAliases(args, 1, "give", "set")) {
                    for (EntityType entityType : CustomEntities.REGISTRY.keySet()) {
                        if (entityType.isSpawnable()) {
                            if (entityType.isAlive()) {
                                possibleResults.add(entityType.name().toLowerCase());
                            }
                        }
                    }
                }
            } else if (Utils.checkCmdAliases(args, 0, "mobstack")) {
                if (Utils.checkCmdAliases(args, 1, "spawn")) {
                    for (EntityType entityType : CustomEntities.REGISTRY.keySet()) {
                        if (entityType.isSpawnable()) {
                            if (entityType.isAlive()) {
                                possibleResults.add(entityType.name().toLowerCase());
                            }
                        }
                    }
                }
            } else if (Utils.checkCmdAliases(args, 0, "whitelist")) {
                if (Utils.checkCmdAliases(args, 1, "setactivewhitelist", "setactive")) {
                    for (Whitelist whitelist : plugin.getWhitelistManager().getWhitelists()) {
                        possibleResults.add(whitelist.getName().toLowerCase().replace(" ", "_"));
                    }
                } else if (StringUtils.isNotEmpty(args[1]) && !Utils.checkCmdAliases(args, 1, "create")) {
                    possibleResults.addAll(List.of("setname", "setdescription", "addplayer", "removeplayer", "delete"));
                }
            }
            
            results.addAll(getResults(args[2], possibleResults));
        } else if (args.length == 4) {
            if (Utils.checkCmdAliases(args, 0, "hamlets", "hamlet", "h")) {
                if (Utils.checkCmdAliases(args, 1, "modify")) {
                    if (!StringUtils.isEmpty(args[2])) {
                        possibleResults.addAll(Arrays.asList("members", "claim", "unclaim", "set"));
                    }
                } else if (Utils.checkCmdAliases(args, 1, "view")) {
                    if (!StringUtils.isEmpty(args[2])) {
                        possibleResults.addAll(Arrays.asList("members", "plots"));
                    }
                } else if (Utils.checkCmdAliases(args, 1, "teleport")) {
                    if (!StringUtils.isEmpty(args[2])) {
                        possibleResults.addAll(Arrays.asList("spawn", "plot"));
                    }
                }
            } else if (Utils.checkCmdAliases(args, 0, "spawner")) {
                if (Utils.checkCmdAliases(args, 1, "give", "set")) {
                    if (!StringUtils.isEmpty(args[2])) {
                        for (Material material : Material.values()) {
                            if (material.isBlock()) {
                                possibleResults.add(material.name().toLowerCase());
                            }
                        }
                    }
                }
            } else if (Utils.checkCmdAliases(args, 0, "whitelist")) {
                if (StringUtils.isNotEmpty(args[1])) {
                    if (Utils.checkCmdAliases(args, 2, "addplayer", "ap", "removeplayer", "rp")) {
                        Whitelist whitelist = plugin.getWhitelistManager().getWhitelist(args[1]);
                        if (whitelist != null) {
                            if (Utils.checkCmdAliases(args, 2, "addplayer", "ap")) {
                                for (RealmProfile profile : plugin.getProfileManager().getProfiles()) {
                                    if (!whitelist.isAllowed(profile)) {
                                        possibleResults.add(profile.getName());
                                    }
                                }
                            } else {
                                for (UUID uuid : whitelist.getAllowedPlayers()) {
                                    RealmProfile profile = plugin.getProfileManager().getProfile(uuid);
                                    possibleResults.add(profile.getName());
                                }
                            }
                        }
                    }
                }
            }
    
            results.addAll(getResults(args[3], possibleResults));
        } else if (args.length == 5) {
            if (Utils.checkCmdAliases(args, 0, "hamlets", "hamlet", "h")) {
                if (Utils.checkCmdAliases(args, 1, "modify")) {
                    if (!StringUtils.isEmpty(args[2])) {
                        if (Utils.checkCmdAliases(args, 3, "members")) {
                            possibleResults.addAll(Arrays.asList("add", "remove", "setrank"));
                        } else if (Utils.checkCmdAliases(args, 3, "set")) {
                            possibleResults.addAll(Arrays.asList("spawnpoint", "name", "leader"));
                        }
                    }
                } else if (Utils.checkCmdAliases(args, 1, "teleport")) {
                    if (!StringUtils.isEmpty(args[2])) {
                        if (Utils.checkCmdAliases(args, 3, "plot")) {
                            Hamlet hamlet = (Hamlet) plugin.getTerritoryManager().getTerritory(args[2].replace("_", " "));
                            if (hamlet != null) {
                                for (ClaimedPlot plot : hamlet.getPlots()) {
                                    possibleResults.add(plot.getPlot().getUniqueId());
                                }
                            }
                        }
                    }
                }
            }
            
            results.addAll(getResults(args[4], possibleResults));
        } else if (args.length == 6) {
            if (Utils.checkCmdAliases(args, 0, "hamlets", "hamlet", "h")) {
                if (Utils.checkCmdAliases(args, 1, "modify")) {
                    if (!StringUtils.isEmpty(args[2])) {
                        if (Utils.checkCmdAliases(args, 3, "members")) {
                            Hamlet hamlet = (Hamlet) plugin.getTerritoryManager().getTerritory(args[2].replace("_", " "));
                            if (hamlet != null) {
                                if (Utils.checkCmdAliases(args, 4, "add")) {
                                    for (RealmProfile profile : plugin.getProfileManager().getProfiles()) {
                                        if (!hamlet.isMember(profile)) {
                                            possibleResults.add(profile.getName());
                                        }
                                    }
                                } else if (Utils.checkCmdAliases(args, 4, "remove", "setrank")) {
                                    for (Member member : hamlet.getMembers()) {
                                        possibleResults.add(member.getName());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            results.addAll(getResults(args[5], possibleResults));
        }
        
        return results;
    }
}