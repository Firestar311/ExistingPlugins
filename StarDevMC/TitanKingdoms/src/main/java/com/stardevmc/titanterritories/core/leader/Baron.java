package com.stardevmc.titanterritories.core.leader;

import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.holder.Town;
import com.stardevmc.titanterritories.core.objects.member.Member;
import com.stardevmc.titanterritories.core.objects.member.Resident;

import java.util.*;

public class Baron extends Leader<Member> {
    
    protected UUID townUniqueId;
    protected Member member;
    protected Resident resident;
    
    public Baron(Member object, long joinDate, UUID townUniqueId) {
        super(object, joinDate);
        this.townUniqueId = townUniqueId;
    }
    
    public IUser getUser() {
        return getResident();
    }
    
    public Resident getResident() {
        if (resident == null) {
            this.resident = new Resident(object, joinDate, getTown(), getTown().getRankController().getLeaderRank(), null);
        }
        return resident;
    }
    
    public String getName() {
        return member.getName();
    }
    
    public void sendMessage(String message) {
        member.sendMessage(message);
    }
    
    public Town getTown() {
        return TitanTerritories.getInstance().getTownManager().getTown(townUniqueId);
    }
    
    public void setTown(Town town) {
        this.townUniqueId = town.getUniqueId();
    }
    
    public static Baron deserialize(Map<String, Object> serialized) {
        Member object = TitanTerritories.getInstance().getMemberManager().getMember(UUID.fromString((String) serialized.get("uuid")));
        long joinDate = Long.parseLong((String) serialized.get("joinDate"));
        UUID town = UUID.fromString((String) serialized.get("town"));
        return new Baron(object, joinDate, town);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("uuid", this.object.getInfo().getUniqueId());
        serialized.put("joinDate", this.joinDate + "");
        serialized.put("town", this.townUniqueId.toString());
        return serialized;
    }
}