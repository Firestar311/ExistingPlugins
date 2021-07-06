package com.firestar311.lib.customitems.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IItemManager {

    void addCustomItem(String category, ICustomItem item);
    
    ICustomItem getCustomItem(String name);
    String extractName(ItemStack itemStack);
    void openGUI(Player player);
    void openCategory(Player player, ICategory category);
    void openCategory(Player player, ICategory category, String title);
    
    ICategory getCategory(String category);
    void addCategory(ICategory category);
    void removeCategory(ICategory category);
    
    ICategory getItemCategory(ICustomItem item);
    ItemStack getBackItem();
}