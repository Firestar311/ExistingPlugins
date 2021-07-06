package com.kingrealms.realms.items.type;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;

import static org.bukkit.potion.PotionType.MUNDANE;

public class Potion extends Resource {
    
    private PotionEffectType type;
    private int amplifier, duration;
    
    public Potion(PotionEffectType type, int i, int duration) {
        super(Material.POTION);
        this.type = type;
        this.amplifier = i;
        this.duration = duration;
    }
    
    @Override
    public ItemStack getItemStack() {
        return this.getItemStack(1);
    }
    
    @Override
    public ItemStack getItemStack(int amount) {
        ItemStack itemStack = super.getItemStack(amount);
        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
        PotionType potionType = null;
        for (PotionType pt : PotionType.values()) {
            if (pt.getEffectType().equals(this.type)) {
                potionType = pt;
                break;
            }
        }
    
        if (potionType == null) potionType = MUNDANE;
        potionMeta.setBasePotionData(new PotionData(potionType));
        potionMeta.addCustomEffect(new PotionEffect(this.type, duration, amplifier), true);
        potionMeta.setDisplayName("&ePotion of " + type.getName());
        itemStack.setItemMeta(potionMeta);
        return itemStack;
    }
}