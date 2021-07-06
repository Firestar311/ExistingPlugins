package com.kingrealms.realms.items.category;

import com.kingrealms.realms.items.CustomItem;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemCategory<T, I extends CustomItem> {
    
    protected String name, description;
    protected Map<T, I> registry = new LinkedHashMap<>();
    protected Material icon;
    
    public ItemCategory(String name, Material icon, String description) {
        this.name = name;
        this.icon = icon;
        this.description = description;
    }
    
    public void addItem(T key, I customItem) {
        this.registry.put(key, customItem);
    }
    
    public I getItem(T key) {
        return registry.get(key);
    }
    
    public Collection<I> getItems() {
        return registry.values();
    }
    
    public Map<T, I> getRegistry() {
        return registry;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ItemStack getIcon() {
        List<String> lore = Utils.wrapLore(50, this.description);
        lore.add(" ");
        lore.add("&bTotal Items: &f" + registry.size());
        return ItemBuilder.start(icon).withName("&e" + name).withLore(lore).withItemFlags(ItemFlag.HIDE_ATTRIBUTES).buildItem();
    }
}