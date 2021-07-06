package com.stardevmc.titanterritories.core.leader;

import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.member.Citizen;
import com.stardevmc.titanterritories.core.objects.member.Member;

import java.util.*;

public class PlayerMonarch extends Monarch<Member> {
    
    private Citizen citizen;
    
    public PlayerMonarch(Member object, long joinDate, UUID kingdom, String rankName) {
        super(object, joinDate, kingdom, rankName);
    }
    
    public IUser getUser() {
        return getCitizen();
    }
    
    public String getName() {
        return getObject().getName();
    }
    
    public void sendMessage(String message) {
        getObject().sendMessage(message);
    }
    
    public Citizen getCitizen() {
        if (citizen == null) {
            this.citizen = new Citizen(object, kingdomUniqueId, rankName, joinDate, null);
        }
        return citizen;
    }
    
    public static PlayerMonarch deserialize(Map<String, Object> serialized) {
        Member object = TitanTerritories.getInstance().getMemberManager().getMember(UUID.fromString((String) serialized.get("uuid")));
        long joinDate = Long.parseLong((String) serialized.get("joinDate"));
        UUID kingdom = UUID.fromString((String) serialized.get("kingdom"));
        String rankName = (String) serialized.get("rank");
        return new PlayerMonarch(object, joinDate, kingdom, rankName);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("uuid", this.object.getUniqueId().toString());
        serialized.put("joinDate", this.joinDate + "");
        serialized.put("kingdom", this.kingdomUniqueId.toString());
        serialized.put("rank", this.rankName);
        return serialized;
    }
}