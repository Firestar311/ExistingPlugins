package com.stardevmc.titangames.arenas;

import com.firestar311.lib.pagination.IElement;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Spawn implements IElement {
    
    private Location location;
    private Player player;
    
    public Spawn(Location location) {
        this.location = location;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spawn spawn = (Spawn) o;
        return Objects.equals(location, spawn.location);
    }
    
    public int hashCode() {
        return Objects.hash(location);
    }
    
    public String formatLine(String... args) {
        String line = "&aSpawn at (" + location.getX() + "," + location.getY() + "," + location.getZ() + ")";
        if (this.player != null) {
            line += " does has the player " + player.getName() + ".";
        } else {
            line += " does not have a player.";
        }
        return line;
    }
}