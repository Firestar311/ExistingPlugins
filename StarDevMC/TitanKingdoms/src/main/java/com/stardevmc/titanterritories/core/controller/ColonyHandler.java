package com.stardevmc.titanterritories.core.controller;

import com.stardevmc.titanterritories.core.objects.holder.Kingdom;
import com.stardevmc.titanterritories.core.objects.holder.Colony;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class ColonyHandler {
    
    private Kingdom kingdom;
    private List<Colony> colonies = new ArrayList<>();
    
    public ColonyHandler(Kingdom kingdom) {
        this.kingdom = kingdom;
    }
    
    public void addColony(Colony colony) {
        this.colonies.add(colony);
    }
    
    public void removeColony(Colony colony) {
        this.colonies.remove(colony);
        colony.setKingdom(null);
    }
    
    public List<Colony> getColonies() {
        return new ArrayList<>(colonies);
    }
    
    public Colony getColony(String colony) {
        for (Colony c : this.colonies) {
            if (c.getName().equalsIgnoreCase(colony)) {
                return c;
            }
        }
        return null;
    }
    
    public Colony getColony(Location location) {
        for (Colony colony : colonies) {
            if (colony.getClaimController().contains(location)) {
                return colony;
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