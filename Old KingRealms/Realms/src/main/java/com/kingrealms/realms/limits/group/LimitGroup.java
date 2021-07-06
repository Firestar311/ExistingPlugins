package com.kingrealms.realms.limits.group;

import com.kingrealms.realms.limits.limit.Limit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("LimitGroup")
public abstract class LimitGroup implements ConfigurationSerializable {
    protected Set<Limit> limits = new HashSet<>();
    protected String name, description, id;
    
    public LimitGroup(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    public LimitGroup(Map<String, Object> serialized) {
        this.name = (String) serialized.get("name");
        this.description = (String) serialized.get("description");
        this.id = (String) serialized.get("id");
        serialized.forEach((key, value) -> {
            if (key.startsWith("limit-")) {
                Limit limit = (Limit) value;
                if (limit != null) {
                    limits.add(limit);
                }
            }
        });
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("name", name);
            put("description", description);
            put("id", id);
            
            if (!limits.isEmpty()) {
                for (Limit limit : limits) {
                    put("limit-" + limit.getId(), limit);
                }
            }
        }};
    }
    
    public Set<Limit> getLimits() {
        return limits;
    }
    
    public void addLimit(Limit limit) {
        this.limits.add(limit);
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
    
    public abstract void createDefaultLimits();
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        LimitGroup that = (LimitGroup) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}