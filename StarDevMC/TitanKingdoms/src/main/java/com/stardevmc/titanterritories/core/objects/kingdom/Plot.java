package com.stardevmc.titanterritories.core.objects.kingdom;

import com.firestar311.lib.region.Cuboid;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.holder.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class Plot extends Cuboid implements ConfigurationSerializable {
    private Kingdom kingdom;
    private Colony colony;
    private Town town;
    
    public Plot(Location pos1, Location pos2) {
        super(pos1, pos2);
    }
    
    public Plot(Location location) {
        super(location.getChunk().getBlock(0, 0, 0).getLocation(), location.getChunk().getBlock(15, 255, 15).getLocation());
    }
    
    public Plot(Map<String, Object> serialized) {
        if (serialized.containsKey("kingdom")) {
            String kingdomString = (String) serialized.get("kingdom");
            try {
                UUID uuid = UUID.fromString(kingdomString);
                this.kingdom = TitanTerritories.getInstance().getKingdomManager().getKingdom(uuid);
            } catch (Exception e) {
                this.kingdom = TitanTerritories.getInstance().getKingdomManager().getKingdom(kingdomString);
            }
            if (serialized.containsKey("town")) {
                this.town = this.kingdom.getTownHandler().getTown((String) serialized.get("town"));
                if (town != null) {
                    town.getClaimController().addPlot(this);
                } else {
                    this.town = null;
                }
            }
            
            if (serialized.containsKey("colony")) {
                this.colony = this.kingdom.getColonyHandler().getColony((String) serialized.get("colony"));
                if (colony != null) {
                    colony.getClaimController().addPlot(this);
                } else {
                    this.colony = null;
                }
            }
        }
        
        if (serialized.containsKey("world")) {
            this.world = Bukkit.getWorld((String) serialized.get("world"));
        }
        
        Location pos1 = null, pos2 = null;
        if (serialized.containsKey("pos1")) {
            pos1 = (Location) serialized.get("pos1");
        }
        
        if (serialized.containsKey("pos2")) {
            pos2 = (Location) serialized.get("pos2");
        }
        setBounds(pos1, pos2);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        if (hasKingdom()) { serialized.put("kingdom", this.kingdom.getUniqueId().toString()); }
        if (hasTown()) { serialized.put("town", this.town.getName()); }
        if (hasColony()) { serialized.put("colony", this.colony.getName()); }
        serialized.put("world", this.world.getName());
        serialized.put("pos1", new Location(world, xMax, yMax, zMax));
        serialized.put("pos2", new Location(world, xMin, yMin, zMin));
        return serialized;
    }
    
    public boolean hasKingdom() {
        return this.kingdom != null;
    }
    
    public boolean hasColony() {
        return hasKingdom() && this.colony != null;
    }
    
    public boolean hasTown() {
        return hasKingdom() && this.town != null;
    }
    
    public Kingdom getKingdom() {
        return kingdom;
    }
    
    public void setKingdom(Kingdom kingdom) {
        this.kingdom = kingdom;
        this.colony = null;
        this.town = null;
    }
    
    public Colony getColony() {
        return colony;
    }
    
    public void setColony(Colony colony) {
        this.colony = colony;
    }
    
    public Town getTown() {
        return town;
    }
    
    public void setTown(Town town) {
        this.town = town;
    }
    
    public String toString() {
        return "(" + this.xMin + "," + this.zMin + ") - (" + xMax + "," + zMax + ")";
    }
}