package com.kingrealms.realms.crafting;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CraftResult {
    private ItemStack itemStack;
    private List<ItemStack> leftOvers;
    private boolean success;
    
    public CraftResult(ItemStack itemStack, List<ItemStack> leftOvers) {
        this.itemStack = itemStack;
        this.leftOvers = leftOvers;
        success = true;
    }
    
    public CraftResult(boolean success) {
        this.success = success;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public ItemStack getItem() {
        return itemStack;
    }
    
    public List<ItemStack> getLeftOvers() {
        return leftOvers;
    }
    
    public CraftResult setLeftOvers(List<ItemStack> items) {
        this.leftOvers = items;
        return this;
    }
}