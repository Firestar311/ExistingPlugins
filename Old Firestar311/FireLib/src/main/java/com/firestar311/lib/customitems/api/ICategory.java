package com.firestar311.lib.customitems.api;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface ICategory {
    
    /**
     * The name of the category
     * @return The name of the category with no formatting and spaces represented by "_"'s
     */
    String getName();
    
    /**
     * @return The icon used in the GUI for this category
     */
    ItemStack getIcon();
    
    /**
     * @return A copy of the Item Map
     */
    Map<String, ICustomItem> getItems();
    
    /**
     * Adds an item to the category
     * @param item The Item to add
     */
    void addItem(ICustomItem item);
    
    /**
     * Removes an item from a category
     * @param item The Item to remove
     */
    void removeItem(ICustomItem item);
    
    /**
     * Gets a custom item from this category
     * @param name The name of the item, method removes formatting automatically
     * @return The custom item
     */
    ICustomItem getItem(String name);
    
    String getPermission();
}