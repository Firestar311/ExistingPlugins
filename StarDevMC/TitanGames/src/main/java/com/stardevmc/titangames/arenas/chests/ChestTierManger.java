package com.stardevmc.titangames.arenas.chests;

import com.stardevmc.titangames.TitanGames;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class ChestTierManger {
    
    private File file;
    private FileConfiguration config;
    
    private Map<Integer, ChestTier> chestTiers = new HashMap<>();
    
    public ChestTierManger(TitanGames plugin) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        file = new File(plugin.getDataFolder(), "teirs.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
        config = YamlConfiguration.loadConfiguration(file);
    }
    
    public ChestTier getChestTeir(int teirNumber) {
        return this.chestTiers.get(teirNumber);
    }
    
    public void addChestTeir(ChestTier teir) {
        this.chestTiers.put(teir.getNumber(), teir);
    }
    
    public void saveChestTeirs() {
        config.set("teirs", null);
        saveConfig();
        for (Entry<Integer, ChestTier> entry : this.chestTiers.entrySet()) {
            String basePath = "teirs." + entry.getKey() + ".items.";
            config.set("teirs." + entry.getKey() + ".maxitems", entry.getValue().getMaxItems());
            int itemCounter = 0;
            for (Entry<ItemStack, Integer> itemEntry : entry.getValue().getItems().entrySet()) {
                config.set(basePath + itemCounter + ".item", itemEntry.getKey());
                config.set(basePath + itemCounter + ".chance", itemEntry.getValue());
                itemCounter++;
            }
        }
        saveConfig();
    }
    
    public void loadChestTeirs() {
        ConfigurationSection teirSection = config.getConfigurationSection("teirs");
        if (teirSection == null) return;
        
        for (String t : teirSection.getKeys(false)) {
            String basePath = "teirs." + t + ".";
            
            int teirNumber = Integer.parseInt(t);
            int maxItems = Integer.parseInt(this.config.getString(basePath + "maxitems"));
            ChestTier chestTeir = new ChestTier(teirNumber, maxItems);
            
            ConfigurationSection itemSection = config.getConfigurationSection(basePath + "items");
            if (itemSection != null) {
                for (String i : itemSection.getKeys(false)) {
                    ItemStack itemStack = config.getItemStack(basePath + "items." + i + ".item");
                    int chance = config.getInt(basePath + "items." + i + ".chance");
                    chestTeir.addItem(itemStack, chance);
                }
            }
            this.chestTiers.put(teirNumber, chestTeir);
        }
    }
    
    private void saveConfig() {
        try {
            this.config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<ChestTier> getChestTiers() {
        return new ArrayList<>(chestTiers.values());
    }
}