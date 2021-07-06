package com.stardevmc.titanterritories.core.manager;

import com.firestar311.lib.config.ConfigManager;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.holder.Town;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class TownManager {
    
    private ConfigManager configManager;
    private Map<UUID, Town> towns = new HashMap<>();
    
    public TownManager() {
        this.configManager = new ConfigManager(TitanTerritories.getInstance(), "towns");
        this.configManager.setup();
    }
    
    public void saveData() {
        FileConfiguration config = configManager.getConfig();
        for (Town town : this.towns.values()) {
            config.set("towns." + town.getUniqueId().toString(), town);
        }
        this.configManager.saveConfig();
    }
    
    public void loadData() {
        FileConfiguration config = configManager.getConfig();
        if (!config.contains("towns")) { return; }
        for (String k : config.getConfigurationSection("towns").getKeys(false)) {
            Town town = (Town) config.get("towns." + k);
            this.towns.put(town.getUniqueId(), town);
            town.getKingdom().getTownHandler().addTown(town);
        }
    }
    
    public void removeTown(Town town) {
        this.towns.remove(town.getUniqueId());
    }
    
    public void addTown(Town town) {
        if (town.getUniqueId() == null) {
            town.setUniqueId(generateUUID());
        }
        this.towns.put(town.getUniqueId(), town);
    }
    
    private UUID generateUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (this.towns.containsKey(uuid));
        return uuid;
    }
    
    public Town getTown(String name) {
        for (Town town : towns.values()) {
            if (town.getName().equalsIgnoreCase(name.toLowerCase())) {
                return town;
            }
        }
        return null;
    }
    
    public List<Town> getTowns() {
        return new ArrayList<>(towns.values());
    }
    
    public Town getTown(Location location) {
        for (Town town : this.towns.values()) {
            if (town.getClaimController().contains(location)) {
                return town;
            }
        }
        return null;
    }
    
    public Town getTown(UUID uuid) {
        return towns.get(uuid);
    }
}