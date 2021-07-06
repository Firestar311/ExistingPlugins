package com.kingrealms.realms.territory.modern;

import com.kingrealms.realms.territory.base.Settlement;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("City")
public class City extends Settlement {
    public City(String name) {
        super(name);
    }
    
    public City(Map<String, Object> serialized) {
        super(serialized);
    }
    
}