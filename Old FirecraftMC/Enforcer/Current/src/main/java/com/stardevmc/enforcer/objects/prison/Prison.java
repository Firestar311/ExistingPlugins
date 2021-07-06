package com.stardevmc.enforcer.objects.prison;

import com.starmediadev.lib.items.InventoryStore;
import com.starmediadev.lib.pagination.IElement;
import com.starmediadev.lib.region.Cuboid;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

public class Prison extends Cuboid implements IElement, ConfigurationSerializable {
    
    private int id;
    private Location location;
    private int maxPlayers;
    private Set<Inmate> inmates = new HashSet<>();
    private String name;
    
    public Prison(int id, Location location, int maxPlayers, Location minLocation, Location maxLocation) {
        super(minLocation, maxLocation);
        this.id = id;
        this.location = location;
        this.maxPlayers = maxPlayers;
    }
    
    public Prison(int id, Location location, int maxPlayers, Set<Inmate> inmates, Location minLocation, Location maxLocation) {
        super(minLocation, maxLocation);
        this.id = id;
        this.location = location;
        this.maxPlayers = maxPlayers;
        this.inmates = inmates;
    }
    
    public Prison(int id, Location location, int maxPlayers) {
        super(location, location);
        this.id = id;
        this.location = location;
        this.maxPlayers = maxPlayers;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("id", id + "");
        serialized.put("spawn", this.location);
        serialized.put("maxPlayers", this.maxPlayers + "");
        serialized.put("name", this.name);
        int counter = 0;
        for (Inmate inmate : this.inmates) {
            serialized.put("inmate" + counter, inmate);
            counter++;
        }
        serialized.put("min", this.getMinimum());
        serialized.put("max", this.getMaximum());
        return serialized;
    }
    
    public Prison(Location pos1, Location pos2, int id, Location location, int maxPlayers, Set<Inmate> inmates, String name) {
        super(pos1, pos2);
        this.id = id;
        this.location = location;
        this.maxPlayers = maxPlayers;
        this.inmates = inmates;
        this.name = name;
    }
    
    public static Prison deserialize(Map<String, Object> serialized) {
        int id = Integer.parseInt((String) serialized.get("id"));
        Location location = (Location) serialized.get("spawn");
        int maxPlayers = Integer.parseInt((String) serialized.get("maxPlayers"));
        Set<Inmate> inmates = new HashSet<>();
        for (Entry<String, Object> entry : serialized.entrySet()) {
            if (entry.getKey().startsWith("inmate")) {
                inmates.add((Inmate) entry.getValue());
            }
        }
        String name = (String) serialized.get("name");
        Location min = (Location) serialized.get("min");
        Location max = (Location) serialized.get("max");
        Prison prison = new Prison(id, location, maxPlayers, inmates, min, max);
        prison.name = name;
        return prison;
    }
    
    public void removeInmate(UUID uniqueId) {
        this.inmates.removeIf(inmate -> inmate.getUuid().equals(uniqueId));
    }
    
    public Inmate addInmate(UUID uuid) {
        Inmate inmate = new Inmate(uuid);
        inmate.setPrison(this.id);
        this.inmates.add(inmate);
        return inmate;
    }
    
    public Inmate getInmate(UUID uuid) {
        for (Inmate inmate : this.inmates) {
            if (inmate.getUuid().equals(uuid)) {
                return inmate;
            }
        }
        
        return null;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    public int getId() {
        return id;
    }
    
    public Set<Inmate> getInmates() {
        return new HashSet<>(inmates);
    }
    
    public void addInmate(Inmate inmate) {
        this.inmates.add(inmate);
    }
    
    public void removeInmate(Inmate inmate) {
        this.inmates.remove(inmate);
    }
    
    public boolean isFull() {
        return this.inmates.size() >= maxPlayers;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public boolean isInmate(UUID uuid) {
        for (Inmate inmate : this.inmates) {
            if (inmate.getUuid().equals(uuid)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void setInmates(Set<Inmate> inmates) {
        this.inmates = inmates;
    }
    
    public String getName() {
        return name == null ? id + "" : name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDisplayName() {
        if (this.name == null || this.name.equalsIgnoreCase("") || this.name.equalsIgnoreCase("null")) {
            return this.id + "";
        }
        return this.name;
    }
    
    public void addInmate(Player player) {
        Inmate inmate = new Inmate(player.getUniqueId());
        inmate.setInventory(InventoryStore.itemsToString(player.getInventory().getContents()));
        inmate.setOffline(false);
        inmate.setPrison(this.id);
        this.inmates.add(inmate);
    }
    
    public String formatLine(String... args) {
        if (this.name != null && !this.name.equals("")) {
            return "&bPrison &d" + this.id + " &bhas the name &e" + this.name + " &band has &a" + this.inmates.size() + " &bout of &a" + this.maxPlayers + " &bplayers";
        }
        return "&bPrison &d" + this.id + " &bhas no name set and has &a" + this.inmates.size() + " &bout of &a" + this.maxPlayers + " &bplayers";
    }
}