package com.firestar311.lib.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    
    protected JavaPlugin plugin;
    protected File file;
    protected FileConfiguration config;
    
    public ConfigManager(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), name + ".yml");
    }
    
    private synchronized void createFile() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create " + file.getName());
            }
        }
    }
    
    private synchronized void loadConfiguration() {
        if (file == null) {
            createFile();
        }
        config = YamlConfiguration.loadConfiguration(file);
    }
    
    public void setup() {
        //Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            createFile();
            loadConfiguration();
        //});
    }
    
    public void saveConfig() {
        Runnable saveRunnable = () -> {
            if (file != null && config != null) {
                try {
                    this.config.save(file);
                } catch (IOException e) {
                    plugin.getLogger().severe("Could not save the file " + file.getName());
                }
            } else {
                plugin.getLogger().severe("Could not save the file " + file.getName());
            }
        };
        if (plugin.isEnabled()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, saveRunnable);
        } else {
            saveRunnable.run();
        }
    }
    
    public void reloadConfig() {
        loadConfiguration();
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
}