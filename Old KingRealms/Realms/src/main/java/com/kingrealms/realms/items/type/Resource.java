package com.kingrealms.realms.items.type;

import com.kingrealms.realms.items.*;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.util.MaterialNames;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class Resource extends CustomItem {
    
    private boolean damage;
    
    public Resource(Material material, boolean damage) {
        this(material);
        this.damage = damage;
    }
    
    public Resource(Material material) {
        this(material, "&e" + MaterialNames.getName(material));
    }
    
    public Resource(Material material, String name) {
        super(new ID(material.name().toLowerCase()), "&e" + name, "", material, ItemType.RESOURCE, false);
        CustomItemRegistry.RESOURCES.addItem(material, this);
        this.sellMultiplier = CustomItemRegistry.SM;
    }
    
    public Resource() {
        super();
    }
    
    @Override
    public ItemStack getItemStack() {
        return this.getItemStack(1);
    }
    
    @Override
    public ItemStack getItemStack(int amount) {
        ItemStack itemStack = super.getItemStack(amount);
        if (damage) {
            if (itemStack.getItemMeta() instanceof Damageable) {
                Damageable damageable = (Damageable) itemStack.getItemMeta();
                int max = itemStack.getType().getMaxDurability();
                int damage = new Random().nextInt(max);
                damageable.setDamage(damage);
                itemStack.setItemMeta((ItemMeta) damageable);
            }
        }
    
        return itemStack;
    }
}