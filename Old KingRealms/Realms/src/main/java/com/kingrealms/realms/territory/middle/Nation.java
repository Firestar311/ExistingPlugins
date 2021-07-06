package com.kingrealms.realms.territory.middle;

import com.kingrealms.realms.territory.base.Government;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("Nation")
public class Nation extends Government {
    public Nation(String name) {
        super(name);
    }
    
    public Nation(Map<String, Object> serialized) {
        super(serialized);
    }
    
}