package com.kingrealms.realms.warps.type;

import com.kingrealms.realms.IOwner;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.warps.Visit;
import com.starmediadev.lib.pagination.IElement;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("Warp")
public abstract class Warp implements ConfigurationSerializable, IElement {
    
    protected int id = -1; //mysql
    protected String owner, name, description, permission; //mysql
    protected Location location; //mysql
    protected Set<Visit> visits = new HashSet<>(); //mysql
    
    //TODO Abstract method for checking owner equality from a string
    
    public Warp(int id, String owner, String name, String description, String permission, Location location) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.location = location;
    }
    
    public Warp(String owner, Location location) {
        this.owner = owner;
        this.location = location;
    }
    
    public Warp(String owner, String name, Location location) {
        this.owner = owner;
        this.name = name;
        this.location = location;
    }
    
    public Warp(Map<String, Object> serialized) {
        this.id = Integer.parseInt((String) serialized.get("id"));
        this.owner = (String) serialized.get("owner");
        this.name = (String) serialized.get("name");
        this.description = (String) serialized.get("description");
        this.permission = (String) serialized.get("permission");
        this.location = (Location) serialized.get("location");
        serialized.forEach((key, value) -> {
            if (key.startsWith("visit-")) {
                Visit visit = (Visit) value;
                if (visit != null) {
                    this.visits.add(visit);
                }
            }
        });
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("id", id + "");
            put("owner", owner);
            put("name", name);
            put("description", description);
            put("permission", permission);
            put("location", location);
            int counter = 0;
            for (Visit visit : visits) {
                put("visit" + counter, visit);
                counter ++;
            }
        }};
    }
    
    public void addVisit(UUID player) {
        this.visits.add(new Visit(player, System.currentTimeMillis(), this.getLocation()));
    }
    
    public int getVisitCount(UUID player) {
        int count = 0;
        for (Visit visit : this.visits) {
            if (visit.getPlayer().equals(player)) {
                count++;
            }
        }
        
        return count;
    }
    
    public int getTotalVisitCount() {
        return this.visits.size();
    }
    
    protected boolean hasPermission(RealmProfile profile) {
        if (StringUtils.isEmpty(this.permission)) return true;
        return profile.hasPermission(this.permission);
    }
    
    public abstract <T extends IOwner> T getOwner();
    public abstract boolean canAccess(UUID uuid);
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setPermission(String permission) {
        this.permission = permission;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public Location getLocation() {
        return location;
    }
}