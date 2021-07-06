package com.kingrealms.realms.skills.base;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class SkillLevel {
    
    protected double xpPerHarvest, totalXpNeeded;
    protected Material iconMaterial;
    protected int level;
    
    public SkillLevel(int level, double xpPerHarvest, double xpToLevel, Material iconMaterial) {
        this.xpPerHarvest = xpPerHarvest;
        this.totalXpNeeded = xpToLevel;
        this.level = level;
        this.iconMaterial = iconMaterial;
    }
    
    public double getXpPerHarvest() {
        return xpPerHarvest;
    }
    
    public double getTotalXpNeeded() {
        return totalXpNeeded;
    }
    
    public int getLevel() {
        return level;
    }
    
    @Deprecated
    public Material getIconMaterial() {
        return iconMaterial;
    }
    
    public abstract ItemStack getIcon();
}