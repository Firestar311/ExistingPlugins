package com.kingrealms.realms.repair;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("RepairUseInfo")
public class RepairUseInfo implements ConfigurationSerializable {
    private int amountUsed;
    private long firstUsed;
    
    public RepairUseInfo() {}
    
    public RepairUseInfo(Map<String, Object> serialized) {
        this.amountUsed = Integer.parseInt((String) serialized.get("amountUsed"));
        this.firstUsed = Long.parseLong((String) serialized.get("firstUsed"));
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{ 
            put("amountUsed", amountUsed + "");
            put("firstUsed", firstUsed + "");
        }};
    }
    
    public void cooldownExpired() {
        this.amountUsed = 0;
        this.firstUsed = 0;
    }
    
    public int getAmountUsed() {
        return amountUsed;
    }
    
    public void setAmountUsed(int amountUsed) {
        this.amountUsed = amountUsed;
    }
    
    public long getFirstUsed() {
        return firstUsed;
    }
    
    public void setFirstUsed(long firstUsed) {
        this.firstUsed = firstUsed;
    }
}