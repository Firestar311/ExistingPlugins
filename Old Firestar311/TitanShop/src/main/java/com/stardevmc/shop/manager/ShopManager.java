package com.stardevmc.shop.manager;

import com.firestar311.lib.config.ConfigManager;
import com.firestar311.lib.util.Utils;
import com.stardevmc.shop.ShopUtils;
import com.stardevmc.shop.TitanShop;
import com.stardevmc.shop.objects.ShopItem;
import com.stardevmc.shop.objects.shops.gui.*;
import com.stardevmc.shop.objects.shops.sign.SignShop;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.Map.Entry;

public class ShopManager {
    
    private ConfigManager configManager;
    
    private Map<String, GUIShop> guiShops = new HashMap<>();
    private Map<String, SignShop> signShops = new HashMap<>();
    
    private TitanShop plugin;
    
    public ShopManager(TitanShop plugin) {
        this.plugin = plugin;
        this.configManager = new ConfigManager(plugin, "shops");
        this.configManager.setup();
    }
    
    public void loadShops() {
        FileConfiguration config = configManager.getConfig();
        if (config == null) return;
        if (!config.contains("shops")) return;
    
        ConfigurationSection guiShopsSection = config.getConfigurationSection("shops.gui");
        if (guiShopsSection == null) return;
        for (String s : guiShopsSection.getKeys(false)) {
            String basePath = "shops.gui." + s + ".";
            ItemStack displayStack = guiShopsSection.getItemStack("displaystack");
            
            GUIShop GUIShop = new GUIShop(s, displayStack);
            
            ConfigurationSection catSection = config.getConfigurationSection(basePath + "categories");
            if (catSection == null) {
                continue;
            }
            for (String cp : catSection.getKeys(false)) {
                int catPlace = Integer.parseInt(cp);
                String catName = catSection.getString(cp + ".name");
                ItemStack catIcon = catSection.getItemStack(cp + ".icon");
    
                GUIShopCategory category = new GUIShopCategory(GUIShop, catName, catIcon, catPlace);
                GUIShop.addCategory(category);
                ConfigurationSection itemSection = catSection.getConfigurationSection(cp + ".items");
                if (itemSection == null) continue;
                for (String ip : itemSection.getKeys(false)) {
                    int itemPlace = Integer.parseInt(ip);
                    ShopItem shopItem = plugin.getItemManager().getItem(UUID.fromString(itemSection.getString(ip + ".uuid")));
                    if (shopItem == null) continue;
                    GUIItem guiItem = new GUIItem(shopItem, category, itemPlace);
                    category.addShopItem(guiItem);
                }
            }
            this.guiShops.put(GUIShop.getName().toLowerCase(), GUIShop);
        }
    }
    
    public void saveShops() {
        FileConfiguration config = configManager.getConfig();
        if (config == null) return;
        config.set("shops", null);
        for (GUIShop guiShop : getGUIShops()) {
            String basePath = "shops.gui." + guiShop.getName() + ".";
            config.set(basePath + "name", guiShop.getName());
            config.set(basePath + "displaystack", guiShop.getIcon());
            String catBasePath = basePath + "categories.";
            for (Entry<Integer, GUIShopCategory> entry : guiShop.getCategories().entrySet()) {
                Integer place = entry.getKey();
                GUIShopCategory category = entry.getValue();
                String catPath = catBasePath + place + ".";
                config.set(catPath + "name", category.getName());
                config.set(catPath + "icon", category.getIcon());
                
                String itemBasePath = catPath + "items.";
                for (Entry<Integer, GUIItem> itemEntry : category.getShopItems().entrySet()) {
                    config.set(itemBasePath + itemEntry.getKey() + ".uuid", itemEntry.getValue().getUuid().toString());
                }
            }
        }
        
        int signCounter = 0;
        for (SignShop signShop : getSignShops()) {
            config.set("shops.sign." + signCounter + ".name",  signShop.getName());
            config.set("shops.sign." + signCounter + ".owner",  signShop.getOwner());
            config.set("shops.sign." + signCounter + ".location.sign", Utils.convertLocationToString(signShop.getSign()));
            config.set("shops.sign." + signCounter + ".location.chest", Utils.convertLocationToString(signShop.getChest()));
            config.set("shops.sign." + signCounter + ".item", signShop.getItem().getUuid().toString());
            signCounter++;
        }
        configManager.saveConfig();
    }
    
    public void addGUIShop(GUIShop guiShop) {
        this.guiShops.put(ShopUtils.formatUnlocalizedName(guiShop.getName()), guiShop);
    }
    
    public GUIShop getGUIShop(String name) {
        return this.guiShops.get(ShopUtils.formatUnlocalizedName(name));
    }
    
    public List<GUIShop> getGUIShops() {
        return new ArrayList<>(guiShops.values());
    }
    
    public boolean guiShopExists(String name) {
        return this.guiShops.containsKey(ShopUtils.formatUnlocalizedName(name));
    }
    
    public void addSignShop(SignShop shop) {
        this.signShops.put(ShopUtils.formatUnlocalizedName(shop.getName()), shop);
    }
    
    public SignShop getSignShop(String name) {
        return this.signShops.get(ShopUtils.formatUnlocalizedName(name));
    }
    
    public List<SignShop> getSignShops() {
        return new ArrayList<>(signShops.values());
    }
    
    public boolean signShopExists(String name) {
        return this.signShops.containsKey(ShopUtils.formatUnlocalizedName(name));
    }
    
    public void recalculateShopItems() {
        ItemManager itemManager = plugin.getItemManager();
        for (GUIShop guiShop : this.guiShops.values()) {
            for (GUIShopCategory category : guiShop.getCategories().values()) {
                for (GUIItem guiItem : category.getShopItems().values()) {
                    if (!itemManager.hasItem(guiItem.getUuid())) {
                        category.removeItem(guiItem.getUuid());
                    }
                }
            }
        }
        
        for (SignShop signShop : this.signShops.values()) {
            if (!itemManager.hasItem(signShop.getItem().getUuid())) {
                signShop.setItem(null);
            }
        }
    }
}