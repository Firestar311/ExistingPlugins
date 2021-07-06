package com.kingrealms.realms.loot;

import com.starmediadev.lib.items.NBTWrapper;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class Loot {
    
    private int id;
    private ItemStack itemStack;
    private Rarity rarity;
    
    public Loot(ItemStack itemStack, Rarity rarity) {
        this.itemStack = itemStack;
        this.rarity = rarity;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public ItemStack getItemStack() {
        try {
            return NBTWrapper.cloneItemStack(itemStack);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
    
    public Rarity getRarity() {
        return rarity;
    }
    
    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }
}