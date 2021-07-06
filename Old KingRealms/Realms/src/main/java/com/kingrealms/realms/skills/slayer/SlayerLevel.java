package com.kingrealms.realms.skills.slayer;

import com.kingrealms.realms.skills.base.SkillLevel;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.util.Constants;
import com.starmediadev.lib.util.EntityNames;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class SlayerLevel extends SkillLevel {
    
    protected EntityType type;
    
    public SlayerLevel(int level, EntityType type, int xpPerKill, int xpToLevel) {
        super(level, xpPerKill, xpToLevel, (type == EntityType.MUSHROOM_COW) ? Material.MOOSHROOM_SPAWN_EGG : Material.valueOf(type.name() + "_SPAWN_EGG"));
        this.type = type;
    }
    
    public SlayerLevel(int level, EntityType entityType, Material icon, int xpPerKill, int xpToLevel) {
        super(level, xpPerKill, xpToLevel, icon);
        this.type = entityType;
    }
    
    public EntityType getType() {
        return type;
    }
    
    @Override
    public ItemStack getIcon() {
        Material material = switch(type) {
            case MUSHROOM_COW -> Material.MOOSHROOM_SPAWN_EGG;
            case IRON_GOLEM -> Material.IRON_INGOT;
            default -> Material.valueOf(type.name() + "_SPAWN_EGG");
        };
        
        String name = EntityNames.getName(type);
        return ItemBuilder.start(material).withName("&e" + name).withLore("", "&7+&a" + Constants.NUMBER_FORMAT.format(getXpPerHarvest()) + " Base XP", "&e" + Constants.NUMBER_FORMAT.format(getTotalXpNeeded()) + " total XP needed to unlock").buildItem();
    }
}