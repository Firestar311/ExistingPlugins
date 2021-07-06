package com.stardevmc.titanterritories.core.leader;

import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.holder.Kingdom;

import java.util.UUID;

public abstract class Monarch<T> extends Leader<T> {

    protected UUID kingdomUniqueId;
    protected String rankName;
    
    public Monarch(T object, long joinDate, UUID kingdomUniqueId, String rankName) {
        super(object, joinDate);
        this.kingdomUniqueId = kingdomUniqueId;
        this.rankName = rankName;
    }
    
    public Kingdom getKingdom() {
        return TitanTerritories.getInstance().getKingdomManager().getKingdom(kingdomUniqueId);
    }
    
    public void setKingdom(Kingdom kingdom) {
        this.kingdomUniqueId = kingdom.getUniqueId();
    }
}