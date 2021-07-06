package com.kingrealms.realms.economy.shop;

import com.kingrealms.realms.economy.EconomyResponse;
import com.starmediadev.lib.util.Pair;
import org.bukkit.inventory.ItemStack;

public class ShopResponse {
    private final boolean success;
    private final Pair<EconomyResponse, EconomyResponse> responses;
    private final double price;
    private final int amount;
    private ItemStack itemStack;
    
    public ShopResponse(boolean success, Pair<EconomyResponse, EconomyResponse> responses, double price, int amount) {
        this.success = success;
        this.responses = responses;
        this.price = price;
        this.amount = amount;
    }
    
    public ShopResponse(boolean success, Pair<EconomyResponse, EconomyResponse> responses, double price, int amount, ItemStack itemStack) {
        this.success = success;
        this.responses = responses;
        this.price = price;
        this.amount = amount;
        this.itemStack = itemStack;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public Pair<EconomyResponse, EconomyResponse> getResponses() {
        return responses;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public double getPrice() {
        return price;
    }
    
    public ItemStack getItemStack() {
        return itemStack;
    }
}