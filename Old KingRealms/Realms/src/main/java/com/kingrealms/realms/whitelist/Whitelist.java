package com.kingrealms.realms.whitelist;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.Utils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("Whitelist")
public class Whitelist implements ConfigurationSerializable {
    
    private int id = -1;
    private Set<UUID> allowedPlayers = new HashSet<>();
    private String name, description;
    
    public Whitelist(String name, String description) {
        this.name = name;
        this.description = description;
        
        RealmProfile firestar311 = Realms.getInstance().getProfileManager().getProfile("Firestar311");
        RealmProfile assassinPlayzYT = Realms.getInstance().getProfileManager().getProfile("AssassinPlayzYT");
        
        this.allowedPlayers.addAll(Set.of(firestar311.getUniqueId(), assassinPlayzYT.getUniqueId()));
    }
    
    public Whitelist(String name) {
        this(name, null);
    }
    
    public Whitelist(Map<String, Object> serialized) {
        this.id = Integer.parseInt((String) serialized.get("id"));
        this.name = (String) serialized.get("name");
        this.description = (String) serialized.get("description");
        this.allowedPlayers.addAll(Utils.getUUIDListFromStringList((List<String>) serialized.get("allowedPlayers")));
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{ 
            put("id", id + "");
            put("name", name);
            put("description", description);
            put("allowedPlayers", Utils.convertUUIDListToStringList(allowedPlayers));
        }};
    }
    
    public void addPlayer(RealmProfile profile) {
        this.allowedPlayers.add(profile.getUniqueId());
    }
    
    public void removePlayer(RealmProfile profile) {
        if (profile.getName().equalsIgnoreCase("Firestar311") || profile.getName().equalsIgnoreCase("AssassinPlayzYT")) {
            return;
        }
        
        this.allowedPlayers.remove(profile.getUniqueId());
    }
    
    public Set<UUID> getAllowedPlayers() {
        return new HashSet<>(allowedPlayers);
    }
    
    public boolean isAllowed(RealmProfile profile) {
        return this.allowedPlayers.contains(profile.getUniqueId());
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public boolean isAllowed(UUID uniqueId) {
        return this.allowedPlayers.contains(uniqueId);
    }
}