package com.kingrealms.realms.territory.modern;

import com.kingrealms.realms.territory.base.Outpost;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("Province")
public class Province extends Outpost {
    public Province(String name) {
        super(name);
    }
    
    public Province(Map<String, Object> serialized) {
        super(serialized);
    }
    
}