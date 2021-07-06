package com.kingrealms.realms.territory.medievil;

import com.kingrealms.realms.territory.base.Outpost;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Colony")
public class Colony extends Outpost {
    public Colony(String name) {
        super(name);
    }
    
}