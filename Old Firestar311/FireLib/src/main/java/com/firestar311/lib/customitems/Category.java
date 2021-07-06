package com.firestar311.lib.customitems;

import com.firestar311.lib.customitems.api.ICategory;
import com.firestar311.lib.customitems.api.ICustomItem;
import com.firestar311.lib.items.NBTWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

final class Category implements ICategory {
    
    private Plugin plugin;
    private String name;
    private ItemStack icon;
    private String permission;
    
    private Map<String, ICustomItem> items = new LinkedHashMap<>();
    
    Category(Plugin plugin, String name, ItemStack icon, String permission) {
        this.plugin = plugin;
        this.name = CustomItemFactory.formatName(name);
        this.permission = permission;
        
        try {
            this.icon = NBTWrapper.addNBTString(icon, "catn", this.name);
        } catch (Exception e) {}
    }
    
    public Plugin getPlugin() {
        return plugin;
    }
    
    public String getName() {
        return name;
    }
    
    public ItemStack getIcon() {
        return icon;
    }
    
    public Map<String, ICustomItem> getItems() {
        return new HashMap<>(items);
    }
    
    public void addItem(ICustomItem item) {
        this.items.put(item.getName(), item);
    }
    
    public void removeItem(ICustomItem item) {
        this.items.remove(item.getName());
    }
    
    public ICustomItem getItem(String name) {
        String itemName = CustomItemFactory.formatName(name);
        return this.items.get(itemName);
    }
    
    public String getPermission() {
        return permission;
    }
}