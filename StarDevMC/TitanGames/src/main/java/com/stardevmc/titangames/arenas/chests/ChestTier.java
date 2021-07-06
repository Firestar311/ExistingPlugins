package com.stardevmc.titangames.arenas.chests;

import com.firestar311.lib.pagination.IElement;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ChestTier implements IElement {
    
    private int number, maxItems;
    private Map<ItemStack, Integer> items;
    
    public ChestTier(int number, int maxItems, Map<ItemStack, Integer> items) {
        this.number = number;
        this.items = items;
        this.maxItems = maxItems;
    }
    
    public ChestTier(int number, int maxItems) {
        this.number = number;
        this.maxItems = maxItems;
        this.items = new HashMap<>();
    }
    
    public int getNumber() {
        return number;
    }
    
    public Map<ItemStack, Integer> getItems() {
        return new HashMap<>(items);
    }
    
    public void addItem(ItemStack item, int chance) {
        this.items.put(item, chance);
    }
    
    public int getMaxItems() {
        return maxItems;
    }
    
    public String formatLine(String... args) {
        return "&aChest tier &b" + this.number + " &ahas &b" + this.maxItems + " &amaximum items and &b" + this.items.size() + " &atotal possible items.";
    }
    
    public int getTotalChance() {
        AtomicInteger chance = new AtomicInteger(0);
        items.values().forEach(chance::addAndGet);
        return chance.get();
    }
}