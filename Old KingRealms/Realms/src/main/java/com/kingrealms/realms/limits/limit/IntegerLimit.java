package com.kingrealms.realms.limits.limit;

import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("IntegerLimit")
public class IntegerLimit extends Limit<Integer> {
    
    public IntegerLimit(String name, String description, String id, Integer value) {
        super(name, description, id, value);
    }
    
    public IntegerLimit(Map<String, Object> serialized) {
        super(serialized);
        this.value = Integer.parseInt((String) serialized.get("value"));
    }
}