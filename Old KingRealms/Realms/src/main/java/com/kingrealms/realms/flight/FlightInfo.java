package com.kingrealms.realms.flight;

import com.kingrealms.realms.profile.RealmProfile;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class FlightInfo implements ConfigurationSerializable {
    
    private boolean active;
    private String activatedBy;
    private long length, date;
    
    public FlightInfo() {}
    
    public FlightInfo(Map<String, Object> serialized) {
        this.active = (boolean) serialized.get("active");
        this.activatedBy = (String) serialized.get("activatedBy");
        this.length = Long.parseLong((String) serialized.get("length"));
        this.date = Long.parseLong((String) serialized.get("date"));
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{ 
            put("active", active);
            put("activatedBy", activatedBy);
            put("length", length + "");
            put("date", date + "");
        }};
    }
    
    public void activate(RealmProfile profile, long length) {
        this.active = true;
        this.activatedBy = profile.getUniqueId().toString();
        this.length = length;
        this.date = System.currentTimeMillis();
    }
    
    public void deactivate() {
        this.active = false;
        this.activatedBy = null;
        this.length = 0;
        this.date = 0;
    }
    
    public boolean isActive() {
        return !isExpired();
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > (this.date + this.length);
    }
    
    public FlightResult checkExpired() {
        if (date == 0) return FlightResult.NOT_SET;
        if (isExpired()) return FlightResult.EXPIRED;
        return FlightResult.ACTIVE;
    }
    
    public String getActivatedBy() {
        return activatedBy;
    }
    
    public long getLength() {
        return length;
    }
    
    public long getDate() {
        return date;
    }
}