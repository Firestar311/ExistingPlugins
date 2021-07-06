package com.firestar311.lib.superadmins;

import com.firestar311.lib.FireLib;
import com.firestar311.lib.config.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class SuperAdminManager {
    
    private ConfigManager configManager;
    
    private boolean enabled, global;
    
    private Set<UUID> globalSuperadmins = new HashSet<>();
    private Set<SuperAdminProvider> providers = new HashSet<>();
    
    public SuperAdminManager(FireLib plugin) {
        this.configManager = new ConfigManager(plugin, "superadmins");
        this.configManager.setup();
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isGlobal() {
        return global;
    }
    
    public void setGlobal(boolean global) {
        this.global = global;
    }
    
    public void addGlobalSuperAdmin(UUID uuid) {
        this.globalSuperadmins.add(uuid);
    }
    
    public void removeGlobalSuperAdmin(UUID uuid) {
        this.globalSuperadmins.remove(uuid);
    }
    
    public boolean isGlobalSuperAdmin(UUID uuid) {
        return enabled && global && this.globalSuperadmins.contains(uuid);
    }
    
    public List<UUID> getGlobalSuperAdmins() {
        if (!enabled) return null;
        else return new ArrayList<>(this.globalSuperadmins);
    }
    
    public void loadData() {
        FileConfiguration config = configManager.getConfig();
        List<String> data = config.getStringList("superadmins");
        for (String u : data) {
            globalSuperadmins.add(UUID.fromString(u));
        }
    }
    
    public void saveData() {
        FileConfiguration config = configManager.getConfig();
        List<String> data = new ArrayList<>();
        for (UUID uuid : globalSuperadmins) {
            data.add(uuid.toString());
        }
        config.set("superadmins", data);
        configManager.saveConfig();
    }
    
    public void registerProvider(SuperAdminProvider provider) {
        this.providers.add(provider);
    }
    
    public SuperAdminProvider getProvider(JavaPlugin plugin) {
        if (!enabled) return null;
        for (SuperAdminProvider provider : providers) {
            if (plugin.getName().equalsIgnoreCase(provider.getPlugin().getName())) {
                return provider;
            }
        }
        return null;
    }
    
    public List<SuperAdminProvider> getProviders(UUID superAdmin) {
        if (!enabled) return null;
        List<SuperAdminProvider> userProviders = new ArrayList<>();
        for (SuperAdminProvider provider : providers) {
            if (provider.isSuperAdmin(superAdmin)) {
                userProviders.add(provider);
            }
        }
        return userProviders;
    }
    
    public List<SuperAdminProvider> getProviders() {
        return new ArrayList<>(providers);
    }
}