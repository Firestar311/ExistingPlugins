package com.stardevmc.titanterritories.core.objects.member;

import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.enums.Permission;
import com.stardevmc.titanterritories.core.objects.holder.Town;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.Invite;
import com.stardevmc.titanterritories.core.objects.kingdom.Rank;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;

public class Resident implements ConfigurationSerializable, IUser {
    private Member member;
    private long joinDate;
    private Town town;
    private Rank rank;
    private Invite acceptedInvite;
    
    public Resident(Member member, long joinDate, Town town, Rank rank, Invite acceptedInvite) {
        this.member = member;
        this.joinDate = joinDate;
        this.town = town;
        this.rank = rank;
        this.acceptedInvite = acceptedInvite;
    }
    
    public Resident(Member member, Town town) {
        this(member, System.currentTimeMillis(), town, town.getRankController().getDefaultRank(), null);
    }
    
    public Resident(Member member) {
        this(member, System.currentTimeMillis(), null, null, null);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("member", member.getInfo().getUniqueId());
        serialized.put("joinDate", this.joinDate);
        serialized.put("town", this.town.getUniqueId().toString());
        serialized.put("rank", this.rank.getName());
        serialized.put("invite", this.acceptedInvite);
        return serialized;
    }
    
    public static Resident deserialize(Map<String, Object> serialized) {
        Member member = TitanTerritories.getInstance().getMemberManager().getMember(UUID.fromString((String) serialized.get("member")));
        long joinDate = Long.parseLong((String) serialized.get("joinDate"));
        Town town = TitanTerritories.getInstance().getTownManager().getTown(UUID.fromString((String) serialized.get("town")));
        Rank rank = town.getRankController().getRank((String) serialized.get("rank"));
        Invite invite = (Invite) serialized.get("invite");
        return new Resident(member, joinDate, town, rank, invite);
    }
    
    public long getJoinDate() {
        return joinDate;
    }
    
    public Town getTown() {
        return town;
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
    
    public void setTown(Town town) {
        this.town = town;
    }
    
    public void setRank(Rank rank) {
        this.rank = rank;
    }
    
    public void setJoinDate(long date) {
        this.joinDate = date;
    }
    
    public void setAcceptedInvite(Invite acceptedInvite) {
        this.acceptedInvite = acceptedInvite;
    }
    
    public <T extends IHolder> void setHolder(T holder) {
        this.town = (Town) holder;
    }
    
    public UUID getUniqueId() {
        return this.member.getInfo().getUniqueId();
    }
    
    public void sendMessage(String message) {
        member.sendMessage(message);
    }
    
    public void sendMessage(BaseComponent... components) {
        member.sendMessage(components);
    }
    
    public boolean hasPermission(Permission permission) {
        if (getTown().hasKingdom()) {
            Citizen citizen = getTown().getKingdom().getUserController().get(this.member.getUniqueId());
            if (citizen.getRank().hasPermission(permission)) {
                return true;
            }
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
        this.member.teleport(location);
    }
    
    public boolean isOnline() {
        return member.isOnline();
    }
    
    public boolean isLeader() {
        return town.getBaron().getUser().getUniqueId().equals(this.member.getUniqueId());
    }
    
    public String getName() {
        return this.member.getInfo().getLastName();
    }
}