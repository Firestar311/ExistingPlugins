package com.kingrealms.realms.limits.limit;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("Limit")
public abstract class Limit<T extends Number> implements ConfigurationSerializable {
    
    protected String name, description, id; //mysql
    protected T value; //mysql
    
    public Limit(String name, String description, String id, T value) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.value = value;
    }
    
    public Limit(Map<String, Object> serialized) {
        this.name = (String) serialized.get("name");
        this.description = (String) serialized.get("description");
        this.id = (String) serialized.get("id");
    }
    
    public void setValue(T value) {
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getId() {
        return id;
    }
    
    public T getValue() {
        return value;
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("name", getName());
            put("description", getDescription());
            put("value", getValue().toString());
            put("id", getId());
        }};
    }
}