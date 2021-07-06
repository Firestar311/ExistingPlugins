package com.stardevmc.shop.worth;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StackWorth {
    private ItemStack itemStack;
    private double totalWorth;
    private List<WorthIngredient> ingredients;
    
    public StackWorth(ItemStack itemStack, double totalWorth, List<WorthIngredient> worthIngredients) {
        this.itemStack = itemStack;
        this.totalWorth = totalWorth;
        this.ingredients = worthIngredients;
    }
    
    public ItemStack getItemStack() {
        return itemStack;
    }
    
    public double getTotalWorth() {
        return totalWorth;
    }
    
    public void addIngredient(WorthIngredient ingredient) {
        this.ingredients.add(ingredient);
    }
    
    public List<WorthIngredient> getIngredients() {
        return new ArrayList<>(ingredients);
    }
}