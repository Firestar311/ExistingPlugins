package com.firestar311.lib.customitems.api;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public interface ICustomItem {
    
    String getName();
    ItemStack getItemStack();
    Plugin getPlugin();
    String getPermission();
}