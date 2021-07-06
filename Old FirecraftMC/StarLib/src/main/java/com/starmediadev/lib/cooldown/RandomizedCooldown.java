package com.starmediadev.lib.cooldown;

import com.starmediadev.lib.util.Unit;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("RandomizedCooldown")
public class RandomizedCooldown extends Cooldown {
    
    protected int min, max;
    
    public RandomizedCooldown(Unit unit, int min, int max) {
        super(unit, 0);
        this.min = min;
        this.max = max;
    }
    
    public RandomizedCooldown(Map<String, Object> serialized) {
        super(serialized);
        this.min = Integer.parseInt((String) serialized.get("min"));
        this.max = Integer.parseInt((String) serialized.get("max"));
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>(super.serialize()) {{ 
            put("min", min + "");
            put("max", max + "");
        }};
    }
    
    public void generateNextLength() {
        this.length = new Random().nextInt(max - min) + min;
    }
}