package com.kingrealms.realms.territory.modern;

import com.kingrealms.realms.territory.base.Government;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("Country")
public class Country extends Government {
    public Country(String name) {
        super(name);
    }
    
    public Country(Map<String, Object> serialized) {
        super(serialized);
    }
    
}