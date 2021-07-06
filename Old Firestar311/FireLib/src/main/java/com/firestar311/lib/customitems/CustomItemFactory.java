package com.firestar311.lib.customitems;

import com.firestar311.lib.customitems.api.ICategory;
import com.firestar311.lib.customitems.api.ICustomItem;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public final class CustomItemFactory {
    private CustomItemFactory() {}
    
    public static ICategory createCategory(Plugin plugin, String name, ItemStack icon, String permission) {
        return new Category(plugin, name, icon, permission);
    }
    
    public static ICustomItem createCustomItem(Plugin plugin, String name, ItemStack itemStack, String permission) {
        return new CustomItem(plugin, name, itemStack, permission);
    }
    
    public static String formatName(String name) {
        return ChatColor.stripColor(name).replace(" ", "_").toLowerCase();
    }
}