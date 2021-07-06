package com.kingrealms.realms.territory.base.member;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.territory.base.Invite;
import com.kingrealms.realms.territory.enums.Rank;
import com.starmediadev.lib.pagination.IElement;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("Member")
public class Member implements ConfigurationSerializable, IElement {
    protected UUID uuid; //mysql
    protected Rank rank; //mysql
    protected Invite invite; //mysql
    protected long joinDate; //mysql
    protected RealmProfile realmProfile; //cache
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("uuid", this.uuid.toString());
        serialized.put("rank", this.rank.name());
        serialized.put("joinDate", this.joinDate + "");
        if (invite != null) {
            serialized.put("invite", this.invite);
        }
        return serialized;
    }
    
    public Member(Map<String, Object> serialized) {
        this.uuid = UUID.fromString((String) serialized.get("uuid"));
        rank = Rank.valueOf((String) serialized.get("rank"));
        if (serialized.containsKey("invite")) {
            Invite invite = (Invite) serialized.get("invite");
            setInvite(invite);
        }
        joinDate = Long.parseLong((String) serialized.get("joinDate"));
    }
    
    public Member(RealmProfile realmProfile) {
        this.uuid = realmProfile.getUniqueId();
    }
    
    public UUID getUniqueId() {
        return uuid;
    }
    
    public String getName() {
        return getRealmProfile().getName();
    }
    
    public RealmProfile getRealmProfile() {
        if (realmProfile == null) {
            this.realmProfile = Realms.getInstance().getProfileManager().getProfile(this.uuid);
        }
        return realmProfile;
    }
    
    public void sendMessage(String msg) {
        getRealmProfile().sendMessage(msg);
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public Location getLocation() {
        if (this.getRealmProfile().getUser().isOnline()) {
            return Bukkit.getServer().getPlayer(getUniqueId()).getLocation();
        }
        
        return null;
    }
    
    public void setRank(Rank rank) {
        this.rank = rank;
    }
    
    public void setInvite(Invite invite) {
        this.invite = invite;
    }
    
    public Invite getInvite() {
        return invite;
    }
    
    public void setJoinDate(long joinDate) {
        this.joinDate = joinDate;
    }
    
    public long getJoinDate() {
        return joinDate;
    }
    
    @Override
    public String formatLine(String... args) {
        String name = this.getRealmProfile().getName();
        return " &8- " + this.rank.getDisplayName().toUpperCase() + " " + name;
    }
}