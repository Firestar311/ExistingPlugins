package com.starmediadev.lib.region;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.*;

@SerializableAs("Cuboid")
public class Cuboid implements ConfigurationSerializable {
    
    protected String worldName;
    protected World world;
    protected int xMin, yMin, zMin, xMax, yMax, zMax;
    protected double xMinCentered, yMinCentered, zMinCentered, xMaxCentered, yMaxCentered, zMaxCentered;
    
    public Cuboid (Location pos1, Location pos2) {
        setBounds(pos1, pos2);
    }
    
    public Cuboid() {
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{ 
            put("world", getWorld().getName());
            put("xMin", xMin + "");
            put("yMin", yMin + "");
            put("zMin", zMin + "");
            put("xMax", xMax + "");
            put("yMax", yMax + "");
            put("zMax", zMax + "");
        }};
    }
    
    public Cuboid(Map<String, Object> serialized) {
        this.worldName = (String) serialized.get("world");
        this.xMin = Integer.parseInt((String) serialized.get("xMin"));
        this.yMin = Integer.parseInt((String) serialized.get("yMin"));
        this.zMin = Integer.parseInt((String) serialized.get("zMin"));
        this.xMax = Integer.parseInt((String) serialized.get("xMax"));
        this.yMax = Integer.parseInt((String) serialized.get("yMax"));
        this.zMax = Integer.parseInt((String) serialized.get("zMax"));
    } 
    
    public Iterator<Block> getBlockList() {
        return getBlockList(false);
    }
    
    public Iterator<Block> getBlockList(boolean removeAir) {
        final List<Block> bL = new ArrayList<>(this.getTotalBlockSize());
        for(int x = this.xMin; x <= this.xMax; ++x) {
            for(int y = this.yMin; y <= this.yMax; ++y) {
                for(int z = this.zMin; z <= this.zMax; ++z) {
                    Block b = getWorld().getBlockAt(x, y, z);
                    if (b.getType().equals(Material.AIR)) {
                        if (removeAir) {
                            continue;
                        }
                    }
                    
                    bL.add(b);
                }
            }
        }
        return bL.iterator();
    }
    
    public World getWorld() {
        if (this.world == null) {
            this.world = Bukkit.getWorld(this.worldName);
        }
        return world;
    }
    
    public Location getCenter() {
        return new Location(getWorld(), (this.xMax - this.xMin) / 2.0 + this.xMin, (this.yMax - this.yMin) / 2.0 + this.yMin, (this.zMax - this.zMin) / 2.0 + this.zMin);
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
        return new Location(getWorld(), this.xMin, this.yMin, this.zMin);
    }
    
    public Location getMaximum() {
        return new Location(getWorld(), this.xMax, this.yMax, this.zMax);
    }
    
    public Location getRandomLocation() {
        final Random rand = new Random();
        final int x = rand.nextInt(Math.abs(this.xMax - this.xMin) + 1) + this.xMin;
        final int y = rand.nextInt(Math.abs(this.yMax - this.yMin) + 1) + this.yMin;
        final int z = rand.nextInt(Math.abs(this.zMax - this.zMin) + 1) + this.zMin;
        return new Location(getWorld(), x, y, z);
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
        if (loc == null) return false;
        int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
        return (loc.getWorld() == getWorld() && x >= this.xMin && x <= this.xMax && y >= this.yMin && y <= this.yMax && z >= this.zMin
                && z <= this.zMax);
    }
    
    public boolean contains(World world, int x, int y, int z) {
        return (world == getWorld() && x >= this.xMin && x <= this.xMax && y >= this.yMin && y <= this.yMax && z >= this.zMin
                && z <= this.zMax);
    }
    
//    public boolean contains(Location loc) {
//        return loc.toVector().isInAABB(new Location(this.world, xMin, yMin, zMin).toVector(), new Location(this.world, xMax, yMax, zMax).toVector());
//    }
    
    public boolean contains(Player player) {
        return this.contains(player.getLocation());
    }
    
    public boolean contains(Location loc, double marge) {
        return loc.getWorld() == getWorld() && loc.getX() >= this.xMinCentered - marge && loc.getX() <= this.xMaxCentered + marge && loc.getY() >= this.yMinCentered - marge && loc
                .getY() <= this.yMaxCentered + marge && loc.getZ() >= this.zMinCentered - marge && loc.getZ() <= this.zMaxCentered + marge;
    }
    
    public void setBounds(Location pos1, Location pos2) {
        this.worldName = pos1.getWorld().getName();
        this.xMin = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.yMin = Math.min(pos1.getBlockY(), pos2.getBlockY());
        this.zMin = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        this.xMax = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.yMax = Math.max(pos1.getBlockY(), pos2.getBlockY());
        this.zMax = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        this.xMinCentered = this.xMin + 0.5;
        this.xMaxCentered = this.xMax + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMinCentered = this.zMin + 0.5;
        this.zMaxCentered = this.zMax + 0.5;
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Cuboid cuboid = (Cuboid) o;
        return xMin == cuboid.xMin && yMin == cuboid.yMin && zMin == cuboid.zMin && xMax == cuboid.xMax && yMax == cuboid.yMax && zMax == cuboid.zMax;
    }
    
    public int hashCode() {
        return Objects.hash(xMin, yMin, zMin, xMax, yMax, zMax);
    }
}