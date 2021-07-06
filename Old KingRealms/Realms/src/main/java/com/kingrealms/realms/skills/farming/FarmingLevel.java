package com.kingrealms.realms.skills.farming;

import com.kingrealms.realms.skills.base.SkillLevel;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.util.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FarmingLevel extends SkillLevel {
    
    protected CropType type;
    
    public FarmingLevel(int level, CropType type, int xpPerHarvest, int xpToLevel) {
        super(level, xpPerHarvest, xpToLevel, type.getIconMaterial());
        this.type = type;
    }
    
    public CropType getType() {
        return type;
    }
    
    @Override
    public ItemStack getIcon() {
        Material material = type.getIconMaterial();
        
        String name = Utils.capitalizeEveryWord(type.name());
        return ItemBuilder.start(material).withName("&e" + name).withLore("", "&7+&a" + Constants.NUMBER_FORMAT.format(getXpPerHarvest()) + " Base XP", "&e" + Constants.NUMBER_FORMAT.format(getTotalXpNeeded()) + " total XP needed to unlock").buildItem();
    }
}