package com.stardevmc.titanterritories.core.objects.member;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.leader.PlayerMonarch;
import com.stardevmc.titanterritories.core.objects.enums.Permission;
import com.stardevmc.titanterritories.core.objects.holder.Kingdom;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.Invite;
import com.stardevmc.titanterritories.core.objects.kingdom.Rank;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;

public class Citizen implements ConfigurationSerializable, IUser {
    private Invite acceptedInvite;
    private long joinDate;
    private UUID kingdomUniqueId;
    private Member member;
    private String rank;
    
    public Citizen(Member member) {
        this.member = member;
    }
    
    public Citizen(Member member, UUID kingdom) {
        this.member = member;
        this.kingdomUniqueId = kingdom;
    }
    
    public Citizen(UUID kingdom, Member member, Invite acceptedInvite) {
        this(member, kingdom, null, System.currentTimeMillis(), acceptedInvite);
    }
    
    public Citizen(Member member, UUID kingdom, String rank, long joinDate, Invite acceptedInvite) {
        this.member = member;
        this.kingdomUniqueId = kingdom;
        this.rank = rank;
        this.joinDate = joinDate;
        this.acceptedInvite = acceptedInvite;
    }
    
    public static Citizen deserialize(Map<String, Object> serialized) {
        Member member = TitanTerritories.getInstance().getMemberManager().getMember(UUID.fromString((String) serialized.get("player")));
        UUID kingdom = UUID.fromString((String) serialized.get("kingdom"));
        long joinDate = Long.parseLong((String) serialized.get("joinDate"));
        Invite acceptedInvite = (Invite) serialized.get("invite");
        String rank = (String) serialized.get("rank");
        return new Citizen(member, kingdom, rank, joinDate, acceptedInvite);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("player", member.getUniqueId().toString());
        if (rank != null) {
            serialized.put("rank", rank);
        }
        serialized.put("kingdom", kingdomUniqueId.toString());
        serialized.put("joinDate", this.joinDate + "");
        serialized.put("invite", this.acceptedInvite);
        return serialized;
    }
    
    public boolean hasKingdom() {
        return this.kingdomUniqueId != null;
    }
    
    public boolean isOnline() {
        return member.isOnline();
    }
    
    public boolean isLeader() {
        return getKingdom().isMonarch(this.member.getUniqueId());
    }
    
    public String getName() {
        return this.member.getName();
    }
    
    public UUID getUniqueId() {
        return this.member.getUniqueId();
    }
    
    public void sendMessage(String message) {
        Player onlinePlayer = Bukkit.getPlayer(this.member.getUniqueId());
        if (onlinePlayer != null) {
            onlinePlayer.sendMessage(Utils.color(message));
        }
    }
    
    public void sendMessage(BaseComponent... components) {
        member.sendMessage(components);
    }
    
    public boolean hasPermission(Permission permission) {
        Rank rank = getRank();
        Kingdom kingdom = getKingdom();
    
        boolean lowerHasPermission = false;
        for (Object r : kingdom.getRankController().getRanks()) {
            if (r instanceof Rank) {
                Rank ra = ((Rank) r);
                if (ra.getOrder() > rank.getOrder()) {
                    if (ra.hasPermission(permission)) {
                        lowerHasPermission = true;
                        break;
                    }
                }
            }
        }
        
        return lowerHasPermission ? lowerHasPermission : rank.hasPermission(permission);
    }
    
    public Player getPlayer() {
        return member.getPlayer();
    }
    
    public Rank getRank() {
        Rank rank;
        Kingdom kingdom = getKingdom();
        if (kingdom.getMonarch() instanceof PlayerMonarch) {
            PlayerMonarch monarch = ((PlayerMonarch) kingdom.getMonarch());
            if (monarch.getObject().getUniqueId().equals(this.member.getUniqueId())) {
                return kingdom.getRankController().getLeaderRank();
            }
        }
        rank = kingdom.getRankController().getRank(this.rank);
        
        if (rank == null) { rank = kingdom.getRankController().getDefaultRank(); }
        return rank;
    }
    
    public Member getMember() {
        return member;
    }
    
    public long getJoinDate() {
        return joinDate;
    }
    
    public void setJoinDate(long joinDate) {
        this.joinDate = joinDate;
    }
    
    public Location getLocation() {
        return member.getLocation();
    }
    
    public void teleport(Location location) {
        member.teleport(location);
    }
    
    public void setRank(Rank rank) {
        this.rank = rank.getName();
    }
    
    public Kingdom getKingdom() {
        return TitanTerritories.getInstance().getKingdomManager().getKingdom(this.kingdomUniqueId);
    }
    
    public void setKingdom(Kingdom kingdomUniqueId) {
        this.kingdomUniqueId = kingdomUniqueId.getUniqueId();
        this.joinDate = System.currentTimeMillis();
        if (kingdomUniqueId != null) {
            if (kingdomUniqueId instanceof Kingdom) {
                kingdomUniqueId.getUserController().add(this);
            }
        }
    }
    
    public int hashCode() {
        return Objects.hash(member);
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Citizen citizen = (Citizen) o;
        return Objects.equals(member, citizen.member);
    }
    
    public Invite getAcceptedInvite() {
        return acceptedInvite;
    }
    
    public void setAcceptedInvite(Invite acceptedInvite) {
        this.acceptedInvite = acceptedInvite;
    }
    
    public <T extends IHolder> void setHolder(T holder) {
        this.kingdomUniqueId = ((Kingdom) holder).getUniqueId();
    }
}