package com.starmediadev.lib.region;

import com.starmediadev.lib.exceptions.CornersNotInSameWorldException;
import org.bukkit.Location;
import org.bukkit.World;

public class Selection {
    
    private World world;
    private Location pointA, pointB;
    
    public Selection(Location pointA, Location pointB) throws CornersNotInSameWorldException {
        if (!pointA.getWorld().getName().equalsIgnoreCase(pointB.getWorld().getName())) {
            throw new CornersNotInSameWorldException();
        }
        
        this.world = pointA.getWorld();
        this.pointA = pointA;
        this.pointB = pointB;
    }
    
    public Selection() {}
    
    public Location getPointA() {
        return pointA;
    }
    
    public void setPointA(Location pointA) throws CornersNotInSameWorldException {
        if (pointB != null) {
            if (!pointB.getWorld().getName().equalsIgnoreCase(pointA.getWorld().getName())) {
                throw new CornersNotInSameWorldException();
            }
        }
        
        this.pointA = pointA;
        if (world == null) this.world = pointA.getWorld();
    }
    
    public Location getPointB() {
        return pointB;
    }
    
    public void setPointB(Location pointB) throws CornersNotInSameWorldException {
        if (pointA != null) {
            if (!pointA.getWorld().getName().equalsIgnoreCase(pointB.getWorld().getName())) {
                throw new CornersNotInSameWorldException();
            }
        }
        
        this.pointB = pointB;
        if (world == null) this.world = pointB.getWorld();
    }
    
    public World getWorld() {
        return world;
    }
    
    public boolean hasMinimum() {
        return pointA != null;
    }
    
    public boolean hasMaximum() {
        return pointB != null;
    }
}