package com.stardevmc.titanterritories.core.objects.member;

import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.enums.Permission;
import com.stardevmc.titanterritories.core.objects.holder.Colony;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.Invite;
import com.stardevmc.titanterritories.core.objects.kingdom.Rank;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;

public class Colonist implements ConfigurationSerializable, IUser {
    
    private Member member;
    private long joinDate;
    private Colony colony;
    private Rank rank;
    private Invite acceptedInvite;
    
    public Colonist(Member member, long joinDate, Colony colony, Rank rank, Invite acceptedInvite) {
        this.member = member;
        this.joinDate = joinDate;
        this.colony = colony;
        this.rank = rank;
        this.acceptedInvite = acceptedInvite;
    }
    
    public Colonist(Member member, Colony colony) {
        this(member, System.currentTimeMillis(), colony, colony.getRankController().getDefaultRank(), null);
    }
    
    public Colonist(Member member) {
        this(member, System.currentTimeMillis(), null, null, null);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("member", member.getUniqueId().toString());
        serialized.put("joinDate", this.joinDate);
        serialized.put("colony", this.colony.getUniqueId().toString());
        serialized.put("rank", this.rank.getName());
        serialized.put("invite", this.acceptedInvite);
        return serialized;
    }
    
    public static Colonist deserialize(Map<String, Object> serialized) {
        Member member = TitanTerritories.getInstance().getMemberManager().getMember(UUID.fromString((String) serialized.get("member")));
        long joinDate = Long.parseLong((String) serialized.get("joinDate"));
        Colony colony = TitanTerritories.getInstance().getColonyManager().getColony(UUID.fromString((String) serialized.get("colony")));
        Rank rank = colony.getRankController().getRank((String) serialized.get("rank"));
        Invite invite = (Invite) serialized.get("invite");
        return new Colonist(member, joinDate, colony, rank, invite);
    }
    
    public long getJoinDate() {
        return joinDate;
    }
    
    public Colony getColony() {
        return colony;
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public Member getMember() {
        return member;
    }
    
    public Invite getAcceptedInvite() {
        return acceptedInvite;
    }
    
    public void setColony(Colony colony) {
        this.colony = colony;
    }
    
    public void setRank(Rank rank) {
        this.rank = rank;
    }
    
    public void setAcceptedInvite(Invite acceptedInvite) {
        this.acceptedInvite = acceptedInvite;
    }
    
    public <T extends IHolder> void setHolder(T holder) {
        this.colony = (Colony) holder;
    }
    
    public UUID getUniqueId() {
        return this.member.getUniqueId();
    }
    
    public void sendMessage(String message) {
        this.member.sendMessage(message);
    }
    
    public void sendMessage(BaseComponent... components) {
        this.member.sendMessage(components);
    }
    
    public boolean hasPermission(Permission permission) {
        Citizen citizen = getColony().getKingdom().getUserController().get(this.member.getUniqueId());
        if (citizen.getRank().hasPermission(permission)) {
            return true;
        }
        
        return getRank().hasPermission(permission);
    }
    
    public Player getPlayer() {
        return member.getPlayer();
    }
    
    public Location getLocation() {
        return member.getLocation();
    }
    
    public void teleport(Location location) {
        member.teleport(location);
    }
    
    public boolean isOnline() {
        return member.isOnline();
    }
    
    public boolean isLeader() {
        return colony.getChief().getUser().getUniqueId().equals(this.member.getUniqueId());
    }
    
    public String getName() {
        return this.member.getName();
    }
    
    public void setJoinDate(long joinDate) {
        this.joinDate = joinDate;
    }
}