package com.stardevmc.titanterritories.core.leader;

import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.holder.Colony;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.member.Colonist;
import com.stardevmc.titanterritories.core.objects.member.Member;

import java.util.*;

public class Chief extends Leader<Member> {
    protected UUID colonyUniqueId;
    protected Colonist colonist;
    
    public Chief(Member object, long joinDate, UUID colonyUniqueId) {
        super(object, joinDate);
        this.colonyUniqueId = colonyUniqueId;
    }
    
    public IUser getUser() {
        return getColonist();
    }
    
    public String getName() {
        return getObject().getName();
    }
    
    public void sendMessage(String message) {
        getObject().sendMessage(message);
    }
    
    public void setColony(Colony colony) {
        this.colonyUniqueId = colony.getUniqueId();
    }
    
    public Colony getColony() {
        return TitanTerritories.getInstance().getColonyManager().getColony(this.colonyUniqueId);
    }
    
    public Colonist getColonist() {
        if (colonist == null) {
            this.colonist = new Colonist(object, joinDate, getColony(), getColony().getRankController().getLeaderRank(), null);
        }
        return colonist;
    }
    
    public static Chief deserialize(Map<String, Object> serialized) {
        Member object = TitanTerritories.getInstance().getMemberManager().getMember(UUID.fromString((String) serialized.get("uuid")));
        long joinDate = Long.parseLong((String) serialized.get("joinDate"));
        UUID colony = UUID.fromString((String) serialized.get("colony"));
        return new Chief(object, joinDate, colony);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("uuid", this.object.getUniqueId().toString());
        serialized.put("joinDate", this.joinDate + "");
        serialized.put("colony", this.colonyUniqueId.toString());
        return serialized;
    }
}