package com.kingrealms.realms.home;

import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;

public class DeathHome extends Home {
    
    public static final String NAME = "death";
    
    public DeathHome(UUID owner, Location location, long createdDate) {
        super(owner, NAME, location, createdDate);
    }
    
    public DeathHome(Map<String, Object> serialized) {
        super(serialized);
    }
}