package com.stardevmc.titanterritories.core.controller;

import com.stardevmc.titanterritories.core.objects.holder.Kingdom;
import com.stardevmc.titanterritories.core.objects.holder.Town;
import org.bukkit.Location;

import java.util.*;
import java.util.Map.Entry;

public class TownHandler {
    
    private Kingdom kingdom;
    private Map<UUID, Town> townMap = new HashMap<>();
    
    private UUID generateUniqueId() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (townMap.containsKey(uuid));
        return uuid;
    }
    
    public TownHandler(Kingdom kingdom) {
        this.kingdom = kingdom;
    }
    
    public void addTown(Town town) {
        UUID uuid = generateUniqueId();
        town.setUniqueId(uuid);
        this.townMap.put(uuid, town);
    }
    
    public void removeTown(Town town) {
        this.townMap.remove(town.getUniqueId());
        town.setKingdom(null);
    }
    
    public List<Town> getTowns() {
        return new ArrayList<>(townMap.values());
    }
    
    public Town getTown(String townName) {
        for (Entry<UUID, Town> t : townMap.entrySet()) {
            if (t.getValue().getName().equalsIgnoreCase(townName)) {
                return t.getValue();
            }
        }
        return null;
    }
    
    public Town getTown(UUID uuid) {
        return this.townMap.get(uuid);
    }
    
    public Town getTown(Location location) {
        for (Town town : townMap.values()) {
            if (town.getClaimController().contains(location)) {
                return town;
            }
        }
        return null;
    }
    
    public Kingdom getKingdom() {
        return kingdom;
    }
    
    public void setKingdom(Kingdom kingdom) {
        this.kingdom = kingdom;
    }
}