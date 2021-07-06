package com.kingrealms.realms.skills.base;

import com.kingrealms.realms.skills.SkillType;
import org.bukkit.Material;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Skill {
    
    protected int baseXpPerLevel;
    protected double xpLevelMultiplier;
    protected Map<Integer, SkillLevel> levels = new TreeMap<>();
    protected Material iconMaterial;
    protected SkillType type;
    
    public Skill(SkillType type, Material iconMaterial, int baseXpPerLevel, double xpLevelMultiplier) {
        this.type = type;
        this.iconMaterial = iconMaterial;
        this.baseXpPerLevel = baseXpPerLevel;
        this.xpLevelMultiplier = xpLevelMultiplier;
    }
    
    public SkillType getType() {
        return type;
    }
    
    public int getCurrentLevel(double totalXp) {
        AtomicInteger highestLevel = new AtomicInteger();
        levels.forEach((pos, level) -> {
            if (totalXp > level.getTotalXpNeeded()) {
                highestLevel.set(pos);
            }
        });
        return highestLevel.get();
    }
    
    public int getBaseXpPerLevel() {
        return baseXpPerLevel;
    }
    
    public double getXpLevelMultiplier() {
        return xpLevelMultiplier;
    }
    
    public Map<Integer, SkillLevel> getLevels() {
        return levels;
    }
    
    public Material getIconMaterial() {
        return iconMaterial;
    }
}