package com.kingrealms.realms.home;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;
import java.util.UUID;

@SerializableAs("StaffHome")
public class StaffHome extends Home {
    public StaffHome(UUID owner, String name, Location location, long createdDate) {
        super(owner, name, location, createdDate);
    }
    
    public StaffHome(Map<String, Object> serialized) {
        super(serialized);
    }
}
