package com.stardevmc.shop.objects.shops.gui;

import com.stardevmc.shop.gui.CategoryGUI;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GUIShopCategory {
    
    private String name;
    private ItemStack icon;
    private int place = -1;
    private GUIShop parent;
    
    private SortedMap<Integer, GUIItem> shopItems = new TreeMap<>();
    
    public GUIShopCategory(GUIShop parent, String name, ItemStack icon, int place) {
        this(parent, name, icon);
        this.place = place;
    }
    
    public GUIShopCategory(GUIShop parent, String name, ItemStack icon) {
        this.parent = parent;
        this.name = name;
        this.icon = icon;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void addShopItem(GUIItem item) {
        if (item.getPlace() == -1) {
            int last = shopItems.isEmpty() ? 0 : shopItems.lastKey() + 1;
            this.shopItems.put(last, item);
            item.setPlace(last);
        } else {
            this.shopItems.put(item.getPlace(), item);
        }
        item.setParent(this);
    }
    
    public SortedMap<Integer, GUIItem> getShopItems() {
        return new TreeMap<>(shopItems);
    }
    
    public ItemStack getIcon() {
        return icon;
    }
    
    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }
    
    public int getPlace() {
        return place;
    }
    
    public void setPlace(int place) {
        this.place = place;
    }
    
    public CategoryGUI getGUI() {
        return new CategoryGUI(this);
    }
    
    public GUIShop getParent() {
        return parent;
    }
    
    public void setParent(GUIShop parent) {
        this.parent = parent;
    }
    
    public void clearItems() {
        this.shopItems.clear();
    }
    
    public void removeItem(UUID item) {
        this.shopItems.values().removeIf(shopItem -> shopItem.getUuid().equals(item));
    }
}