package com.stardevmc.titangames.signs;

import com.firestar311.lib.pagination.IElement;
import com.stardevmc.titangames.arenas.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.Objects;

public class LobbySign implements IElement {

    private Location location;
    private Arena arena;
    
    public LobbySign(Location location, Arena arena) {
        this.location = location;
        this.arena = arena;
    }
    
    public LobbySign(Sign sign, Arena arena) {
        this.location = sign.getLocation();
        this.arena = arena;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public Arena getArena() {
        return arena;
    }
    
    public void setArena(Arena arena) {
        this.arena = arena;
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LobbySign lobbySign = (LobbySign) o;
        return Objects.equals(location, lobbySign.location);
    }
    
    public int hashCode() {
        return Objects.hash(location);
    }
    
    public void updateSign() {
        Sign sign = (Sign) location.getBlock().getState();
        sign.setLine(0, arena.getName());
        sign.setLine(1, arena.getTributes().size() + "/" + arena.getSpawnpoints().size());
        sign.setLine(2, arena.getState().toString());
        sign.update();
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendSignChange(sign.getLocation(), sign.getLines());
        }
    }
    
    public String formatLine(String... args) {
        return "&aSign at (" + this.location.getX() + ", " + this.location.getY() + ", " + this.location.getZ() + ") is linked to arena " + this.arena.getName();
    }
}