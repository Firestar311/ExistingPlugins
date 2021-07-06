package com.kingrealms.realms.cmd;

import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.user.User;
import com.starmediadev.lib.util.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TeleportCommands extends BaseCommand {
    
    private Set<TPRequest> teleportRequests = new HashSet<>();
    
    public TeleportCommands() {
        new BukkitRunnable() {
            public void run() {
                Iterator<TPRequest> iterator = teleportRequests.iterator();
                while (iterator.hasNext()) {
                    TPRequest request = iterator.next();
                    RealmProfile actor = plugin.getProfileManager().getProfile(request.getActor());
                    RealmProfile target = plugin.getProfileManager().getProfile(request.getTarget());
                    
                    if (request.hasTimedOut() || !actor.isOnline() || !target.isOnline()) {
                        iterator.remove();
                        actor.sendMessage("&cThe request to " + target.getName() + " has timed out.");
                        target.sendMessage("&cThe request from " + actor.getName() + " has timed out.");
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        RealmProfile profile = plugin.getProfileManager().getProfile(sender);
        if (cmd.getName().equalsIgnoreCase("teleport")) {
            if (!sender.hasPermission("realms.teleport")) {
                if (!profile.getStaffMode().isActive()) {
                    sender.sendMessage(Utils.color("&cYou do not have permission to use that command."));
                    return true;
                }
            }
            
            if (!(sender instanceof Player)) {
                if (args.length != 2) {
                    sender.sendMessage(Utils.color("&cYou must provide two arguments in order to use that command."));
                    return true;
                }
            }
            
            if (args.length == 1) {
                if (!sender.hasPermission("realms.teleport.players")) {
                    sender.sendMessage(Utils.color("&cYou do not have permission to teleport to players."));
                    return true;
                }
                RealmProfile player = plugin.getProfileManager().getProfile(((Player) sender).getUniqueId());
                User target = plugin.getUserManager().getUser(args[0]);
                if (target == null) {
                    player.sendMessage(Utils.color("&cThe name you provided does not match a valid player."));
                    return true;
                }
                
                RealmProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());
                if (targetProfile == null) {
                    player.sendMessage(Utils.color("&cThat player has not joined the server."));
                    return true;
                }
                
                if (!checkNether(player, targetProfile.getLocation(), "You are not allowed to enter the nether.")) return true;
                
                if (targetProfile.isOnline()) {
                    player.teleport(targetProfile.getLocation());
                    player.sendMessage(Utils.color("&gTeleported to &h" + targetProfile.getName() + "&g."));
                } else {
                    if (!player.hasPermission("realms.teleport.others.offline")) {
                        player.sendMessage(Utils.color("&cYou do not have permission to teleport to offline players."));
                        return true;
                    }
                    
                    player.teleport(targetProfile.getLocation());
                    player.sendMessage(Utils.color("&gTeleported to &h" + targetProfile.getName() + "&g's last location."));
                }
            } else if (args.length == 2) {
                if (!sender.hasPermission("realms.teleport.others")) {
                    sender.sendMessage(Utils.color("&cYou do not have permission to use that command."));
                    return true;
                }
                
                RealmProfile target1 = plugin.getProfileManager().getProfile(args[0]);
                RealmProfile target2 = plugin.getProfileManager().getProfile(args[1]);
                
                if (target1 == null || target2 == null) {
                    if (target1 == null) {
                        sender.sendMessage(Utils.color("&cThe name " + args[0] + " did not match a valid player."));
                    }
                    
                    if (target2 == null) {
                        sender.sendMessage(Utils.color("&cThe name " + args[1] + " did not match a valid player."));
                    }
                    
                    return true;
                }
                
                boolean target1Online = target1.isOnline(), target2Online = target2.isOnline();
                if (!target1Online || !target2Online) {
                    if (!target1Online) {
                        sender.sendMessage(Utils.color("&c" + target1.getName() + " is not online."));
                    }
                    
                    if (!target2Online) {
                        sender.sendMessage(Utils.color("&c" + target2.getName() + " is not online."));
                    }
                    
                    return true;
                }
                
                if (!checkNether(target1, target2.getLocation(), target1.getName() + " cannot be teleported to the nether.")) return true;
    
                target1.teleport(target2.getLocation());
                sender.sendMessage(Utils.color("&gTeleported &h" + target1.getName() + " &gto &h" + target2.getName() + "&g."));
                target1.sendMessage("&gYou were teleported to &h" + target2.getName() + " &gby &h" + sender.getName());
                target2.sendMessage("&h" + target1.getName() + " &gwas teleported to you by &h" + sender.getName());
            } else if (args.length == 3) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Utils.color("&cOnly a player may use that command."));
                    return true;
                }
                
                if (!profile.hasPermission("realms.teleport.position")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                
                RealmProfile player = plugin.getProfileManager().getProfile(((Player) sender).getUniqueId());
                
                int x = getCoord(args[0], player.getLocation().getBlockX(), player);
                int y = getCoord(args[1], player.getLocation().getBlockY(), player);
                int z = getCoord(args[2], player.getLocation().getBlockZ(), player);
                
                if (x == -1 || y == -1 || z == -1) {
                    return true;
                }
                
                World world = player.getLocation().getWorld();
                float yaw = player.getLocation().getYaw();
                float pitch = player.getLocation().getPitch();
                
                player.teleport(new Location(world, x, y, z, yaw, pitch));
                player.sendMessage("&gTeleported to &h" + x + "&g, &h" + y + "&g, &h" + z + "&g.");
            }
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly a player may use that command."));
            return true;
        }
        
        RealmProfile player = plugin.getProfileManager().getProfile(((Player) sender).getUniqueId());
        List<TPRequest> teleportRequests = new ArrayList<>();
        
        if (cmd.getName().equalsIgnoreCase("tphere")) {
            if (!player.hasPermission("realms.teleport.tphere")) {
                player.sendMessage(Utils.color("&cYou do not have permission to use that command."));
                return true;
            }
            
            if (tphereCount(args, player, "realms.teleport.tphere.multiple")) { return true; }
            
            for (String name : args) {
                RealmProfile targetProfile = getTargetProfile(name, player);
                if (targetProfile == null) { return true; }
                
                if (!checkNether(targetProfile, player.getLocation(), targetProfile.getName() + " cannot be teleported into the nether.")) return true;
                
                targetProfile.teleport(player.getLocation());
                targetProfile.sendMessage("&gYou were teleported to &h" + player.getName());
                player.sendMessage("&gTeleported &h" + targetProfile.getName() + " &gto you.");
            }
        } else if (cmd.getName().equalsIgnoreCase("tpa")) {
            if (!(args.length > 0)) {
                player.sendMessage(Utils.color("&cYou must provide a name."));
                return true;
            }
            
            if (args.length > 1) {
                if (!player.hasPermission("realms.teleport.tpahere.multiple")) {
                    player.sendMessage(Utils.color("&cYou only have permission to teleport one player at a time."));
                    return true;
                }
            }
            
            for (String name : args) {
                RealmProfile targetProfile = getTargetProfile(name, player);
                if (targetProfile == null) { return false; }
                if (!checkTeleportRequests(targetProfile, player)) return true;
                this.teleportRequests.add(new TPRequest(player.getUniqueId(), targetProfile.getUniqueId(), System.currentTimeMillis()));
                String line = Utils.blankLine("&d", Constants.LINE_CHARS);
                player.sendMessage("&gTeleport request sent to " + targetProfile.getName(), "&gTo cancel type &a/tpacancel");
                targetProfile.sendMessage(line, Utils.getCenteredMessage("&h" + player.getName() + " &ghas requested to teleport to you."), Utils.getCenteredMessage("&gTo teleport, type &a/tpaccept " + player.getName()), Utils.getCenteredMessage("&gTo deny this request, type &c/tpdeny " + player.getName()), Utils.getCenteredMessage("&gIf you only have one request, you don't have to put a name."), Utils.getCenteredMessage("&gYou currently have: &h" + getTeleportRequestsByTarget(targetProfile.getUniqueId()).size() + " &grequest(s)."), Utils.getCenteredMessage("&gThis request will timeout after &h120 seconds&g."), line);
            }
        } else if (cmd.getName().equalsIgnoreCase("tpaccept") || cmd.getName().equalsIgnoreCase("tpdeny")) {
            for (TPRequest request : this.teleportRequests) {
                if (request.getTarget().equals(player.getUniqueId())) {
                    teleportRequests.add(request);
                }
            }
            
            if (teleportRequests.size() == 0) {
                player.sendMessage("&cNo one has requested to teleport to you, or a previous request timed out.");
                return true;
            }
            
            if (teleportRequests.size() > 1 && !(args.length > 0)) {
                player.sendMessage(Utils.color("&cYou have more than one teleport request, please specify a name."));
                return true;
            }
            
            TPRequest request = null;
            if (teleportRequests.size() == 1) {
                request = teleportRequests.get(0);
            } else {
                User user = plugin.getUserManager().getUser(args[0]);
                if (user == null) {
                    player.sendMessage("&cA player by that name does not exist.");
                    return true;
                }
                
                for (TPRequest r : teleportRequests) {
                    if (r.getActor().equals(user.getUniqueId())) {
                        request = r;
                        break;
                    }
                }
                
                if (request == null) {
                    player.sendMessage("&cCould not find a teleport request from " + user.getLastName());
                    return true;
                }
            }
            
            RealmProfile requester = plugin.getProfileManager().getProfile(request.getActor());
            if (!requester.isOnline()) {
                player.sendMessage("&cThe request from " + requester.getName() + " is no longer valid as they have left the server.");
                this.teleportRequests.remove(request);
                return true;
            }
            
            if (cmd.getName().equalsIgnoreCase("tpaccept")) {
                if (!request.isTpHere()) {
                    requester.teleport(player.getLocation());
                } else {
                    player.teleport(requester.getLocation());
                }
                player.sendMessage("&gYou have &aaccepted &h" + requester.getName() + "&g's teleport request.");
                requester.sendMessage("&h" + player.getName() + " &ghas &aaccepted &gyour teleport request.");
            } else if (cmd.getName().equalsIgnoreCase("tpdeny")) {
                player.sendMessage("&gYou have &cdenied &h" + requester.getName() + "&g's teleport request.");
                requester.sendMessage("&h" + player.getName() + " &ghas &cdenied &gyour teleport request.");
            }
            this.teleportRequests.remove(request);
        } else if (cmd.getName().equalsIgnoreCase("tpall")) {
            if (!player.hasPermission("realms.teleport.all")) {
                player.sendMessage("&cYou do not have permission to teleport all players.");
                return true;
            }
            
            String targetName;
            Location targetLocation;
            
            if (args.length != 0) {
                if (!player.hasPermission("realms.teleport.all.other")) {
                    player.sendMessage("&cYou do not have permission to teleport all players to someone else.");
                    return true;
                }
                
                User user = plugin.getUserManager().getUser(args[0]);
                if (user == null) {
                    player.sendMessage("&cThe target you provided is not a valid player.");
                    return true;
                }
                
                if (!user.isOnline()) {
                    player.sendMessage("&cThe target you provided is not online.");
                    return true;
                }
                
                targetLocation = user.getLocation();
                targetName = user.getLastName();
            } else {
                targetLocation = player.getLocation();
                targetName = player.getName();
            }
            
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.teleport(targetLocation);
                if (!p.getUniqueId().equals(player.getUniqueId())) {
                    if (targetName.equalsIgnoreCase(player.getName())) {
                        p.sendMessage(Utils.color("&h" + player.getName() + " &ghas teleported all players to &h" + targetName + "."));
                    } else {
                        p.sendMessage(Utils.color("&h" + targetName + " &ghas teleported all players to them."));
                    }
                } else {
                    p.sendMessage("&gYou have teleported all players to you.");
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("tpahere")) {
            if (tphereCount(args, player, "realms.teleport.tpahere.multiple")) { return true; }
            
            for (String name : args) {
                RealmProfile targetProfile = getTargetProfile(name, player);
                if (targetProfile == null) { return false; }
                if (!checkTeleportRequests(targetProfile, player)) return true;
                if (!checkNether(targetProfile, player.getLocation(), targetProfile.getName() + " cannot enter the nether.")) return true;
                this.teleportRequests.add(new TPRequest(player.getUniqueId(), targetProfile.getUniqueId(), System.currentTimeMillis(), true));
                String line = Utils.blankLine("&d", Constants.LINE_CHARS);
                player.sendMessage("&gTeleport request sent to " + targetProfile.getName(), "&gTo cancel type &a/tpacancel");
                targetProfile.sendMessage(line, Utils.getCenteredMessage("&h" + player.getName() + " &ghas requested that you teleport to them."), Utils.getCenteredMessage("&gTo teleport, type &a/tpaccept " + player.getName()), Utils.getCenteredMessage("&gTo deny this request, type &c/tpdeny " + player.getName()), Utils.getCenteredMessage("&gIf you only have one request, you can just type the command without the name."), Utils.getCenteredMessage("&gYou currently have: &h" + getTeleportRequestsByTarget(targetProfile.getUniqueId()).size() + " &grequests."), Utils.getCenteredMessage("&gThis request will timeout after &h120 seconds&g."), line);
            }
        } else if (cmd.getName().equalsIgnoreCase("tpacancel")) {
            for (TPRequest request : this.teleportRequests) {
                if (request.getActor().equals(player.getUniqueId())) {
                    teleportRequests.add(request);
                }
            }
            
            if (teleportRequests.size() == 0) {
                player.sendMessage("&cYou have not requested to teleport to anyone, or a previous request timed out.");
                return true;
            }
            
            if (teleportRequests.size() > 1 && !(args.length > 0)) {
                player.sendMessage(Utils.color("&cYou have more than one teleport request, please specify a name."));
                return true;
            }
            
            TPRequest request = null;
            if (teleportRequests.size() == 1) {
                request = teleportRequests.get(0);
            } else {
                User user = plugin.getUserManager().getUser(args[0]);
                if (user == null) {
                    player.sendMessage("&cA player by that name does not exist.");
                    return true;
                }
                
                for (TPRequest r : teleportRequests) {
                    if (r.getTarget().equals(user.getUniqueId())) {
                        request = r;
                        break;
                    }
                }
                
                if (request == null) {
                    player.sendMessage("&cCould not find a teleport request to " + user.getLastName());
                    return true;
                }
            }
            
            RealmProfile target = plugin.getProfileManager().getProfile(request.getTarget());
            player.sendMessage("&gYou have &dcancelled &gthe teleport request to &h" + target.getName() + "&g.");
            target.sendMessage("&h" + player.getName() + " &ghas &dcancelled &gtheir teleport request to you.");
            this.teleportRequests.remove(request);
        }
        
        return true;
    }
    
    private boolean checkTeleportRequests(RealmProfile targetProfile, RealmProfile player) {
        if (getTeleportRequest(player.getUniqueId(), targetProfile.getUniqueId()) != null) {
            player.sendMessage(Utils.color("&cYou already have an active telport request to " + targetProfile.getName()));
            return false;
        }
        return true;
    }
    
    private boolean tphereCount(String[] args, RealmProfile player, String permission) {
        if (!(args.length > 0)) {
            player.sendMessage(Utils.color("&cYou must provide a name."));
            return true;
        }
        
        if (args.length > 1) {
            if (!player.hasPermission(permission)) {
                player.sendMessage(Utils.color("&cYou only have permission to teleport one player at a time."));
                return true;
            }
        }
        return false;
    }
    
    private RealmProfile getTargetProfile(String name, RealmProfile player) {
        User user = plugin.getUserManager().getUser(name);
        if (user == null) {
            player.sendMessage("&cCould not find a player with the name " + name);
        }
        
        RealmProfile targetProfile = plugin.getProfileManager().getProfile(user.getUniqueId());
        if (targetProfile == null) {
            player.sendMessage("&cPlayer with the name " + name + " has not joined the server.");
        }
        
        if (!targetProfile.isOnline()) {
            player.sendMessage("&cPlayer with the name " + name + " is not online.");
        }
        
        return targetProfile;
    }
    
    public List<TPRequest> getTeleportRequestsByTarget(UUID uniqueId) {
        ArrayList<TPRequest> requests = new ArrayList<>();
        for (TPRequest request : this.teleportRequests) {
            if (request.getTarget().equals(uniqueId)) {
                requests.add(request);
            }
        }
        return requests;
    }
    
    public List<TPRequest> getTeleportRequestsByActor(UUID uniqueId) {
        ArrayList<TPRequest> requests = new ArrayList<>();
        for (TPRequest request : this.teleportRequests) {
            if (request.getActor().equals(uniqueId)) {
                requests.add(request);
            }
        }
        return requests;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> possibleResults = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            possibleResults.add(p.getName());
        }
        if (args.length > 0) {
            if (args.length == 1) {
                return Utils.getResults(args[0], possibleResults);
            } else if (args.length == 2) {
                return Utils.getResults(args[1], possibleResults);
            } else if (args.length == 3) {
                return Utils.getResults(args[2], possibleResults);
            }
        }
        return new ArrayList<>();
    }
    
    static class TPRequest {
        private static final long TIMEOUT = TimeUnit.SECONDS.toMillis(120);
        
        private UUID actor, target;
        private long date;
        private boolean tpHere;
        
        public TPRequest(UUID actor, UUID target, long date) {
            this.actor = actor;
            this.target = target;
            this.date = date;
            this.tpHere = false;
        }
        
        public TPRequest(UUID actor, UUID target, long date, boolean tpHere) {
            this.actor = actor;
            this.target = target;
            this.date = date;
            this.tpHere = tpHere;
        }
        
        public boolean isTpHere() {
            return tpHere;
        }
        
        public UUID getActor() {
            return actor;
        }
        
        public UUID getTarget() {
            return target;
        }
        
        public long getDate() {
            return date;
        }
        
        public boolean hasTimedOut() {
            return System.currentTimeMillis() > (this.date + TIMEOUT);
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }
            TPRequest tpRequest = (TPRequest) o;
            return Objects.equals(actor, tpRequest.actor) && Objects.equals(target, tpRequest.target);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(actor, target);
        }
    }
    
    public TPRequest getTeleportRequest(UUID actor, UUID target) {
        for (TPRequest request : this.teleportRequests) {
            if (request.getTarget().equals(target) && request.getActor().equals(actor)) {
                return request;
            }
        }
        
        return null;
    }
    
    private int getCoord(String arg, int start, RealmProfile player) {
        int coord;
        
        if (arg.startsWith("~")) {
            String a = arg.replace("~", "");
            if (StringUtils.isEmpty(a)) {
                coord = start;
            } else {
                int i;
                try {
                    i = Integer.parseInt(a);
                } catch (NumberFormatException e) {
                    player.sendMessage("&cThe value " + arg + " is not valid.");
                    return -1;
                }
                coord = start + i;
            }
        } else {
            try {
                coord = Integer.parseInt(arg);
            } catch (NumberFormatException e) {
                player.sendMessage("&cThe value " + arg + " is not valid.");
                return -1;
            }
        }
        
        return coord;
    }
    
    private boolean checkNether(RealmProfile profile, Location location, String message) {
        if (location.getWorld().getEnvironment() == Environment.NETHER) {
            if (!profile.isQuestComplete(new ID("enter_the_nether"))) {
                if (!profile.getStaffMode().isActive()) {
                    profile.sendMessage("&c" + message);
                    return false;
                }
            }
        }
        return true;
    }
}