package com.firestar311.lib.customitems;

import com.firestar311.lib.customitems.api.ICustomItem;
import com.firestar311.lib.items.NBTWrapper;
import com.firestar311.lib.util.Utils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

final class CustomItem implements ICustomItem {
    
    private Plugin plugin;
    private String name;
    private ItemStack itemStack;
    private String permission;
    
    CustomItem(Plugin plugin, String name, ItemStack itemStack, String permission) {
        this.plugin = plugin;
        this.permission = permission;
    
        this.name = CustomItemFactory.formatName(name);
        ItemMeta meta = itemStack.getItemMeta();
    
        if (meta.getLore() != null) {
            List<String> lore = meta.getLore();
            lore.addAll(Arrays.asList("", "", Utils.color("&dAdded by &e" + plugin.getName())));
            meta.setLore(lore);
        } else {
            List<String> lore = new ArrayList<>(Arrays.asList("", "", Utils.color("&dAdded by &e" + plugin.getName())));
            meta.setLore(lore);
        }
        itemStack.setItemMeta(meta);
    
        try {
            this.itemStack = NBTWrapper.addNBTString(itemStack, "cin", this.name);
        } catch (Exception e) {}
    }
    
    public String getName() {
        return name;
    }
    
    public ItemStack getItemStack() {
        return itemStack;
    }
    
    public Plugin getPlugin() {
        return plugin;
    }
    
    public String getPermission() {
        return permission;
    }
}