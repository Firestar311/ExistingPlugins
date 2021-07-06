package com.stardevmc.titangames.arenas.chests;

import com.firestar311.lib.pagination.IElement;
import org.bukkit.Location;

import java.util.Objects;

public class ArenaChest implements IElement {
 
    private Location location;
    private int chestTeir;
    
    public ArenaChest(Location location, int chestTeir) {
        this.location = location;
        this.chestTeir = chestTeir;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public int getChestTeir() {
        return chestTeir;
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArenaChest that = (ArenaChest) o;
        return Objects.equals(location, that.location);
    }
    
    public int hashCode() {
        return Objects.hash(location);
    }
    
    public String formatLine(String... args) {
        return "&aChest at (" + this.location.getX() + ", " + this.location.getY() + ", " + this.location.getZ() + ") is of tier " + this.chestTeir;
    }
}