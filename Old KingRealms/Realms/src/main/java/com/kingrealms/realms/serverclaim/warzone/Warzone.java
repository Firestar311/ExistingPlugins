package com.kingrealms.realms.serverclaim.warzone;

import com.kingrealms.realms.serverclaim.ServerClaim;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("Warzone")
public class Warzone extends ServerClaim implements ConfigurationSerializable {
    public Warzone(Map<String, Object> serialized) {
        super(serialized);
    }
    
    @Override
    public String getName() {
        return "Warzone";
    }
    
    public Warzone() {}
    //This class will contain utility methods for supply drops and the like, warzone events
}