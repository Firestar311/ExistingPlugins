package com.kingrealms.realms.kits;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("KitUseInfo")
public class KitUseInfo implements ConfigurationSerializable {
    private int kitId, uses;
    private long lastUsed = -1;
    
    public KitUseInfo(int kitId) {
        this.kitId = kitId;
    }
    
    public KitUseInfo(Map<String, Object> serialized) {
        this.kitId = Integer.parseInt((String) serialized.get("kitId"));
        this.uses = Integer.parseInt((String) serialized.get("uses"));
        this.lastUsed = Long.parseLong((String) serialized.get("lastUsed"));
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("kitId", kitId + "");
            put("uses", uses + "");
            put("lastUsed", lastUsed + "");
        }};
    }
    
    public KitUseInfo(int kitId, int uses, long lastUsed) {
        this.kitId = kitId;
        this.uses = uses;
        this.lastUsed = lastUsed;
    }
    
    public void setUses(int uses) {
        this.uses = uses;
    }
    
    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }
    
    public int getKitId() {
        return kitId;
    }
    
    public int getUses() {
        return uses;
    }
    
    public long getLastUsed() {
        return lastUsed;
    }
    
    public void addUse(int value) {
        this.uses += value;
    }
}