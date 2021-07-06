package com.kingrealms.realms.profile;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("SocialSpy")
public class SocialSpy implements ConfigurationSerializable {
    
    private long date;
    private boolean active;
    
    public SocialSpy() {}
    
    public void activate() {
        this.date = System.currentTimeMillis();
        this.active = true;
    }
    
    public SocialSpy(Map<String, Object> serialized) {
        this.date = Long.parseLong((String) serialized.get("date"));
        this.active = (boolean) serialized.get("active");
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{ 
            put("date", date + "");
            put("active", active);
        }};
    }
    
    public void deactivate() {
        this.active = false;
        this.date = 0;
    }
    
    public long getDate() {
        return date;
    }
    
    public boolean isActive() {
        return active;
    }
}