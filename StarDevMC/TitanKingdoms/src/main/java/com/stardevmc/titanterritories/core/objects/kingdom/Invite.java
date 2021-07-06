package com.stardevmc.titanterritories.core.objects.kingdom;

import com.firestar311.lib.pagination.IElement;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.member.Member;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class Invite implements ConfigurationSerializable, IElement {
    
    private UUID invited, inviter;
    private long date;
    
    public Invite(UUID invited, UUID inviter, long date) {
        this.invited = invited;
        this.inviter = inviter;
        this.date = date;
    }
    
    public Invite(Map<String, Object> serialized) {
        this.invited = UUID.fromString((String) serialized.get("invited"));
        this.inviter = UUID.fromString((String) serialized.get("inviter"));
        this.date = Long.parseLong((String) serialized.get("date"));
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("invited", invited.toString());
        serialized.put("inviter", inviter.toString());
        serialized.put("date", date + "");
        return serialized;
    }
    
    public UUID getInvited() {
        return invited;
    }
    
    public UUID getInviter() {
        return inviter;
    }
    
    public long getDate() {
        return date;
    }
    
    public Member getInviterMember() {
        return TitanTerritories.getInstance().getMemberManager().getMember(this.inviter);
    }
    
    public Member getInvitedMember() {
        return TitanTerritories.getInstance().getMemberManager().getMember(this.invited);
    }
    
    public String formatLine(String... args) {
        return "&b" + getInviterMember().getName() + " invited " + getInvitedMember().getName();
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Invite that = (Invite) o;
        return date == that.date && invited.equals(that.invited) && inviter.equals(that.inviter);
    }
    
    public int hashCode() {
        return Objects.hash(invited, inviter, date);
    }
}