package com.kingrealms.realms.plot;

import com.starmediadev.lib.pagination.IElement;
import com.starmediadev.lib.region.Cuboid;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("Plot")
public class Plot extends Cuboid implements ConfigurationSerializable, IElement {
    
    //Mysql - coords
    protected String uniqueId; //to be replaced with mysql auto id
    
    public Plot(Location location) {
        super(location.getBlock().getChunk().getBlock(0, 0, 0).getLocation(), location.getBlock().getChunk().getBlock(15, 255, 15).getLocation());
    }
    
    public Plot(Map<String, Object> serialized) {
        super(serialized);
        this.uniqueId = (String) serialized.get("uniqueId");
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>(super.serialize()) {{ 
            put("uniqueId", uniqueId);
        }};
    }
    
    public String getUniqueId() {
        return uniqueId;
    }
    
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    @Override
    public String toString() {
        return "(" + this.xMin + "," + this.xMax + ") " + "(" + this.zMin + "," + this.zMax + ")";
    }
    
    @Override
    public String formatLine(String... args) {
        return " &8- &e" + this.getUniqueId() + " " + toString();
    }
}