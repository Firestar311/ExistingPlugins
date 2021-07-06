package com.stardevmc.titanterritories.core.controller;

import com.firestar311.lib.pagination.*;
import com.firestar311.lib.player.User;
import com.firestar311.lib.util.Utils;
import com.stardevmc.chat.api.DefaultRoles;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.enums.ElectionReason;
import com.stardevmc.titanterritories.core.objects.enums.Permission;
import com.stardevmc.titanterritories.core.objects.holder.*;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.member.Member;
import com.stardevmc.titanterritories.core.objects.member.UserBan;
import com.stardevmc.titanterritories.core.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.*;

@SuppressWarnings("rawtypes")
public class UserController<T extends IHolder, H extends IUser> extends Controller<T> {
    private Set<H> users = new HashSet<>();
    private Set<UserBan> banned = new HashSet<>();
    private boolean locked = false;
    private UUID lockedBy = null;
    
    public UserController(T holder) {
        super(holder);
    }
    
    private UserController() {}
    
    public void handleCommand(Command cmd, IHolder holder, IUser user, String[] args) {
        if (!(args.length > 1)) {
            user.sendMessage("&cYou must provide a subcommand.");
            return;
        }
        
        if (Utils.checkCmdAliases(args, 1, "list")) {
            Paginator<H> paginator = PaginatorFactory.generatePaginator(7, getUsers(), new HashMap<DefaultVariables, String>() {{
                put(DefaultVariables.COMMAND, "/" + cmd.getName() +  " " + args[0] + "  list");
                put(DefaultVariables.TYPE, "Users");
            }});
            
            if (args.length > 2) {
                paginator.display(user.getPlayer(), args[2]);
            } else {
                paginator.display(user.getPlayer(), 1);
            }
        } else if (Utils.checkCmdAliases(args, 1, "view")) {
            if (!(args.length > 2)) {
                user.sendMessage("&cYou must provide a player to view.");
                return;
            }
            
            H target = get(args[2]);
            if (target == null) {
                user.sendMessage("&cThat person is not a member of your " + holder.getClass().getSimpleName());
                return;
            }
            
            user.sendMessage("&6Viewing information for " + target.getName());
            user.sendMessage("&7Rank: " + target.getRank().getDisplayName());
            user.sendMessage("&7Join Date: " + Constants.DATE_FORMAT.format(new Date(target.getJoinDate())));
        } else if (Utils.checkCmdAliases(args, 1, "remove", "r", "kick", "k")) {
            if (!(args.length > 2)) {
                user.sendMessage("&cYou must provide a name.");
                return;
            }
            
            H target = get(args[2]);
            if (target == null) {
                user.sendMessage("&cThat player is not a member of your " + holder.getClass().getSimpleName());
                return;
            }
            
            if (!user.hasPermission(Permission.REMOVE_USER)) {
                user.sendMessage("&cYou are not allowed to remove users.");
                return;
            }
            
            if (!(user.getRank().getOrder() < target.getRank().getOrder())) {
                user.sendMessage("&cYour rank is not high enough to remove that user.");
                return;
            }
            
            this.remove(target);
            holder.sendMemberMessage(target.getName() + " has been removed by " + user.getName());
        } else if (Utils.checkCmdAliases(args, 1, "ban", "b")) {
            if (!(args.length > 2)) {
                user.sendMessage("&cYou must provide a player to ban");
                return;
            }
            
            if (!user.hasPermission(Permission.BAN_PLAYERS)) {
                user.sendMessage("&cYou cannot ban other players.");
                return;
            }
    
            H localTarget = get(args[2]);
            if (localTarget != null) {
                if (localTarget.getRank().getOrder() >= user.getRank().getOrder()) {
                    user.sendMessage("&cYou cannot ban someone with a rank equal to or higher than your own.");
                    return;
                }
            }
    
            Member target = TitanTerritories.getInstance().getMemberManager().getMember(args[2]);
            if (target == null) {
                user.sendMessage("&cThe name you provided does not match a valid player.");
                return;
            }
            
            if (!(args.length > 3)) {
                user.sendMessage("&cYou must provided a valid reason.");
                return;
            }
            
            String reason = StringUtils.join(args, " ", 3, args.length);
            if (StringUtils.isEmpty(reason)) {
                user.sendMessage("&cThere was a problem parsing your reason.");
                return;
            }
            
            UserBan userBan = new UserBan(target.getUniqueId(), user.getUniqueId(), reason);
            this.banned.add(userBan);
            setLocked(true, null);
            
            if (holder instanceof Kingdom) {
                Kingdom kingdom = (Kingdom) holder;
    
                for (Town town : kingdom.getTownHandler().getTowns()) {
                    if (town.getBaron().getUser().getUniqueId().equals(target.getUniqueId())) {
                        town.getElectionController().startElection(ElectionReason.LEADER_KINGDOM_BANNED);
                    }
                }
    
                for (Colony colony : kingdom.getColonyHandler().getColonies()) {
                    if (colony.getChief().getUser().getUniqueId().equals(target.getUniqueId())) {
                        colony.getElectionController().startElection(ElectionReason.LEADER_KINGDOM_BANNED);
                    }
                }
            } else {
                if (holder.getLeader().getUser().getUniqueId().equals(target.getUniqueId())) {
                    holder.getElectionController().startElection(ElectionReason.LEADER_KINGDOM_BANNED);
                }
            }
            
            target.sendMessage("&4You have been banned from the " + holder.getClass().getSimpleName() + " by " + user.getName() + " for the reason: " + reason);
        } else if (Utils.checkCmdAliases(args, 1, "unban", "ub")) {
        
        } else if (Utils.checkCmdAliases(args, 1, "banlist")) {
        
        } else if (Utils.checkCmdAliases(args, 1, "lock")) {
        
        } else if (Utils.checkCmdAliases(args, 1, "unlock")) {
        
        }
    }
    
    public void add(H user) {
        this.users.add(user);
        try {
            holder.getChatroom().addMember(user.getUniqueId(), DefaultRoles.MEMBER);
        } catch (Exception e) {}
    }
    
    public void remove(H user) {
        this.users.remove(user);
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    public void remove(UUID uniqueId) {
        remove(get(uniqueId));
    }
    
    public List<H> getUsers() {
        return new ArrayList<>(users);
    }
    
    public H get(UUID uuid) {
        if (holder.getLeader() != null) {
            if (holder.getLeader().getUser() != null) {
                if (holder.getLeader().getUser().getUniqueId().equals(uuid)) {
                    return (H) holder.getLeader().getUser();
                }
            }
        }
        
        
        for (H user : users) {
            if (user.getUniqueId().equals(uuid)) {
                return user;
            }
        }
        return null;
    }
    
    public H get(Player player) {
        return get(player.getUniqueId());
    }
    
    public H get(String name) {
        User info = TitanTerritories.getInstance().getPlayerManager().getUser(name);
        return get(info.getUniqueId());
    }
    
    public Collection<H> getOnlineUsers() {
        List<H> onlineMembers = new ArrayList<>();
        for (H user : this.users) {
            if (user.isOnline()) onlineMembers.add(user);
        }
        return onlineMembers;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("amount", getUsers().size());
        for (int i = 0; i < getUsers().size(); i++) {
            serialized.put("user" + i, getUsers().get(i));
        }
        return serialized;
    }
    
    public static UserController deserialize(Map<String, Object> serialized) {
        int userAmount = (int) serialized.get("amount");
        List<IUser> users = new ArrayList<>();
        for (int i = 0; i < userAmount; i++) {
            users.add((IUser) serialized.get("user" + i));
        }
        UserController userController = new UserController();
        userController.users = new HashSet<>(users);
        return userController;
    }
    
    public List<UserBan> getBanned() {
        return new ArrayList<>(banned);
    }
    
    public void addBan(UserBan userBan) {
        this.banned.add(userBan);
    }
    
    public void setLocked(boolean locked, UUID lockedBy) {
        this.locked = locked;
        this.lockedBy = lockedBy;
    }
    
    public UUID getLockedBy() {
        return lockedBy;
    }
}