package com.starmediadev.lib.cooldown;

import com.starmediadev.lib.util.Unit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("Cooldown")
public class Cooldown implements ConfigurationSerializable {
    
    protected Unit unit;
    protected int length;
    
    public Cooldown(Unit unit, int length) {
        this.length = length;
        this.unit = unit;
    }
    
    public Cooldown(Map<String, Object> serialized) {
        this.length = Integer.parseInt((String) serialized.get("length"));
    }
    
    public boolean isExpired(long time) {
        return (time + unit.convertTime(length)) >= System.currentTimeMillis();
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{ 
            put("length", length + "");
        }};
    }
    
    public int getLength() {
        return length;
    }
    
    public Unit getUnit() {
        return unit;
    }
    
    public long toMilliseconds() {
        return unit.convertTime(length);
    }
}