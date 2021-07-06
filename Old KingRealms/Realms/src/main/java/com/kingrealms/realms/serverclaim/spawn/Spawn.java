package com.kingrealms.realms.serverclaim.spawn;

import com.kingrealms.realms.serverclaim.ServerClaim;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("Spawn")
public class Spawn extends ServerClaim {
    
    private Location spawnpoint;
    public Spawn(Location spawnpoint) {
        this.spawnpoint = spawnpoint;
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = super.serialize();
        serialized.put("spawnpoint", this.spawnpoint);
        return serialized;
    }
    
    public Spawn(Map<String, Object> serialized) {
        super(serialized);
        this.spawnpoint = (Location) serialized.get("spawnpoint");
    }
    
    @Override
    public String getName() {
        return "Spawn";
    }
    
    public void setSpawnpoint(Location spawnpoint) {
        this.spawnpoint = spawnpoint;
    }
    
    public Location getSpawnpoint() {
        return spawnpoint;
    }
}