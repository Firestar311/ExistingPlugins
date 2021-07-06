package com.kingrealms.realms.territory.middle;

import com.kingrealms.realms.territory.base.Outpost;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("Protectorate")
public class Protectorate extends Outpost {
    public Protectorate(String name) {
        super(name);
    }
    
    public Protectorate(Map<String, Object> serialized) {
        super(serialized);
    }
    
}