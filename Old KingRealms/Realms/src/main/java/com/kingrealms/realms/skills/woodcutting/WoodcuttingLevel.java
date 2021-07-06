package com.kingrealms.realms.skills.woodcutting;

import com.kingrealms.realms.skills.base.SkillLevel;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.util.Constants;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class WoodcuttingLevel extends SkillLevel {
    public WoodcuttingLevel(int level, double xpPerHarvest, double xpToLevel, Material iconMaterial) {
        super(level, xpPerHarvest, xpToLevel, iconMaterial);
    }
    
    @Override
    public ItemStack getIcon() {
        String name = Utils.capitalizeEveryWord(iconMaterial.name().replace("_LOG", ""));
        return ItemBuilder.start(iconMaterial).withName("&e" + name).withLore("", "&7+&a" + Constants.NUMBER_FORMAT.format(getXpPerHarvest()) + " Base XP", "&e" + Constants.NUMBER_FORMAT.format(getTotalXpNeeded()) + " total XP needed to unlock").buildItem();
    }
}