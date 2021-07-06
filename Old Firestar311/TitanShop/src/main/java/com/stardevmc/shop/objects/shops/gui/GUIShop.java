package com.stardevmc.shop.objects.shops.gui;

import com.stardevmc.shop.gui.ShopGUI;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.SortedMap;
import java.util.TreeMap;

public class GUIShop {
    
    private String name;
    private ItemStack icon;
    private SortedMap<Integer, GUIShopCategory> categories = new TreeMap<>();
    
    public GUIShop(String name, ItemStack icon) {
        this.name = name;
        this.icon = icon;
    }
    
    public void addCategory(GUIShopCategory category) {
        if (category.getPlace() == -1) {
            int last = categories.isEmpty() ? 0 : categories.lastKey() + 1;
            this.categories.put(last, category);
            category.setPlace(last);
        } else {
            this.categories.put(category.getPlace(), category);
        }
        category.setParent(this);
    }
    
    public String getName() {
        return name;
    }
    
    public ItemStack getIcon() {
        return icon;
    }
    
    public SortedMap<Integer, GUIShopCategory> getCategories() {
        return new TreeMap<>(categories);
    }
    
    public ShopGUI getGUI() {
        return new ShopGUI(this);
    }
    
    public GUIShopCategory getCategory(int place) {
        return this.categories.get(place);
    }
    
    public GUIShopCategory getCategory(String name) {
        for (GUIShopCategory category : categories.values()) {
            if (ChatColor.stripColor(category.getName()).equalsIgnoreCase(ChatColor.stripColor(name))) {
                return category;
            }
        }
        
        return null;
    }
}