package com.stardevmc.titanterritories.core.objects.kingdom;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Shop implements ConfigurationSerializable {
    
    private UUID owner;
    private String name;
    private Set<Location> stockChests = new HashSet<>();
    private Set<Location> signs = new HashSet<>();
    private double price;
    private ItemStack item;
    
    public Shop(UUID owner, String name, double price, ItemStack item) {
        this.owner = owner;
        this.name = name;
        this.price = price;
        this.item = item;
    }
    
    private Shop(UUID owner, String name, Set<Location> stockChests, Set<Location> signs, double price, ItemStack item) {
        this.owner = owner;
        this.name = name;
        this.stockChests = stockChests;
        this.signs = signs;
        this.price = price;
        this.item = item;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("owner", this.owner.toString());
        serialized.put("name", name);
        serialized.put("price", this.price + "");
        serialized.put("item", this.item);
        if (!stockChests.isEmpty()) {
            serialized.put("chestAmount", this.stockChests.size());
            int counter = 0;
            for (Location location : this.stockChests) {
                serialized.put("chest" + counter, location);
                counter++;
            }
        }
        
        if (!signs.isEmpty()) {
            serialized.put("signAmount", this.signs.size());
            int counter = 0;
            for (Location location : this.signs) {
                serialized.put("sign" + counter, location);
                counter++;
            }
        }
        return serialized;
    }
    
    public static Shop deserialize(Map<String, Object> serialized) {
        UUID owner = UUID.fromString((String) serialized.get("owner"));
        String name = (String) serialized.get("name");
        double price = Double.parseDouble((String) serialized.get("price"));
        ItemStack item = (ItemStack) serialized.get("item");
        Set<Location> chests = new HashSet<>();
        if (serialized.containsKey("chestAmount")) {
            int amount = Integer.parseInt((String) serialized.get("chestAmount"));
            for (int i = 0; i < amount; i++) {
                chests.add((Location) serialized.get("chest" + i));
            }
        }
    
        Set<Location> signs = new HashSet<>();
        if (serialized.containsKey("signAmount")) {
            int amount = Integer.parseInt((String) serialized.get("signAmount"));
            for (int i = 0; i < amount; i++) {
                signs.add((Location) serialized.get("sign" + i));
            }
        }
        return new Shop(owner, name, chests, signs, price, item);
    }
    
    public UUID getOwner() {
        return owner;
    }
    
    public Set<Location> getStockChests() {
        return stockChests;
    }
    
    public Set<Location> getSigns() {
        return signs;
    }
    
    public double getPrice() {
        return price;
    }
    
    public ItemStack getItem() {
        return item;
    }
    
    public String getName() {
        return name;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public void setItem(ItemStack item) {
        this.item = item;
    }
    
    public void addStockChest(Location location) {
        this.stockChests.add(location);
    }
    
    public void removeStockChest(Location location) {
        this.stockChests.remove(location);
    }
    
    public void addSign(Location location) {
        this.signs.add(location);
    }
    
    public void removeSign(Location location) {
        this.signs.remove(location);
    }
    
    public void setName(String name) {
        this.name = name;
    }
}