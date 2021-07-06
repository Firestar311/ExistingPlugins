package com.stardevmc.shop.manager;

import com.firestar311.lib.config.ConfigManager;
import com.stardevmc.shop.TitanShop;
import com.stardevmc.shop.gui.ItemGUI;
import com.stardevmc.shop.objects.ShopItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemManager {
    
    private Map<UUID, ShopItem> shopItems = new HashMap<>();
    
    private ConfigManager configManager;
    
    public ItemManager(TitanShop plugin) {
        this.configManager = new ConfigManager(plugin, "items");
        this.configManager.setup();
    }
    
    //FIXME Preserve original lore of the item
    
    public void addItem(ShopItem item) {
        if (item.getUuid() == null) {
            UUID uuid;
            do {
                uuid = UUID.randomUUID();
            } while (shopItems.containsKey(uuid));
            item.setUuid(uuid);
        }
        
        this.shopItems.put(item.getUuid(), item);
    }
    
    public ShopItem getItem(UUID uuid) {
        if (uuid == null) return null;
        return this.shopItems.get(uuid);
    }
    
    public void loadItems() {
        FileConfiguration config = configManager.getConfig();
        if (!config.contains("items")) return;
        ConfigurationSection itemSection = config.getConfigurationSection("items");
        if (itemSection == null) return;
        for (String u : itemSection.getKeys(false)) {
            UUID uuid = UUID.fromString(u);
            String name = itemSection.getString(u + ".name");
            UUID creator = UUID.fromString(itemSection.getString(u + ".creator"));
            ItemStack stack = itemSection.getItemStack(u + ".stack");
            double buyPrice = itemSection.getDouble(u + ".prices.buy");
            double sellPrice = itemSection.getDouble(u + ".price.sell");
            ShopItem shopItem = new ShopItem(uuid, creator, stack, name, buyPrice, sellPrice);
            this.shopItems.put(uuid, shopItem);
        }
    }
    
    public void saveItems() {
        FileConfiguration config = configManager.getConfig();
        for (ShopItem shopItem : shopItems.values()) {
            String uuid = shopItem.getUuid().toString();
            config.set("items." + uuid + ".name", shopItem.getDisplayName());
            config.set("items." + uuid + ".creator", shopItem.getCreator().toString());
            config.set("items." + uuid + ".stack", shopItem.getItemStack());
            config.set("items." + uuid + ".prices.buy", shopItem.getPrices().buy());
            config.set("items." + uuid + ".prices.sell", shopItem.getPrices().sell());
        }
        configManager.saveConfig();
    }
    
    public List<ShopItem> getItems() {
        return new ArrayList<>(shopItems.values());
    }
    
    public void clearItems() {
        this.shopItems.clear();
    }
    
    public ItemGUI getGUI() {
        return new ItemGUI();
    }
    
    public boolean hasItem(UUID uuid) {
        return this.shopItems.containsKey(uuid);
    }
}