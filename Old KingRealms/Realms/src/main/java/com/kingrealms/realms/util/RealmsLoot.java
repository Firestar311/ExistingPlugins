package com.kingrealms.realms.util;

import com.kingrealms.realms.items.CustomItem;
import com.kingrealms.realms.loot.Loot;
import com.kingrealms.realms.loot.Rarity;
import org.bukkit.inventory.ItemStack;

public class RealmsLoot extends Loot {
    
    private CustomItem customItem;
    
    public RealmsLoot(CustomItem customItem, Rarity rarity) {
        super(null, rarity);
        this.customItem = customItem;
    }
    
    @Override
    public ItemStack getItemStack() {
        return customItem.getItemStack();
    }
}