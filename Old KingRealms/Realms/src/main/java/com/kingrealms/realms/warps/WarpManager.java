package com.kingrealms.realms.warps;

import com.kingrealms.realms.storage.StorageManager;
import com.kingrealms.realms.territory.base.Territory;
import com.kingrealms.realms.warps.type.*;
import com.starmediadev.lib.collection.IncrementalMap;
import com.starmediadev.lib.config.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class WarpManager {
    
    private final ConfigManager configManager = StorageManager.warpsConfig;
    private final IncrementalMap<Warp> warps = new IncrementalMap<>();
    
    public WarpManager() {
        this.configManager.setup();
    }
    
    public void saveData() {
        FileConfiguration config = configManager.getConfig();
        config.set("warps", null);
        warps.forEach((id, warp) -> config.set("warps." + id, warp));
        configManager.saveConfig();
    }
    
    public void loadData() {
        ConfigurationSection warpsSection = this.configManager.getConfig().getConfigurationSection("warps");
        if (warpsSection != null) {
            for (String i : warpsSection.getKeys(false)) {
                Warp warp = (Warp) warpsSection.get(i);
                this.warps.put(warp.getId(), warp);
            }
        }
    }
    
    public void addWarp(Warp warp) {
        int id = this.warps.add(warp);
        warp.setId(id);
    }
    
    public Warp getWarp(int id) {
        return this.warps.get(id);
    }
    
    public Warp getWarp(String name) {
        for (Warp warp : getWarps()) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return warp;
            }
        }
        
        return null;
    }
    
    public Collection<Warp> getWarps() {
        return new HashSet<>(this.warps.values());
    }
    
    public Collection<TerritoryWarp> getTerritoryWarps(Territory territory) {
        Set<TerritoryWarp> territoryWarps = new HashSet<>();
        for (TerritoryWarp warp : getTerritoryWarps()) {
            if (warp.getOwner().getUniqueId().equals(territory.getUniqueId())) {
                territoryWarps.add(warp);
            }
        }
        
        return territoryWarps;
    }
    
    public Collection<TerritoryWarp> getTerritoryWarps() {
        Set<TerritoryWarp> territoryWarps = new HashSet<>();
        for (Warp warp : getWarps()) {
            if (warp instanceof TerritoryWarp) {
                territoryWarps.add((TerritoryWarp) warp);
            }
        }
        
        return territoryWarps;
    }
    
    public ServerWarp getServerWarp(String name) {
        for (ServerWarp warp : getServerWarps()) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return warp;
            }
        }
        return null;
    }
    
    public Collection<ServerWarp> getServerWarps() {
        Set<ServerWarp> serverWarps = new HashSet<>();
        for (Warp warp : getWarps()) {
            if (warp instanceof ServerWarp) {
                serverWarps.add((ServerWarp) warp);
            }
        }
        
        return serverWarps;
    }
    
    public void removeWarp(Warp warp) {
        if (warp != null) { this.warps.remove(warp.getId()); }
    }
    
    public Warp getWarp(String owner, String name) {
        for (Warp warp : getWarps()) {
            if (owner.equalsIgnoreCase("server") || owner.equalsIgnoreCase("console")) {
                if (warp instanceof ServerWarp) {
                    if (warp.getName().equalsIgnoreCase(name)) {
                        return warp;
                    }
                }
            }
            
            if (warp instanceof TerritoryWarp) {
                TerritoryWarp territoryWarp = (TerritoryWarp) warp;
                if (territoryWarp.getOwner().getName().replace(" ", "_").equalsIgnoreCase(owner) || territoryWarp.getOwner().getUniqueId().equalsIgnoreCase(owner)) {
                    if (warp.getName().equalsIgnoreCase(name)) {
                        return warp;
                    }
                }
            }
        }
        
        return null;
    }
    
    public TerritoryWarp getTerritoryWarp(Territory territory, String name) {
        for (TerritoryWarp warp : getTerritoryWarps(territory)) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return warp;
            }
        }
        return null;
    }
}