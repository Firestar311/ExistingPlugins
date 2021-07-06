package com.stardevmc.titangames.kits;

import com.stardevmc.titangames.TitanGames;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class KitManager {
    
    private File file;
    private FileConfiguration config;
    
    private List<Kit> kits = new ArrayList<>();
    
    public KitManager(TitanGames plugin) {
        file = new File(plugin.getDataFolder(), "kits.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        config = YamlConfiguration.loadConfiguration(file);
    }
    
    public void addKit(Kit kit) {
        this.kits.add(kit);
    }
    
    public void removeKit(Kit kit) {
        this.kits.remove(kit);
    }
    
    public void removeKit(String name) {
        Kit target = null;
        for (Kit kit : this.kits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                target = kit;
            }
        }
        
        this.kits.remove(target);
    }
    
    public Kit getKit(String name) {
        for (Kit kit : this.kits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }
        return null;
    }
    
    public List<Kit> getKits() {
        return new ArrayList<>(kits);
    }
    
    public void saveKits() {
        this.config.set("kits", null);
        saveFile();
        for (Kit kit : this.kits) {
            config.set("kits." + kit.getName() + ".permission", kit.getPermission());
            config.set("kits." + kit.getName() + ".displaymaterial", kit.getDisplayMaterial().name());
            List<ItemStack> items = kit.getItems();
            int counter = 0;
            for (ItemStack item : items) {
                config.set("kits." + kit.getName() + ".items." + counter, item);
                counter++;
            }
        }
        saveFile();
    }
    
    public void loadKits() {
        this.kits.clear();
        ConfigurationSection kitSection = this.config.getConfigurationSection("kits");
        if (kitSection == null) return;
        for (String kn : kitSection.getKeys(false)) {
            String permission = this.config.getString("kits." + kn + ".permission");
            Material displayMaterial = Material.getMaterial(this.config.getString("kits." + kn + ".displaymaterial"));
            ConfigurationSection itemSection = config.getConfigurationSection("kits." + kn + ".items");
            if (itemSection == null) {
                this.kits.add(new Kit(kn, permission, displayMaterial));
                continue;
            }
            
            List<ItemStack> itemStacks = new ArrayList<>();
            for (String i : itemSection.getKeys(false)) {
                itemStacks.add(config.getItemStack("kits." + kn + ".items." + i));
            }
            
            this.kits.add(new Kit(kn, permission, itemStacks, displayMaterial));
        }
    }
    
    private void saveFile() {
        try {
            this.config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}