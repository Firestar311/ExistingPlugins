package com.stardevmc.titanterritories.core.base;

import com.firestar311.lib.region.Cuboid;
import org.bukkit.Location;

public class Plot extends Cuboid {
    
    protected String uniqueId;
    
    public Plot(Location location) {
        super(location.getChunk().getBlock(0, 0, 0).getLocation(), location.getChunk().getBlock(15, 255, 15).getLocation());
    }
    
    public String getUniqueId() {
        return uniqueId;
    }
    
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}