package com.stardevmc.titanterritories.core.base;

import com.stardevmc.titanterritories.core.TitanTerritories;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;

import java.util.*;

/**
 * A parent class to all things that are related to claims
 */
public abstract class Territory implements ConfigurationSerializable {
    
    protected static final TitanTerritories plugin = TitanTerritories.getInstance();
    
    protected String uniqueId, name;
    protected Location spawnpoint;
    protected Set<Plot> plots = new HashSet<>();
    
    protected Map<String, Object> baseSerialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("uniqueId", uniqueId);
        serialized.put("name", name);
        serialized.put("spawnpoint", spawnpoint);
        return serialized;
    }
    
    public String getUniqueId() {
        return uniqueId;
    }
    
    public String getName() {
        return name;
    }
    
    public Location getSpawnpoint() {
        return spawnpoint;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setSpawnpoint(Location spawnpoint) {
        this.spawnpoint = spawnpoint;
    }
    
    public Set<Plot> getPlots() {
        return plots;
    }
    
    public boolean contains(Location location) {
        for (Plot plot : this.plots) {
            if (plot.contains(location)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean contains(Entity entity) {
        return contains(entity.getLocation());
    }
}