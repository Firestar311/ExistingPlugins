package com.kingrealms.realms.territory.middle;

import com.kingrealms.realms.territory.base.Settlement;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("Town")
public class Town extends Settlement {
    public Town(String name) {
        super(name);
    }
    
    public Town(Map<String, Object> serialized) {
        super(serialized);
    }
    
}