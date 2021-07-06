package net.firecraftmc.api.regions;

import net.firecraftmc.api.exceptions.CornersNotInSameWorldException;
import org.bukkit.Location;
import org.bukkit.World;

public class Selection {
    
    private World world;
    private Location minimum, maximum;
    
    public Selection(Location minimum, Location maximum) throws CornersNotInSameWorldException {
        if (!minimum.getWorld().getName().equalsIgnoreCase(maximum.getWorld().getName())) {
            throw new CornersNotInSameWorldException();
        }
        
        this.world = minimum.getWorld();
        this.minimum = minimum;
        this.maximum = maximum;
    }
    
    public Selection() {}
    
    public Location getMinimum() {
        return minimum;
    }
    
    public void setMinimum(Location minimum) throws CornersNotInSameWorldException {
        if (maximum != null) {
            if (!maximum.getWorld().getName().equalsIgnoreCase(minimum.getWorld().getName())) {
                throw new CornersNotInSameWorldException();
            }
        }
        
        this.minimum = minimum;
        if (world == null) this.world = minimum.getWorld();
    }
    
    public Location getMaximum() {
        return maximum;
    }
    
    public void setMaximum(Location maximum) throws CornersNotInSameWorldException {
        if (minimum != null) {
            if (!minimum.getWorld().getName().equalsIgnoreCase(maximum.getWorld().getName())) {
                throw new CornersNotInSameWorldException();
            }
        }
        
        this.maximum = maximum;
        if (world == null) this.world = maximum.getWorld();
    }
    
    public World getWorld() {
        return world;
    }
    
    public boolean hasMinimum() {
        return minimum != null;
    }
    
    public boolean hasMaximum() {
        return maximum != null;
    }
}