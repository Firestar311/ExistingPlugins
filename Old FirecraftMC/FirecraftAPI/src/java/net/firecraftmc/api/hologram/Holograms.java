package net.firecraftmc.api.hologram;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public final class Holograms {
    private static final Holograms instance = new Holograms();
    
    private static final Map<Location, Hologram> holograms = new HashMap<>();
    
    public static Holograms getInstance() {
        return instance;
    }
    
    private Holograms() {}
    
    public static Hologram createHologram(String name, Location location, String text) {
        Hologram hologram = new Hologram(name, location, text);
        hologram.spawn();
        holograms.put(location, hologram);
        return hologram;
    }
    
    public static Hologram getHologram(String name) {
        for (Hologram hologram : holograms.values()) {
            if (hologram.getName().equalsIgnoreCase(name)) {
                return hologram;
            }
        }
        return null;
    }
    
    public static Hologram getHologram(Location location) {
        return holograms.get(location);
    }
    
}
