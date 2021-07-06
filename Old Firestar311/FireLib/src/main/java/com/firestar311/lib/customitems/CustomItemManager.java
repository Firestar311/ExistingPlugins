package com.firestar311.lib.customitems;

import com.firestar311.lib.FireLib;
import com.firestar311.lib.customitems.api.*;
import com.firestar311.lib.items.NBTWrapper;
import com.firestar311.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

final class CustomItemManager implements IItemManager {
    private final Map<String, ICategory> categories = new LinkedHashMap<>();
    
    private ItemStack backItem;
    
    CustomItemManager(FireLib plugin) {
        backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName(Utils.color("&c&l<-- BACK"));
        backMeta.setLore(new ArrayList<>(Collections.singleton(Utils.color("&7Go back to the main menu"))));
        backItem.setItemMeta(backMeta);
        try {
            backItem = NBTWrapper.addNBTString(backItem, "cin", "back");
        } catch (Exception e) {}
    
        ItemStack dc = new ItemStack(Material.COBBLESTONE);
        ItemMeta dm = dc.getItemMeta();
        dm.setDisplayName(Utils.color("&7Default Category"));
        dc.setItemMeta(dm);
        this.categories.put("default", CustomItemFactory.createCategory(plugin, "default", dc, ""));
    }
    
    public void addCustomItem(String cat, ICustomItem item) {
        ICategory category = getCategory(cat);
        if (category == null) {
            category = getCategory("default");
        }
        category.addItem(item);
    }
    
    public ICustomItem getCustomItem(String name) {
        String n = CustomItemFactory.formatName(name);
        
        for (ICategory category : this.categories.values()) {
            if (category.getItem(n) != null) {
                return category.getItem(n);
            }
        }
        
        return null;
    }
    
    public ICategory getItemCategory(ICustomItem item) {
        for (ICategory category : this.categories.values()) {
            if (category.getItem(item.getName()) != null) {
                return category;
            }
        }
        return null;
    }
    
    public String extractName(ItemStack itemStack) {
        String cin = null, catn = null;
        try {
            cin = NBTWrapper.getNBTString(itemStack, "cin");
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        try {
            catn = NBTWrapper.getNBTString(itemStack, "catn");
        } catch (Exception e) { }
    
        if (cin != null && !cin.equals("")) {
            return cin;
        }
    
        return catn;
    }
    
    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "Custom Items");
        categories.values().forEach(category -> {
            try {
                gui.addItem(category.getIcon());
            } catch (Exception e) {
                System.out.println("Error while adding category: " + category.getName());
            }
        });
        player.openInventory(gui);
    }
    
    public void openCategory(Player player, ICategory category) {
        openCategory(player, category, "Custom Items: " + category.getName());
    }
    
    public void openCategory(Player player, ICategory category, String title) {
        Inventory gui = Bukkit.createInventory(null, 54, Utils.color(title));
        gui.setItem(49, backItem);
        category.getItems().values().forEach(item -> gui.addItem(item.getItemStack()));
        player.openInventory(gui);
    }
    
    public ICategory getCategory(String category) {
        return categories.get(category);
    }
    
    public void addCategory(ICategory category) {
        if (category.getName().equalsIgnoreCase("default")) return;
        this.categories.put(category.getName(), category);
    }
    
    public void removeCategory(ICategory category) {
        if (category.getName().equalsIgnoreCase("default")) return;
        this.categories.remove(category.getName());
    }
    
    public ItemStack getBackItem() {
        return this.backItem;
    }
}