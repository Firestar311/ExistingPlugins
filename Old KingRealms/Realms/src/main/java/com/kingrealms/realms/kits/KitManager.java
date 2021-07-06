package com.kingrealms.realms.kits;

import com.kingrealms.realms.storage.StorageManager;
import com.starmediadev.lib.collection.IncrementalMap;
import com.starmediadev.lib.config.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collection;

public class KitManager {
    
    private ConfigManager configManager = StorageManager.kitsConfig;
    private IncrementalMap<Kit> kits = new IncrementalMap<>();
    
    public KitManager() {
        configManager.setup();
    }
    
    public void saveData() {
        for (Kit kit : kits.values()) {
            configManager.getConfig().set("kits." + kit.getId(), kit);
        }
        
        configManager.saveConfig();
    }
    
    public void loadData() {
        ConfigurationSection kitsSection = configManager.getConfig().getConfigurationSection("kits");
        if (kitsSection == null) return;
        for (String k : kitsSection.getKeys(false)) {
            Kit kit = (Kit) kitsSection.get(k);
            this.kits.add(kit);
        }
    }
    
    public Kit getKit(String name) {
        for (Kit kit : this.kits.values()) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }
        return null;
    }
    
    public void addKit(Kit kit) {
        int pos = this.kits.add(kit);
        kit.setId(pos);
    }
    
    public void removeKit(Kit kit) {
        this.kits.remove(kit.getId());
    }
    
    public Collection<Kit> getKits() {
        return new ArrayList<>(this.kits.values());
    }
    
    public int getNextPosition() {
        return this.kits.lastKey() + 1;
    }
}