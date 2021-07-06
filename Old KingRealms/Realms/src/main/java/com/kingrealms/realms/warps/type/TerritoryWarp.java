package com.kingrealms.realms.warps.type;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.territory.base.Territory;
import com.kingrealms.realms.territory.base.member.Member;
import com.kingrealms.realms.territory.enums.Rank;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.*;

@SerializableAs("TerritoryWarp")
public class TerritoryWarp extends Warp {
    
    private Rank minRank = Rank.MEMBER; //mysql
    
    public TerritoryWarp(int id, Territory owner, String name, String description, String permission, Location location) {
        super(id, owner.getUniqueId(), name, description, permission, location);
    }
    
    public TerritoryWarp(Territory owner, Location location) {
        super(owner.getUniqueId(), location);
    }
    
    public TerritoryWarp(Territory owner, String name, Location location) {
        super(owner.getUniqueId(), name, location);
    }
    
    public TerritoryWarp(Map<String, Object> serialized) {
        super(serialized);
        this.minRank = Rank.valueOf((String) serialized.get("minRank"));
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>(super.serialize()) {{
            put("minRank", minRank.name());
        }};
    }
    
    public void setMinRank(Rank minRank) {
        this.minRank = minRank;
    }
    
    public Rank getMinRank() {
        return minRank;
    }
    
    @Override
    public Territory getOwner() {
        return Realms.getInstance().getTerritoryManager().getTerritory(this.owner);
    }
    
    @Override
    public boolean canAccess(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            if (player.hasPermission("realms.warps.override.territory") || player.hasPermission("realms.warps.override.*")) return true;
        }
    
        Member member = getOwner().getMember(uuid);
        if (member == null) return false;
        return member.getRank().getOrder() <= this.minRank.getOrder();
    }
}