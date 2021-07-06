package com.stardevmc.titanterritories.core.controller;

import com.firestar311.lib.pagination.*;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.enums.Permission;
import com.stardevmc.titanterritories.core.objects.holder.*;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.Invite;
import com.stardevmc.titanterritories.core.objects.member.Member;
import org.bukkit.command.Command;

import java.util.*;

public class InviteController<T extends IHolder> extends Controller<T> {
    private List<Invite> invites = new ArrayList<>();
    private TitanTerritories plugin = TitanTerritories.getInstance();
    
    public InviteController(T kingdom) {
        super(kingdom);
    }
    
    public InviteController() {}
    
    public static InviteController deserialize(Map<String, Object> serialized) {
        InviteController controller = new InviteController();
        List<Invite> invites = new ArrayList<>();
        if (serialized.containsKey("amount")) {
            int inviteAmount = (int) serialized.get("amount");
            for (int i = 0; i < inviteAmount; i++) {
                invites.add((Invite) serialized.get("invite" + i));
            }
    
            controller.invites = invites;
        }
        return controller;
    }
    
    public void handleCommand(Command cmd, IHolder holder, IUser user, String[] args) {
        if (Utils.checkCmdAliases(args, 0, "invite", "i")) {
            if (!user.hasPermission(Permission.INVITE)) {
                user.sendMessage(Utils.color("&cYou are not allowed to invite others to your " + holder.getClass().getSimpleName()));
                return;
            }
            
            if (Utils.checkCmdAliases(args, 0, "remove", "r")) {
                if (!user.hasPermission(Permission.REMOVE_INVITE)) {
                    user.sendMessage(Utils.color("&cYou are not allowed to remove invites."));
                    return;
                }
                
                if (!(args.length > 1)) {
                    user.sendMessage("&cYou must provide the name of the invited player to remove.");
                    return;
                }
                
                Member targetMember = plugin.getMemberManager().getMember(args[1]);
                if (targetMember == null) {
                    user.sendMessage("&cThat is not a valid username.");
                    return;
                }
                
                if (!hasBeenInvited(targetMember.getUniqueId())) {
                    user.sendMessage("&cThat player has not been invited to your " + holder.getClass().getSimpleName());
                    return;
                }
                
                Invite invite = getInvite(targetMember.getUniqueId());
                removeInvite(invite);
                user.sendMessage("&aYou have removed the invite to the player " + targetMember.getName());
                return;
            } else if (Utils.checkCmdAliases(args, 1, "list", "l")) {
                Paginator<Invite> paginator = PaginatorFactory.generatePaginator(7, getInvites(), new HashMap<DefaultVariables, String>() {{
                    put(DefaultVariables.COMMAND, "/" + cmd.getName() + " invite list");
                    put(DefaultVariables.TYPE, "Invites");
                }});
    
                if (args.length > 2) {
                    paginator.display(user.getPlayer(), args[2]);
                } else {
                    paginator.display(user.getPlayer(), 1);
                }
                return;
            }
            
            if (!(args.length > 1)) {
                user.sendMessage(Utils.color("&cYou must provide a user to invite."));
                return;
            }
            
            Member target = plugin.getMemberManager().getMember(args[1]);
            if (target == null) {
                user.sendMessage("&cThe name you provided is not a valid player.");
                return;
            }
            
            if (holder.getUserController().get(target.getUniqueId()) != null) {
                user.sendMessage("&cThe user you provided is already a member of your " + holder.getClass().getSimpleName());
                return;
            }
            
            if (holder instanceof Town) {
                Town town = (Town) holder;
                if (town.getKingdom() != null) {
                    if (town.getKingdom().getUserController().get(target.getUniqueId()) == null) {
                        user.sendMessage("&cThat player is not a member of your " + holder.getClass().getSimpleName());
                    }
                }
            }
            if (holder instanceof Colony) {
                Colony colony = (Colony) holder;
                if (colony.getKingdom().getUserController().get(target.getUniqueId()) == null) {
                    user.sendMessage("&cThat player is not a member of your " + holder.getClass().getSimpleName());
                }
            }
            
            if (hasBeenInvited(target.getUniqueId())) {
                user.sendMessage(Utils.color("&cThat user has already been invited to your " + holder.getClass().getSimpleName()));
                return;
            }
            
            Invite invite = new Invite(target.getUniqueId(), user.getUniqueId(), System.currentTimeMillis());
            addInvite(invite);
            holder.sendMemberMessage(target.getName() + " has been invited to your " + holder.getClass().getSimpleName() + " by " + user.getName());
            target.sendMessage("&aYou have been invited to the " + holder.getClass().getSimpleName() + " " + holder.getName() + " by " + user.getName());
        } else if (Utils.checkCmdAliases(args, 0, "accept", "a")) {
            if (!(args.length > 1)) {
                user.sendMessage(Utils.color("&cYou must provide a " + holder.getClass().getSimpleName() + " name to accept an invite to."));
                return;
            }
            
            Kingdom kingdom = plugin.getKingdomManager().getKingdom(args[1]);
            if (kingdom == null) {
                user.sendMessage(Utils.color("&cYou provided an invalid " + holder.getClass().getSimpleName() + " name"));
                return;
            }
            
            if (!kingdom.getInviteController().hasBeenInvited(user.getUniqueId())) {
                user.sendMessage(Utils.color("&cYou have not been invited to that " + holder.getClass().getSimpleName() + "."));
                return;
            }
            
            Invite invite = kingdom.getInviteController().getInvite(user.getUniqueId());
            
            Member member = ((Member) user);
            IUser targetUser = IUser.createUser(member, holder, holder.getRankController().getDefaultRank(), System.currentTimeMillis(), invite);
            holder.getUserController().add(targetUser);
            kingdom.sendMemberMessage(user.getName() + " has joined the " + holder.getClass().getSimpleName() + " from an invite by " + invite.getInviterMember().getName());
            kingdom.getInviteController().removeInvite(invite);
        } else if (Utils.checkCmdAliases(args, 0, "deny", "d")) {
            if (!(args.length > 1)) {
                user.sendMessage(Utils.color("&cYou must provide a " + holder.getClass().getSimpleName() + " name to deny the invite from."));
                return;
            }
            
            if (args[1].equalsIgnoreCase("all")) {
                int removalCount = 0;
                for (Kingdom kingdom : plugin.getKingdomManager().getKingdoms()) {
                    if (kingdom.getInviteController().hasBeenInvited(user.getUniqueId())) {
                        Invite invite = kingdom.getInviteController().getInvite(user.getUniqueId());
                        kingdom.sendMemberMessage(user.getName() + " has denied the invite from " + invite.getInviterMember().getName());
                        kingdom.getInviteController().removeInvite(invite);
                        removalCount += 1;
                    }
                }
                
                if (removalCount == 0) {
                    user.sendMessage(Utils.color("&cYou had no pending invites to any kingdom."));
                    return;
                }
                
                user.sendMessage(Utils.color("&aYou denied the invite(s) to " + removalCount + " " + holder.getClass().getSimpleName() + "(s)"));
                return;
            }
            
            Kingdom kingdom = plugin.getKingdomManager().getKingdom(args[1]);
            if (kingdom.getInviteController().hasBeenInvited(user.getUniqueId())) {
                Invite invite = kingdom.getInviteController().getInvite(user.getUniqueId());
                kingdom.sendMemberMessage(user.getName() + " has denied the invite from " + invite.getInviterMember().getName());
                kingdom.getInviteController().removeInvite(invite);
            } else {
                user.sendMessage(Utils.color("&cYou do not have a pending invite to that " + holder.getClass().getSimpleName() + "."));
            }
        }
    }
    
    public boolean hasBeenInvited(UUID uuid) {
        for (Invite invite : invites) {
            if (invite.getInvited().equals(uuid)) {
                return true;
            }
        }
        return false;
    }
    
    public void addInvite(Invite invite) {
        this.invites.add(invite);
    }
    
    public Invite getInvite(UUID uuid) {
        for (Invite invite : invites) {
            if (invite.getInvited().equals(uuid)) {
                return invite;
            }
        }
        return null;
    }
    
    public void removeInvite(Invite invite) {
        this.invites.remove(invite);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("amount", getInvites().size());
        for (int i = 0; i < getInvites().size(); i++) {
            serialized.put("invite" + i, getInvites().get(i));
        }
        return serialized;
    }
    
    public List<Invite> getInvites() {
        return new ArrayList<>(invites);
    }
}