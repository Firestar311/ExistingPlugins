package com.kingrealms.realms.territory.medievil;

import com.kingrealms.realms.territory.base.Government;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("Kingdom")
public class Kingdom extends Government {
    
    public Kingdom(String name) {
        super(name);
    }
    
    public Kingdom(Map<String, Object> serialized) {
        super(serialized);
    }
}