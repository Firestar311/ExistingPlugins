package net.firecraftmc.api.regions;

import net.firecraftmc.api.model.player.FirecraftPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Iterator;

public interface ICuboid {
    
    Iterator<Block> getBlockList();
    Location getCenter();
    double getDistance();
    double getDistanceSquared();
    int getHeight();
    Location getMinimum();
    Location getMaximum();
    Location getRandomLocation();
    int getTotalBlockSize();
    int getXWidth();
    int getZWidth();
    boolean contains(Location loc);
    boolean contains(FirecraftPlayer player);
    boolean contains(Location loc, double marge);
}