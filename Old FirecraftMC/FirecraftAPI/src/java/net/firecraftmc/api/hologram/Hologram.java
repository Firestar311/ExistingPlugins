package net.firecraftmc.api.hologram;

import net.firecraftmc.api.util.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class Hologram {
    
    private final String name;
    private final Location location;
    private final World world;
    private ArmorStand entity;
    private final String text;
    
    //To support multiple lines need a new entity for each line
    
    Hologram(String name, Location location, String text) {
        this.name = name;
        this.world = location.getWorld();
        this.location = location;
        this.text = text;
        spawn();
    }
    
    public void spawn() {
        entity = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        entity.setGravity(false);
        entity.setCustomName(Utils.color(text));
        entity.setCustomNameVisible(true);
    }
    
    public World getWorld() {
        return world;
    }
    
    public String getText() {
        return text;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void despawn() {
        entity.remove();
    }
    
    public String getName() {
        return name;
    }
}