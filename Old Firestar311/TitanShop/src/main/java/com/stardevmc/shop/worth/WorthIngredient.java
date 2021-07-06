package com.stardevmc.shop.worth;

import org.bukkit.Material;

public class WorthIngredient {
    
    private Material material;
    private int amount;
    private double price;
    
    public WorthIngredient(Material material, int amount, double price) {
        this.material = material;
        this.amount = amount;
        this.price = price;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public double getPrice() {
        return price;
    }
}