package com.kingrealms.realms.serverclaim;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.plot.Plot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;
import java.util.Map.Entry;

@SerializableAs("ServerClaim")
public abstract class ServerClaim implements ConfigurationSerializable {
    protected Set<String> plots = new HashSet<>();
    
    protected ServerClaim() {}
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        int i = 0;
        for (String plot : this.plots) {
            serialized.put("plot-" + i, plot);
            i++;
        }
        return serialized;
    }
    
    public ServerClaim(Map<String, Object> serialized) {
        for (Entry<String, Object> entry : serialized.entrySet()) {
            if (entry.getKey().startsWith("plot-")) {
                plots.add((String) entry.getValue());
            }
        }
    }
    
    public Set<Plot> getPlots() {
        Set<Plot> plots = new HashSet<>();
        for (String p : this.plots) {
            plots.add(Realms.getInstance().getPlotManager().getPlot(p));
        }
        
        return plots;
    }
    
    public void addPlot(Plot plot) {
        this.plots.add(plot.getUniqueId());
    }
    
    public boolean contains(Location location) {
        for (String p : this.plots) {
            Plot plot = Realms.getInstance().getPlotManager().getPlot(p);
            if (plot.contains(location)) {
                return true;
            }
        }
        return false;
    }
    
    public void removePlot(Plot plot) {
        this.plots.remove(plot.getUniqueId());
    }
    
    public boolean contains(World world, int x, int y, int z) {
        for (String p : this.plots) {
            Plot plot = Realms.getInstance().getPlotManager().getPlot(p);
            if (plot.contains(world, x, y, z)) {
                return true;
            }
        }
        return false;
    }
    
    public abstract String getName();
}