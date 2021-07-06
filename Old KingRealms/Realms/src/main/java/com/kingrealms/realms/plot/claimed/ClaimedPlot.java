package com.kingrealms.realms.plot.claimed;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.plot.Plot;
import com.kingrealms.realms.territory.base.Territory;
import com.starmediadev.lib.pagination.IElement;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("ClaimedPlot")
public abstract class ClaimedPlot implements ConfigurationSerializable, IElement {
    private UUID actor; //mysql
    private long date; //mysql
    private String id; //mysql
    private Plot plot; //cache
    private Territory territory; //cache
    private String territoryId;
    
    public ClaimedPlot(Territory territory, Plot plot, UUID actor, long date) {
        this(territory, plot.getUniqueId(), actor, date);
    }
    
    public ClaimedPlot(Territory territory, String plot, UUID actor, long date) {
        territoryId = territory.getUniqueId();
        this.id = plot;
        this.actor = actor;
        this.date = date;
        this.territory = territory;
    }
    
    public ClaimedPlot(Map<String, Object> serialized) {
        this.territoryId = (String) serialized.get("territoryId");
        this.id = (String) serialized.get("plot");
        this.actor = UUID.fromString((String) serialized.get("actor"));
        this.date = Long.parseLong((String) serialized.get("date"));
    }
    
    public boolean contains(Location location) {
        return getPlot().contains(location);
    }
    
    public Plot getPlot() {
        if (this.plot == null) {
            this.plot = Realms.getInstance().getPlotManager().getPlot(this.id);
        }
        return plot;
    }
    
    public String getId() {
        return id;
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("plot", this.id);
        serialized.put("actor", this.actor.toString());
        serialized.put("date", this.date + "");
        serialized.put("territoryId", this.territoryId);
        return serialized;
    }
    
    public UUID getActor() {
        return actor;
    }
    
    public long getDate() {
        return date;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(plot);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ClaimedPlot plot1 = (ClaimedPlot) o;
        return Objects.equals(plot, plot1.plot);
    }
    
    @Override
    public String formatLine(String... args) {
        String actor = Realms.getInstance().getProfileManager().getProfile(this.actor).getName();
        return getPlot().formatLine(args) + " " + actor;
    }
    
    public Territory getOwner() {
        if (this.territory == null) {
            this.territory = Realms.getInstance().getTerritoryManager().getTerritory(this.territoryId);
        }
        
        return territory;
    }
}