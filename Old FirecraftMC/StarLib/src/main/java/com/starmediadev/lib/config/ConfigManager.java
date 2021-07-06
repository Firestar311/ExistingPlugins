package com.starmediadev.lib.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    
    protected FileConfiguration config;
    protected File file;
    protected JavaPlugin plugin;
    
    public ConfigManager(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), name + ".yml");
    }
    
    protected ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void saveConfig() {
        if (file != null && config != null) {
            try {
                this.config.save(file);
            } catch (IOException e) {
                System.out.println("Could not save the file " + file.getName());
            }
        } else {
            System.out.println("Could not save the file " + file.getName());
        }
    }
    
    public void reloadConfig() {
        loadConfiguration();
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public void setup() {
        //Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        createFile();
        loadConfiguration();
        //});
    }
    
    private synchronized void createFile() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Could not create " + file.getName());
            }
        }
    }
    
    private synchronized void loadConfiguration() {
        if (file == null) {
            createFile();
        }
        config = YamlConfiguration.loadConfiguration(file);
    }
}