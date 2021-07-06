package com.kingrealms.realms.skills.slayer;

import com.kingrealms.realms.skills.SkillType;
import com.kingrealms.realms.skills.base.Skill;
import com.kingrealms.realms.skills.base.SkillLevel;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

@SuppressWarnings("UnusedAssignment")
public final class SlayerSkill extends Skill {
    public SlayerSkill(int baseXp, double xpMultiplier) {
        super(SkillType.SLAYER, Material.DIAMOND_SWORD, baseXp, xpMultiplier);
        int xpPerKill = 5;
        int level = 1;
        addLevel(level++, EntityType.CHICKEN, xpPerKill+=5);
        addLevel(level++, EntityType.PIG, xpPerKill+=5);
        addLevel(level++, EntityType.SHEEP, xpPerKill+=5);
        addLevel(level++, EntityType.COW, xpPerKill+=5);
        addLevel(level++, EntityType.FOX, xpPerKill+=5);
        addLevel(level++, EntityType.PARROT, xpPerKill+=5);
        addLevel(level++, EntityType.ZOMBIE, xpPerKill+=5);
        addLevel(level++, EntityType.CREEPER, xpPerKill+=5);
        addLevel(level++, EntityType.RABBIT, xpPerKill+=5);
        addLevel(level++, EntityType.SKELETON, xpPerKill+=5);
        addLevel(level++, EntityType.TURTLE, xpPerKill+=5);
        addLevel(level++, EntityType.BEE, xpPerKill+=5);
        addLevel(level++, EntityType.SQUID, xpPerKill+=5);
        addLevel(level++, EntityType.DONKEY, xpPerKill+=5);
        addLevel(level++, EntityType.CAT, xpPerKill+=5);
        addLevel(level++, EntityType.MUSHROOM_COW, xpPerKill+=5);
        addLevel(level++, EntityType.SPIDER, xpPerKill+=5);
        addLevel(level++, EntityType.HUSK, xpPerKill+=5);
        addLevel(level++, EntityType.PANDA, xpPerKill+=5);
        addLevel(level++, EntityType.BLAZE, xpPerKill+=5);
        addLevel(level++, EntityType.PHANTOM, xpPerKill+=5);
        addLevel(level++, EntityType.SILVERFISH, xpPerKill+=5);
        addLevel(level++, EntityType.WITCH, xpPerKill+=5);
        addLevel(level++, EntityType.SLIME, xpPerKill+=5);
        addLevel(level++, EntityType.VEX, xpPerKill+=5);
        addLevel(level++, EntityType.STRAY, xpPerKill+=5);
        addLevel(level++, EntityType.MAGMA_CUBE, xpPerKill+=5);
        addLevel(level++, EntityType.WOLF, xpPerKill+=5);
        addLevel(level++, EntityType.POLAR_BEAR, xpPerKill+=5);
        addLevel(level++, EntityType.DOLPHIN, xpPerKill+=5);
        addLevel(level++, EntityType.LLAMA, xpPerKill+=5);
        addLevel(level++, EntityType.OCELOT, xpPerKill+=5);
        addLevel(level++, EntityType.HORSE, xpPerKill+=5);
        addLevel(level++, EntityType.ZOMBIFIED_PIGLIN, xpPerKill+=5);
        addLevel(level++, EntityType.IRON_GOLEM, xpPerKill+=5);
        addLevel(level++, EntityType.DROWNED, xpPerKill+=5);
        addLevel(level++, EntityType.GUARDIAN, xpPerKill+=5);
        addLevel(level++, EntityType.GHAST, xpPerKill+=5);
        addLevel(level++, EntityType.ENDERMITE, xpPerKill+=5);
        addLevel(level++, EntityType.ELDER_GUARDIAN, xpPerKill+=5);
        addLevel(level++, EntityType.PILLAGER, xpPerKill+=5);
        addLevel(level++, EntityType.RAVAGER, xpPerKill+=5);
        addLevel(level++, EntityType.SHULKER, xpPerKill+=5);
        addLevel(level++, EntityType.VINDICATOR, xpPerKill+=5);
        addLevel(level++, EntityType.VILLAGER, xpPerKill+=5);
        addLevel(level++, EntityType.EVOKER, xpPerKill+=5);
        addLevel(level++, EntityType.WITHER_SKELETON, xpPerKill+=5);
        addLevel(level++, EntityType.HOGLIN, xpPerKill+=5);
        addLevel(level++, EntityType.ZOGLIN, xpPerKill+=5);
        addLevel(level++, EntityType.PIGLIN, xpPerKill+=5);
        addLevel(level++, EntityType.STRIDER, xpPerKill+=5);
        addLevel(level++, EntityType.ENDERMAN, xpPerKill+=5);
    }
    
    public SlayerLevel getLevel(EntityType type) {
        for (SkillLevel level : levels.values()) {
            if (level instanceof SlayerLevel) {
                SlayerLevel slayerLevel = (SlayerLevel) level;
                if (slayerLevel.getType().equals(type)) {
                    return slayerLevel;
                }
            }
        }
        
        return null;
    }
    
    private void addLevel(int position, EntityType type, int killXp) {
        int xpToLevel = (int) Math.ceil((getBaseXpPerLevel() * position) * getXpLevelMultiplier());
        try {
            this.levels.put(position, new SlayerLevel(position, type, killXp, xpToLevel));
        } catch (Exception e) {
            this.levels.put(position, new SlayerLevel(position, type, Material.STONE, killXp, xpToLevel));
        }
    }
}