package com.stardevmc.titangames.arenas;

import com.firestar311.lib.region.Cuboid;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.chests.ArenaChest;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class ArenaManager {
    
    private File file;
    private FileConfiguration config;
    
    private List<Arena> arenas = new ArrayList<>();
    
    public ArenaManager(TitanGames plugin) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        file = new File(plugin.getDataFolder(), "arenas.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        config = YamlConfiguration.loadConfiguration(file);
    }
    
    public Arena getArena(Location location) {
        for (Arena arena : arenas) {
            if (arena.getRegion().contains(location)) {
                return arena;
            }
        }
        
        return null;
    }
    
    public void addArena(Arena arena) {
        this.arenas.add(arena);
        arena.setup();
    }
    
    public Arena getArena(String id) {
        if (id == null) return null;
        for (Arena arena : arenas) {
            if (arena.getName().equalsIgnoreCase(id)) {
                return arena;
            }
        }
        return null;
    }
    
    public Arena getArena(Player player) {
        for (Arena arena : arenas) {
            if (arena.hasPlayer(player)) {
                return arena;
            }
        }
        return null;
    }
    
    public void removeArena(Arena arena) {
        arena.removeArenaCleanup();
        this.arenas.remove(arena);
    }
    
    public void saveArenas() {
        this.config.set("arenas", null);
        saveConfig();
        for (Arena arena : this.arenas) {
            String basePath = "arenas." + arena.getName() + ".";
            config.set(basePath + "region.min", arena.getRegion().getMinimum());
            config.set(basePath + "region.max", arena.getRegion().getMaximum());
            for (Entry<Integer, Spawn> entry : arena.getSpawnpoints().entrySet()) {
                config.set(basePath + "spawns." + entry.getKey(), entry.getValue().getLocation());
            }
    
            int chestCounter = 0;
            for (ArenaChest chest: arena.getChests()) {
                config.set(basePath + "chests." + chestCounter + ".location", chest.getLocation());
                config.set(basePath + "chests." + chestCounter + ".teir", chest.getChestTeir());
                chestCounter++;
            }
        }
        saveConfig();
    }
    
    public void loadArenas() {
        this.arenas.clear();
        ConfigurationSection arenasSection = this.config.getConfigurationSection("arenas");
        if (arenasSection == null) return;
        for (String a : arenasSection.getKeys(false)) {
            String basePath = "arenas." + a + ".";
            Location min = (Location) config.get(basePath + "region.min");
            Location max = (Location) config.get(basePath + "region.max");
            Cuboid region = new Cuboid(min, max);
            Arena arena = new Arena(a, region);
            ConfigurationSection spawnSection = this.config.getConfigurationSection(basePath + "spawns");
            if (spawnSection != null) {
                for (String s : spawnSection.getKeys(false)) {
                    arena.addSpawn(new Spawn((Location) config.get(basePath + "spawns." + s)));
                }
            }
            
            ConfigurationSection chestSection = this.config.getConfigurationSection(basePath + "chests");
            if (chestSection != null) {
                for (String c : chestSection.getKeys(false)) {
                    Location location = (Location) config.get(basePath + "chests." + c + ".location");
                    int teir = config.getInt(basePath + "chests." + c + ".teir");
                    arena.addChest(new ArenaChest(location, teir));
                }
            }
            this.arenas.add(arena);
        }
    }
    
    private void saveConfig() {
        try {
            this.config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<Arena> getArenas() {
        return new ArrayList<>(arenas);
    }
}