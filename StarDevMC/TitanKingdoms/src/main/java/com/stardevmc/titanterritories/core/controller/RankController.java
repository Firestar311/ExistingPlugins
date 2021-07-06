package com.stardevmc.titanterritories.core.controller;

import com.firestar311.lib.pagination.PaginatorFactory;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.objects.enums.Permission;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.Rank;
import com.stardevmc.titanterritories.core.objects.holder.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;

import java.util.*;

public class RankController<T extends IHolder> extends Controller<T> {
    private List<Rank> ranks = new ArrayList<>();
    private Rank defaultRank, leaderRank, allyRank, enemyRank, neutralRank;
    
    public RankController(T kingdom) {
        super(kingdom);
        
        this.defaultRank = new Rank("default", 100);
        this.defaultRank.getPermissionList().addPermission(Permission.SPAWNPOINT, Permission.BUILD, Permission.DEPOSIT, Permission.CONTAINER, Permission.REDSTONE, Permission.DOOR);
        
        String name = "", displayName = "", prefix = "";
        if (holder != null) {
            if (holder instanceof Kingdom) {
                name = "monarch";
                displayName = "Monarch";
                prefix = "&6[Monarch] ";
            } else if (holder instanceof Town) {
                name = "baron";
                displayName = "Baron";
                prefix = "&6[Baron] ";
            } else if (holder instanceof Colony) {
                name = "chief";
                displayName = "Chief";
                prefix = "&6[Chief] ";
            }
        }
        this.leaderRank = new Rank(name, -1);
        this.leaderRank.setDisplayName(displayName);
        this.leaderRank.setPrefix(prefix);
        this.leaderRank.getPermissionList().addPermission(Permission.values());
        
        this.allyRank = new Rank("ally", 101);
        allyRank.getPermissionList().addPermission(Permission.SPAWNPOINT, Permission.BUILD, Permission.DEPOSIT);
        
        this.neutralRank = new Rank("neutral", 102);
        
        this.enemyRank = new Rank("enemy", 1000);
    }
    
    private RankController() {
        this(null);
    }
    
    public void handleCommand(Command cmd, IHolder holder, IUser user, String[] args) {
        if (!user.hasPermission(Permission.MANAGE_RANKS)) {
            user.sendMessage("&cYou do not have permission to manage the kingdom's ranks");
            return;
        }
    
        if (!(args.length > 1)) {
            user.sendMessage("&cYou must provide a sub command");
            return;
        }
    
        if (Utils.checkCmdAliases(args, 1, "create", "c")) {
            if (!(args.length > 3)) {
                user.sendMessage("&cYou must provide a rank name and the order in which to assign priority");
                return;
            }
        
            String name = args[2];
            if (getRank(name) != null) {
                user.sendMessage("&cA rank by that name already exists");
                return;
            }
        
            int order;
            try {
                order = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                user.sendMessage("&cThe value you provided for the order is not a valid number.");
                return;
            }
        
            if (order < 0) {
                user.sendMessage("&cThe value for the order must be 0 or positive");
                return;
            }
        
            if (isBaseRank(name)) {
                user.sendMessage("&cYou are not allowed to use that name as that is a base rank, please edit instead of adding.");
                return;
            }
        
            Rank rank = new Rank(name, order);
            addRank(rank);
            user.sendMessage("&aYou added the rank " + name + " to your kingdom.");
        } else if (Utils.checkCmdAliases(args, 1, "delete", "d", "remove", "r")) {
            if (!(args.length > 2)) {
                user.sendMessage("&cYou must provide a rank to remove");
                return;
            }
        
            Rank rank = getRank(args[2].toLowerCase());
            if (rank == null) {
                user.sendMessage("&cYou provided an invalid rank name.");
                return;
            }
        
            if (isBaseRank(rank.getName())) {
                user.sendMessage("&cYou are not allowed to delete that rank because it is a base rank.");
                return;
            }
        
            List<IUser> users = holder.getUserController().getUsers();
            for (IUser km : users) {
                if (km.getRank().equals(rank)) {
                    km.setRank(getDefaultRank());
                    km.sendMessage(Utils.color("&aYou have been moved to the default rank because your rank was deleted."));
                }
            }
        
            removeRank(rank);
            user.sendMessage("&aYou deleted the rank " + rank.getName());
        } else if (Utils.checkCmdAliases(args, 1, "set", "s")) {
            if (!(args.length > 3)) {
                user.sendMessage("&cYou must provide a member and a rank.");
                return;
            }
        
            IUser target = holder.getUserController().get(args[2]);
            if (target == null) {
                user.sendMessage("&cCould not find a member by that name.");
                return;
            }
        
            if (target.getRank().getOrder() <= user.getRank().getOrder()) {
                user.sendMessage("&cYou are not allowed to change the rank of someone with a rank equal to or higher than yours.");
                return;
            }
        
            Rank rank = getRank(args[3]);
            if (rank == null) {
                user.sendMessage("&cCould not find a rank with that name.");
                return;
            }
        
            target.setRank(rank);
            target.sendMessage("&aYour rank was changed to " + rank.getDisplayName() + " by " + user.getName());
            user.sendMessage("&aYou changed " + target.getName() + "'s rank to " + rank.getDisplayName());
        } else if (Utils.checkCmdAliases(args, 1, "reset", "rs")) {
            if (!(args.length > 2)) {
                user.sendMessage("&cYou must provide a member to reset their rank.");
                return;
            }
        
            IUser target = holder.getUserController().get(args[2]);
            if (target == null) {
                user.sendMessage("&cCould not find a member by that name.");
                return;
            }
        
            if (target.getRank().getOrder() <= user.getRank().getOrder()) {
                user.sendMessage("&cYou are not allowed to reset the rank of someone with a rank equal to or higher than yours.");
                return;
            }
        
            target.setRank(getDefaultRank());
            target.sendMessage("&aYour rank was reset to the default rank.");
            user.sendMessage("&aYou reset the rank of " + target.getName());
        } else if (Utils.checkCmdAliases(args, 1, "edit", "e")) {
            if (!(args.length > 4)) {
                user.sendMessage("&cNot enough arguments.");
                return;
            }
        
            Rank rank = getRank(args[2]);
            if (rank == null) {
                user.sendMessage("&cThe name you provided did not match a rank.");
                return;
            }
        
            if (rank.getOrder() <= user.getRank().getOrder()) {
                if (!user.isLeader()) {
                    user.sendMessage("&cYou are not allowed to edit that rank because it is the one you hold, or higher than your rank.");
                    return;
                }
            }
        
            if (Utils.checkCmdAliases(args, 3, "name", "n")) {
                if (isBaseRank(rank.getName())) {
                    user.sendMessage("&cYou are not allowed to edit the name of a Base Rank.");
                    return;
                }
            
                String previousName = rank.getName();
                rank.setName(args[4].toLowerCase());
                user.sendMessage("&aYou changed the name of the rank " + previousName + " to " + rank.getName());
            } else if (Utils.checkCmdAliases(args, 3, "displayname", "dn")) {
                String displayName = args[4];
                if (isLeaderRank(rank.getName())) {
                    if (!displayName.equalsIgnoreCase("monarch") || !displayName.equalsIgnoreCase("king") || !displayName.equalsIgnoreCase("queen")) {
                        user.sendMessage("&cThe Monarch Rank can only have &aMonarch&c, &aKing &cor &aQueen &cas the display name.");
                        return;
                    }
                }
                rank.setDisplayName(displayName);
                user.sendMessage("&aYou changed the display name of the rank " + rank.getName() + " to " + rank.getDisplayName());
            } else if (Utils.checkCmdAliases(args, 3, "prefix", "p")) {
                String prefix = StringUtils.join(args, " ", 4, args.length);
                if (isLeaderRank(rank.getName())) {
                    String lowerPrefix = prefix.toLowerCase();
                    if (!lowerPrefix.contains("monarch") || !lowerPrefix.contains("king") || !lowerPrefix.contains("queen")) {
                        user.sendMessage("&cThe Monarch Rank's prefix must have one of the following: &aMonarch&c, &aKing &cor &aQueen");
                        return;
                    }
                }
            
                rank.setPrefix(prefix);
                user.sendMessage("&aYou changed " + rank.getName() + "'s &aprefix to " + rank.getPrefix());
            } else if (Utils.checkCmdAliases(args, 3, "order", "o")) {
                if (rank.getName().equalsIgnoreCase(getLeaderRank().getName())) {
                    user.sendMessage("&cYou cannot modify the order of the Monarch rank.");
                    return;
                }
            
                int order;
                try {
                    order = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    user.sendMessage("&cThe value that you provided for the order is not a valid number.");
                    return;
                }
            
                int oldOrder = rank.getOrder();
                rank.setOrder(order);
                user.sendMessage("&aYou changed " + rank.getDisplayName() + "'s &aorder from " + oldOrder + " to " + order);
            } else if (Utils.checkCmdAliases(args, 3, "permissions", "perms", "perm")) {
                if (!(args.length > 4)) {
                    user.sendMessage("&cUsage: /kingdom rank edit " + rank.getName() + " permissions <subcommand> <values...>");
                    return;
                }
            
                if (Utils.checkCmdAliases(args, 4, "add", "a")) {
                    if (isLeaderRank(rank.getName())) {
                        user.sendMessage("&cYou are not allowed to modify the Monarch rank's permissions");
                        return;
                    }
                    List<Permission> permsToAdd = new ArrayList<>();
                    for (int i = 5; i < args.length; i++) {
                        try {
                            permsToAdd.add(Permission.valueOf(args[i]));
                        } catch (IllegalArgumentException e) {
                            user.sendMessage("&cThe value " + args[i] + " was not a valid permission.");
                        }
                    }
                    permsToAdd.forEach(perm -> {
                        if (!rank.getPermissionList().hasPermission(perm)) {
                            rank.getPermissionList().addPermission(perm);
                            user.sendMessage("&aYou added the permission " + perm.name() + " to the rank " + rank.getDisplayName());
                        } else {
                            user.sendMessage("&aThe rank " + rank.getDisplayName() + " &aalready had the permission " + perm.name());
                        }
                    });
                } else if (Utils.checkCmdAliases(args, 4, "remove", "r")) {
                    if (isLeaderRank(rank.getName())) {
                        user.sendMessage("&cYou are not allowed to modify the Monarch rank's permissions");
                        return;
                    }
                    List<Permission> permsToRemove = new ArrayList<>();
                    for (int i = 5; i < args.length; i++) {
                        try {
                            permsToRemove.add(Permission.valueOf(args[i]));
                        } catch (IllegalArgumentException e) {
                            user.sendMessage("&cThe value " + args[i] + " was not a valid permission.");
                        }
                    }
                    permsToRemove.forEach(perm -> {
                        rank.getPermissionList().removePermission(perm);
                        user.sendMessage("&aYou removed the permission " + perm.name() + " from the rank " + rank.getDisplayName());
                    });
                } else if (Utils.checkCmdAliases(args, 4, "list", "l")) {
                    if (isLeaderRank(rank.getName())) {
                        user.sendMessage("&aThe Monarch Rank holds all permissions.");
                        return;
                    }
                
                    user.sendMessage("&aThe " + rank.getDisplayName() + " rank holds the following permission(s): " + rank.getPermissionList().toString());
                }
            }
        } else if (Utils.checkCmdAliases(args, 1, "list", "l")) {
            PaginatorFactory<Rank> factory = new PaginatorFactory<>();
            factory.setMaxElements(7).setHeader("List of ranks for " + holder.getName() + " &e({pagenumber}/{totalpages})").setFooter("&7Type /kingdom ranks list {nextpage} for more");
            List<Rank> ranks = getRanks();
            ranks.forEach(factory::addElement);
            int page = 1;
            if (args.length > 2) {
                try {
                    page = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    user.sendMessage("&cThe value for the page was not a valid number.");
                    return;
                }
            }
            factory.build().display(user.getPlayer(), page);
        } else if (Utils.checkCmdAliases(args, 1, "view", "v")) {
            if (!(args.length > 2)) {
                user.sendMessage("&cYou must provide a rank to view.");
                return;
            }
            Rank rank = getRank(args[2]);
            if (rank == null) {
                user.sendMessage("&cYou provided an invalid rank name.");
                return;
            }
        
            user.sendMessage("&6Rank information for " + rank.getName() + "\n" + rank.toViewString());
        }
    }
    
    public void addRank(Rank rank) {
        this.ranks.add(rank);
    }
    
    public void removeRank(Rank rank) {
        this.ranks.remove(rank);
    }
    
    public List<Rank> getRanks() {
        List<Rank> ranks = new ArrayList<>(this.ranks);
        ranks.addAll(Arrays.asList(leaderRank, defaultRank));
        return new ArrayList<>(ranks);
    }
    
    public Rank getDefaultRank() {
        return defaultRank;
    }
    
    public void setDefaultRank(Rank defaultRank) {
        this.defaultRank = defaultRank;
    }
    
    public void setHolder(T holder) {
        super.setHolder(holder);
        if (leaderRank == null) {
            String name = "", displayName = "", prefix = "";
            if (holder != null) {
                if (holder instanceof Kingdom) {
                    name = "monarch";
                    displayName = "Monarch";
                    prefix = "&6[Monarch] ";
                } else if (holder instanceof Town) {
                    name = "baron";
                    displayName = "Baron";
                    prefix = "&6[Baron] ";
                } else if (holder instanceof Colony) {
                    name = "chief";
                    displayName = "Chief";
                    prefix = "&6[Chief] ";
                }
            }
            this.leaderRank = new Rank(name, -1);
            this.leaderRank.setDisplayName(displayName);
            this.leaderRank.setPrefix(prefix);
        }
    
        this.leaderRank.getPermissionList().addPermission(Permission.values());
    }
    
    public Rank getRank(String rank) {
        this.ranks.removeIf(rnk -> rnk == null);
        if (rank == null) return getDefaultRank();
        rank = ChatColor.stripColor(rank).toLowerCase();
        if (rank.equals(leaderRank.getName())) {
            return getLeaderRank();
        }
        
        if (rank.equals(defaultRank.getName())) {
            return defaultRank;
        }
        
        if (rank.equals(enemyRank.getName())) {
            return enemyRank;
        }
        
        if (rank.equals(allyRank.getName())) {
            return allyRank;
        }
        
        if (rank.equals(neutralRank.getName())) {
            return neutralRank;
        }
        for (Rank r : this.ranks) {
            if (r.getName().equalsIgnoreCase(rank)) {
                return r;
            }
        }
        return null;
    }
    
    public Rank getLeaderRank() {
        return leaderRank;
    }
    
    public Rank getAllyRank() {
        return allyRank;
    }
    
    public Rank getEnemyRank() {
        return enemyRank;
    }
    
    public Rank getNeutralRank() {
        return neutralRank;
    }
    
    public boolean isLeaderRank(String rank) {
        return rank.equalsIgnoreCase(getLeaderRank().getName());
    }
    
    public boolean isBaseRank(String rank) {
        if (rank.equalsIgnoreCase(leaderRank.getName())) {
            return true;
        } else if (rank.equalsIgnoreCase(defaultRank.getName())) {
            return true;
        } else if (rank.equalsIgnoreCase(enemyRank.getName())) {
            return true;
        } else if (rank.equalsIgnoreCase(neutralRank.getName())) {
            return true;
        } else return rank.equalsIgnoreCase(allyRank.getName());
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("amount", getRanks().size());
        serialized.put("defaultRank", getDefaultRank());
        Rank leaderRank = getLeaderRank();
        for (Permission value : Permission.values()) {
            leaderRank.removePermission(value);
        }
        serialized.put("leaderRank", getLeaderRank());
        serialized.put("enemyRank", getEnemyRank());
        serialized.put("allyRank", getAllyRank());
        serialized.put("neutralRank", getNeutralRank());
        for (int i = 0; i < getRanks().size(); i++) {
            serialized.put("rank" + i, getRanks().get(i));
        }
        return serialized;
    }
    
    public static RankController deserialize(Map<String, Object> serialized) {
        RankController controller = new RankController();
        
        if (serialized.containsKey("amount")) {
            int amount = (int) serialized.get("amount");
            List<Rank> ranks = new ArrayList<>();
            for (int i = 0; i < amount; i++) {
                ranks.add((Rank) serialized.get("rank" + i));
            }
    
            controller.defaultRank = (Rank) serialized.get("defaultRank");
            controller.leaderRank = (Rank) serialized.get("leaderRank");
            controller.enemyRank = (Rank) serialized.get("enemyRank");
            controller.allyRank = (Rank) serialized.get("allyRank");
            controller.neutralRank = (Rank) serialized.get("neutralRank");
    
            ranks.removeIf(rank -> controller.isBaseRank(rank.getName()));
            controller.ranks = ranks;
        }
        return controller;
    }
}