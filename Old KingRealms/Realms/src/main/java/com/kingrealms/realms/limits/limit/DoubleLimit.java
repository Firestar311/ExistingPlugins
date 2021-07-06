package com.kingrealms.realms.limits.limit;

import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("DoubleLimit")
public class DoubleLimit extends Limit<Double> {
    
    public DoubleLimit(String name, String description, String id, Double value) {
        super(name, description, id, value);
    }
    
    public DoubleLimit(Map<String, Object> serialized) {
        super(serialized);
        this.value = Double.parseDouble((String) serialized.get("value"));
    }
}