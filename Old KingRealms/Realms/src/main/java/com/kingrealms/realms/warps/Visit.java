package com.kingrealms.realms.warps;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("Visit")
public class Visit implements ConfigurationSerializable {
    private final long date; //mysql
    private final Location location; //mysql
    private final UUID uuid; //mysql
    
    public Visit(UUID uuid, long date, Location location) {
        this.uuid = uuid;
        this.date = date;
        this.location = location;
    }
    
    public Visit(Map<String, Object> serialized) {
        this.uuid = UUID.fromString((String) serialized.get("uuid"));
        this.date = Long.parseLong((String) serialized.get("date"));
        this.location = (Location) serialized.get("location");
    }
    
    public long getDate() {
        return date;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public UUID getPlayer() {
        return uuid;
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("date", date + "");
            put("location", location);
            put("uuid", uuid.toString());
        }};
    }
}