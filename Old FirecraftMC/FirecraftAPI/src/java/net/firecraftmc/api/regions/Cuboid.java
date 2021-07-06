package net.firecraftmc.api.regions;

import net.firecraftmc.api.model.player.FirecraftPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;

public class Cuboid implements ICuboid {
    
    protected final World world;
    protected final int xMin, yMin, zMin, xMax, yMax, zMax;
    protected final double xMinCentered, yMinCentered, zMinCentered, xMaxCentered, yMaxCentered, zMaxCentered;
    
    public Cuboid (Location minLoc, Location maxLoc) {
        this.world = minLoc.getWorld();
        this.xMin = minLoc.getBlockX();
        this.yMin = minLoc.getBlockY();
        this.zMin = minLoc.getBlockZ();
        this.xMax = maxLoc.getBlockX();
        this.yMax = maxLoc.getBlockY();
        this.zMax = maxLoc.getBlockZ();
        this.xMinCentered = this.xMin + 0.5;
        this.xMaxCentered = this.xMax + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMinCentered = this.zMin + 0.5;
        this.zMaxCentered = this.zMax + 0.5;
    }
    
    public Iterator<Block> getBlockList() {
        final List<Block> bL = new ArrayList<>(this.getTotalBlockSize());
        for(int x = this.xMin; x <= this.xMax; ++x) {
            for(int y = this.yMin; y <= this.yMax; ++y) {
                for(int z = this.zMin; z <= this.zMax; ++z) {
                    Block b = this.world.getBlockAt(x, y, z);
                    bL.add(b);
                }
            }
        }
        return bL.iterator();
    }
    
    public Location getCenter() {
        return new Location(this.world, (this.xMax - this.xMin) / 2 + this.xMin, (this.yMax - this.yMin) / 2 + this.yMin, (this.zMax - this.zMin) / 2 + this.zMin);
    }
    
    public double getDistance() {
        return this.getMinimum().distance(this.getMaximum());
    }
    
    public double getDistanceSquared() {
        return this.getMinimum().distanceSquared(this.getMaximum());
    }
    
    public int getHeight() {
        return this.yMax - this.yMin + 1;
    }
    
    public Location getMinimum() {
        return new Location(this.world, this.xMin, this.yMin, this.zMin);
    }
    
    public Location getMaximum() {
        return new Location(this.world, this.xMax, this.yMax, this.zMax);
    }
    
    public Location getRandomLocation() {
        final Random rand = new Random();
        final int x = rand.nextInt(Math.abs(this.xMax - this.xMin) + 1) + this.xMin;
        final int y = rand.nextInt(Math.abs(this.yMax - this.yMin) + 1) + this.yMin;
        final int z = rand.nextInt(Math.abs(this.zMax - this.zMin) + 1) + this.zMin;
        return new Location(this.world, x, y, z);
    }
    
    public int getTotalBlockSize() {
        return this.getHeight() * this.getXWidth() * this.getZWidth();
    }
    
    public int getXWidth() {
        return this.xMax - this.xMin + 1;
    }
    
    public int getZWidth() {
        return this.zMax - this.zMin + 1;
    }
    
    public boolean contains(Location loc) {
        return loc.getWorld() == this.world && loc.getBlockX() >= this.xMin && loc.getBlockX() <= this.xMax && loc.getBlockY() >= this.yMin && loc.getBlockY() <= this.yMax && loc
                .getBlockZ() >= this.zMin && loc.getBlockZ() <= this.zMax;
    }
    
    public boolean contains(FirecraftPlayer player) {
        return this.contains(player.getLocation());
    }
    
    public boolean contains(Location loc, double marge) {
        return loc.getWorld() == this.world && loc.getX() >= this.xMinCentered - marge && loc.getX() <= this.xMaxCentered + marge && loc.getY() >= this.yMinCentered - marge && loc
                .getY() <= this.yMaxCentered + marge && loc.getZ() >= this.zMinCentered - marge && loc.getZ() <= this.zMaxCentered + marge;
    }
}