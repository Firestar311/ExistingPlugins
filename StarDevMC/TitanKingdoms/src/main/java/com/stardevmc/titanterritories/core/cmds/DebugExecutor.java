package com.stardevmc.titanterritories.core.cmds;

import com.firestar311.lib.pagination.*;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.debug.DebugCmd;
import com.stardevmc.titanterritories.core.debug.DebugStatus;
import com.stardevmc.titanterritories.core.objects.changelog.Version;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class DebugExecutor implements CommandExecutor {
    
    public static Map<String, String> variables = new HashMap<>();
    private Set<DebugCmd> cmds = new TreeSet<>();
    private TitanTerritories plugin;
    private boolean showVerified = true;
    
    public DebugExecutor(TitanTerritories plugin) {
        this.plugin = plugin;
        addCmd("createKingdom", "/kingdom create {kingdomName}");
        addCmd("joinKingdom", "/kingdom join {kingdomName}");
        addCmd("acceptKingdomAccept", "/kingdom accept {kingdomName}");
        addCmd("denyKingdomDeny", "/kingdom deny {kingdomName}");
        addCmd("disbandKingdom", "/kingdom disband");
        addCmd("kingdomInvite", "/kingdom invite {player}");
        addCmd("kingdomAnnouncements", "/kingdom announcements");
        addCmd("kingdomAnnouncementsCreate", "/kingdom announcements create {announcementText}");
        addCmd("kingdomAnnouncementsRemove", "/kingdom announcements remove {announcementId}");
        addCmd("kingdomAnnouncementsList", "/kingdom announcements list");
        addCmd("kingdomAnnouncementsView", "/kingdom announcements view {announcementId}");
        addCmd("kingdomAnnouncementsEditOrder", "/kingdom announcements edit {announcementId} order {newOrder}");
        addCmd("kingdomAnnouncementsEditMsg", "/kingdom announcements edit {announcementId} message {newMessage}");
        addCmd("kingdomAnnouncementsSetInterval", "/kingdom announcements setinterval {newInterval}");
        addCmd("kingdomAnnouncementsStart", "/kingdom announcements start");
        addCmd("kingdomAnnouncementsStop", "/kingdom announcements stop");
        addCmd("kingdomClaim", "/kingdom claim");
        addCmd("kingdomUnclaim", "/kingdom unclaim");
        addCmd("kingdomDeposit", "/kingdom deposit {money}");
        addCmd("kingdomWithdraw", "/kingdom withdraw {money}");
        addCmd("kingdomTransactions", "/kingdom transactions");
        addCmd("kingdomBalance", "/kingdom balance");
        addCmd("kingdomFlagsAdd", "/kingdom flags add {flag}");
        addCmd("kingdomFlagsRemove", "/kingdom flags remove {flag}");
        addCmd("kingdomFlagsList", "/kingdom flags list");
        addCmd("kingdomMailList", "/kingdom mail");
        addCmd("kingdomMailRead", "/kingdom mail read {mailId}");
        addCmd("kingdomMailCompose", "/kingdom mail compose");
        addCmd("kingdomMailList", "/kingdom mail list");
        addCmd("kingdomRankCreate", "/kingdom rank create {rankName}");
        addCmd("kingdomRankDelete", "/kingdom rank delete {rankName}");
        addCmd("kingdomRankSet", "/kingdom rank set {player} {rankName}");
        addCmd("kingdomRankReset", "/kingdom rank reset {player}");
        addCmd("kingdomRankEditName", "kingdom rank edit name {rankName} {newRankName}");
        addCmd("kingdomRankEditDisplayName", "/kingdom rank displayname {rankName} {displayName}");
        addCmd("kingdomRankEditPrefix", "/kingdom rank edit prefix {rankName} {newPrefix}");
        addCmd("kingdomRankEditOrder", "/kingdom rank edit order {rankName} {order}");
        addCmd("kingdomRankEditPermissionsAdd", "/kingdom rank edit permissions {rankName} add {permission}");
        addCmd("kingdomRankEditPermissionsRemove", "/kingdom rank edit permissions {rankName} remove {permission}");
        addCmd("kingdomRankEditPermissionsList", "/kingdom rank edit permissions {rankName} list");
        addCmd("kingdomRankList", "/kingdom rank list");
        addCmd("kingdomRankView", "/kingdom rank view {rankName}");
        addCmd("kingdomCitizensList", "/kingdom citizens list");
        addCmd("kingdomCitizensView", "/kingdom citizens view {player}");
        addCmd("kingdomCitizensRemove", "/kingdom citizens remove {player}");
        addCmd("kingdomWarpsList", "/kingdom warps list");
        addCmd("kingdomWarpsCreate", "/kingdom warps create {warpName}");
        addCmd("kingdomWarpsDelete", "/kingdom warps delete {warpName}");
        addCmd("kingdomWarpsEditName", "/kingdom warps edit {warpName} name {newName}");
        addCmd("kingdomWarpsEditLocation", "/kingdom warps edit {warpName} location");
        addCmd("kingdomWarpsView", "/kingdom warps view {warpName}");
        addCmd("kingdomWarpsViewVisits", "/kingdom warps viewvisits {warpName}");
        addCmd("kingdomWarp", "/kingdom warp {warpName}");
        addCmd("kindomExp", "/kingdom exp");
        addCmd("kingdomExpListActions", "/kingdom exp listactions");
        addCmd("kingdomExpAction", "/kingdom exp action {exptype} {expamount}");
        addCmd("kingdomElectionVote", "/kingdom election vote");
        addCmd("kingdomElectionLeaderboard", "/kingdom election leaderboard");
        addCmd("kingdomAdminElectionCreate", "/kingdom election create <reason>");
        addCmd("kingdomAdminExpAction", "/kingdom exp action <type> <amount>");
        addCmd("kingdomInviteList", "/kingdom invite list");
        addCmd("kingdomTransfer", "/kingdom transfer <player>");
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        executePlayer((Player) sender, args);
        return true;
    }
    
    public void executePlayer(Player player, String[] args) {
        if (!(args.length > 0)) {
            player.sendMessage(Utils.color("&cYou do not have enough arguments."));
            return;
        }
        
        if (Utils.checkCmdAliases(args, 0, "load")) {
            this.load();
            player.sendMessage(Utils.color("&aLoad Complete"));
        } else if (Utils.checkCmdAliases(args, 0, "save")) {
            this.save();
            player.sendMessage(Utils.color("&aSave Complete"));
        } else if (Utils.checkCmdAliases(args, 0, "variable", "v")) {
            if (!(args.length > 1)) {
                player.sendMessage(Utils.color("&cUsage: /debug variable <subcommand> <arguments>"));
                return;
            }
            
            if (Utils.checkCmdAliases(args, 1, "set", "s")) {
                variables.put("{" + args[2] + "}", args[3]);
                player.sendMessage(Utils.color("&aYou set the variable " + args[2] + " to " + args[3]));
            } else if (Utils.checkCmdAliases(args, 1, "list", "l")) {
                variables.forEach((k, v) -> {
                    String message = "&a" + k + " &7-> ";
                    if (v == null) {
                        message += "&cNot Set";
                    } else {
                        message += "&e" + v;
                    }
                    player.sendMessage(Utils.color(message));
                });
            } else if (Utils.checkCmdAliases(args, 1, "analyze")) {
                this.analyze(player);
            }
        } else if (Utils.checkCmdAliases(args, 0, "list")) {
            Set<DebugCmd> filteredCmds;
            if (!showVerified) {
                filteredCmds = this.cmds.stream().filter(debugCmd -> !debugCmd.getStatus().equals(DebugStatus.VERIFIED)).collect(Collectors.toCollection(TreeSet::new));
            } else {
                filteredCmds = new TreeSet<>(this.cmds);
            }
            Paginator<DebugCmd> paginator = PaginatorFactory.generatePaginator(9, filteredCmds, new HashMap<DefaultVariables, String>() {{
                put(DefaultVariables.TYPE, "debug commands");
                put(DefaultVariables.COMMAND, "/debug list page");
            }});
            
            int page = 1;
            if (args.length > 1) {
                if (Utils.checkCmdAliases(args, 1, "page", "p")) {
                    try {
                        page = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        player.sendMessage("&cThe value for the page was not a valid number.");
                        return;
                    }
                }
            }
            paginator.display(player, page);
        } else if (Utils.checkCmdAliases(args, 0, "toggle")) {
            if (Utils.checkCmdAliases(args, 1, "showverified", "sv")) {
                this.showVerified = !showVerified;
                player.sendMessage(Utils.color("&aToggled &eshowVerified &ato &e" + showVerified));
            }
        } else if (Utils.checkCmdAliases(args, 0, "ack")) {
            Version currentVersion = TitanTerritories.getChangeLog().getCurrentVersion();
            if (currentVersion.getAcknowledged().contains(player.getUniqueId())) {
                player.sendMessage(Utils.color("&cYou have already acknowledged that version."));
                return;
            }
            
            currentVersion.addAcknoledged(player.getUniqueId());
            player.sendMessage(Utils.color("&aYou have acknowledged the version " + currentVersion.getNumber()));
        } else {
            DebugCmd targetCmd = null;
            for (DebugCmd cmd : this.cmds) {
                if (cmd.getName().equalsIgnoreCase(args[0])) {
                    targetCmd = cmd;
                    break;
                }
            }
            
            if (Utils.checkCmdAliases(args, 1, "setstatus")) {
                targetCmd.setStatus(DebugStatus.valueOf(args[2]));
                player.sendMessage(Utils.color("&eYou set the status of the command " + targetCmd.getName() + " to " + targetCmd.getStatus().getColor() + targetCmd.getStatus().name()));
            }
        }
    }
    
    public void load() {
        if (plugin.getConfig().contains("variables")) {
            for (String k : plugin.getConfig().getConfigurationSection("variables").getKeys(false)) {
                variables.put(k, plugin.getConfig().getString("variables." + k));
            }
        }
        
        if (plugin.getConfig().contains("cmds")) {
            for (String c : plugin.getConfig().getConfigurationSection("cmds").getKeys(false)) {
                DebugCmd loaded = new DebugCmd(c, plugin.getConfig().getString("cmds." + c + ".base"), plugin.getConfig().getInt("cmds." + c + ".order"));
                loaded.setStatus(DebugStatus.valueOf(plugin.getConfig().getString("cmds." + c + ".status")));
                this.cmds.remove(loaded);
                this.cmds.add(loaded);
            }
        }
        
        if (plugin.getConfig().contains("showverified")) {
            this.showVerified = plugin.getConfig().getBoolean("showverified");
        }
    }
    
    public void save() {
        for (Entry<String, String> entry : variables.entrySet()) {
            plugin.getConfig().set("variables." + entry.getKey(), entry.getValue());
        }
        
        for (DebugCmd cmd : this.cmds) {
            plugin.getConfig().set("cmds." + cmd.getName() + ".status", cmd.getStatus().name());
            plugin.getConfig().set("cmds." + cmd.getName() + ".base", cmd.getBaseCommand());
            plugin.getConfig().set("cmds." + cmd.getName() + ".order", cmd.getOrder());
        }
        
        plugin.getConfig().set("showverified", showVerified);
        plugin.saveConfig();
    }
    
    
    
    public void analyze(Player player) {
        for (DebugCmd cmd : cmds) {
            String baseCommand = cmd.getBaseCommand();
            boolean foundStart = false;
            String variable = "";
            char[] charArray = baseCommand.toCharArray();
            for (char c : charArray) {
                if (c == '{') {
                    foundStart = true;
                    variable += "{";
                } else if (c == '}') {
                    variable += "}";
                    if (!variables.containsKey(variable)) { variables.put(variable, null); }
                    player.sendMessage(Utils.color("&aFound the variable " + variable + " in the command " + cmd.getName()));
                    foundStart = false;
                    variable = "";
                } else {
                    if (foundStart) {
                        variable += c;
                    }
                }
            }
        }
    }
    
    public void addCmd(String name, String fullCmd) {
        int lastIndex = 0;
        for (DebugCmd cmd : this.cmds) {
            if (cmd.getOrder() > lastIndex) {
                lastIndex = cmd.getOrder();
            }
        }
        
        this.cmds.add(new DebugCmd(name, fullCmd, lastIndex + 1));
    }
}