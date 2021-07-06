package com.stardevmc.shop.objects;

import com.firestar311.lib.items.NBTWrapper;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ShopItem {
    private UUID uuid, creator;
    private ItemStack itemStack;
    private Price prices;
    private String displayName;
    
    public ShopItem(UUID uuid, UUID creator, ItemStack itemStack, String displayName, double buy, double sell) {
        this.uuid = uuid;
        this.creator = creator;
        this.itemStack = itemStack;
        this.displayName = displayName;
        this.prices = new Price(buy, sell);
    }
    
    public ShopItem(UUID creator, ItemStack itemStack, String displayName, double buy, double sell) {
        this.creator = creator;
        this.itemStack = itemStack;
        this.displayName = displayName;
        this.prices = new Price(buy, sell);
    }
    
    public ItemStack getItemStack() {
        return itemStack;
    }
    
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
    
    public Price getPrices() {
        return prices;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public boolean setUuid(UUID uuid) {
        try {
            NBTWrapper.addNBTString(itemStack, "shopuuid", uuid.toString());
            this.uuid = uuid;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public UUID getCreator() {
        return creator;
    }
    
    public void setCreator(UUID creator) {
        this.creator = creator;
    }
}