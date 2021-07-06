package com.stardevmc.titanterritories.core.objects.interfaces;

import com.firestar311.lib.pagination.IElement;
import com.stardevmc.titanterritories.core.objects.enums.Permission;
import com.stardevmc.titanterritories.core.objects.kingdom.Invite;
import com.stardevmc.titanterritories.core.objects.kingdom.Rank;
import com.stardevmc.titanterritories.core.objects.member.Member;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface IUser extends IElement {
    
    boolean isOnline();
    boolean isLeader();
    String getName();
    UUID getUniqueId();
    void sendMessage(String message);
    void sendMessage(BaseComponent... components);
    boolean hasPermission(Permission permission);
    Player getPlayer();
    Rank getRank();
    Member getMember();
    long getJoinDate();
    void setAcceptedInvite(Invite invite);
    <T extends IHolder> void setHolder(T holder);
    void setRank(Rank rank);
    void setJoinDate(long date);
    Location getLocation();
    void teleport(Location location);
    
    default String formatLine(String... args) {
        String name = isOnline() ? "&a" + getName() : "&c" + getName();
        return " &8- &7" + name + " &7-> " + getRank().getDisplayName();
    }
    
    static IUser createUser(Member member, IHolder holder, Rank rank, long joinDate, Invite acceptedInvite) {
        try {
            IUser user = holder.getUserClass().getDeclaredConstructor(Member.class).newInstance(member);
            user.setHolder(holder);
            user.setRank(rank);
            user.setJoinDate(joinDate);
            user.setAcceptedInvite(acceptedInvite);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return null;
    }
}