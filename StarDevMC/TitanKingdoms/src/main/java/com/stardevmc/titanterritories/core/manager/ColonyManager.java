package com.stardevmc.titanterritories.core.manager;

import com.firestar311.lib.config.ConfigManager;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.holder.Colony;
import com.stardevmc.titanterritories.core.objects.holder.Town;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ColonyManager {
    
    private Map<UUID, Colony> colonies = new HashMap<>();
    
    private ConfigManager configManager;
    
    public ColonyManager() {
        this.configManager = new ConfigManager(TitanTerritories.getInstance(), "colonies");
        this.configManager.setup();
    }
    
    public void saveData() {
        FileConfiguration config = configManager.getConfig();
        for (Colony town : this.colonies.values()) {
            config.set("colonies." + town.getUniqueId().toString(), town);
        }
        this.configManager.saveConfig();
    }
    
    public void loadData() {
        FileConfiguration config = configManager.getConfig();
        if (!config.contains("colonies")) return;
        for (String k : config.getConfigurationSection("colonies").getKeys(false)) {
            Colony colony = (Colony) config.get("colonies." + k);
            this.colonies.put(colony.getUniqueId(), colony);
            colony.getKingdom().getColonyHandler().addColony(colony);
        }
    }
    
    public void removeColony(Town town) {
        this.colonies.remove(town.getUniqueId());
    }
    
    public Colony getColony(String name) {
        for (Colony colony : colonies.values()) {
            if (colony.getName().equalsIgnoreCase(name.toLowerCase())) {
                return colony;
            }
        }
        return null;
    }
    
    public void addColony(Colony colony) {
        if (colony.getUniqueId() == null) {
            colony.setUniqueId(generateUUID());
        }
        this.colonies.put(colony.getUniqueId(), colony);
    }
    
    public List<Colony> getColonies() {
        return new ArrayList<>(colonies.values());
    }
    
    public Colony getColony(Location location) {
        for (Colony colony : this.colonies.values()) {
            if (colony.getClaimController().contains(location)) {
                return colony;
            }
        }
        return null;
    }
    
    public Colony getColony(UUID uuid) {
        return colonies.get(uuid);
    }
    
    private UUID generateUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (this.colonies.containsKey(uuid));
        return uuid;
    }
}