package com.kingrealms.realms.home;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("Home")
public class Home implements ConfigurationSerializable {
    
    private UUID owner; //mysql
    private long createdDate; //mysql
    private String name; //mysql
    private Location location; //mysql
    
    public Home(UUID owner, String name, Location location, long createdDate) {
        this.owner = owner;
        this.name = name;
        this.location = location;
        this.createdDate = createdDate;
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("owner", this.owner.toString());
        serialized.put("name", this.name);
        serialized.put("location", this.location);
        serialized.put("createdDate", this.createdDate + "");
        return serialized;
    }
    
    public Home(Map<String, Object> serialized) {
        this.owner = UUID.fromString((String) serialized.get("owner"));
        this.name = (String) serialized.get("name");
        this.location = (Location) serialized.get("location");
        this.createdDate = Long.parseLong((String) serialized.get("createdDate"));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Home home = (Home) o;
        return Objects.equals(owner, home.owner) && name.equalsIgnoreCase(home.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(owner, name);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
}
