package com.kingrealms.realms.skills.mining;

import com.kingrealms.realms.skills.SkillType;
import com.kingrealms.realms.skills.base.Skill;
import com.kingrealms.realms.skills.base.SkillLevel;
import org.bukkit.Material;

public final class MiningSkill extends Skill {
    
    public MiningSkill(int baseXp, double xpMultiplier) {
        super(SkillType.MINING, Material.DIAMOND_PICKAXE, baseXp, xpMultiplier);
        addLevel(1, Material.COBBLESTONE, 1).addMaterial(Material.BLACKSTONE).addMaterial(Material.STONE).setName("Stone");
        addLevel(2, Material.COAL_ORE, 2);
        addLevel(3, Material.IRON_ORE, 4);
        addLevel(4, Material.REDSTONE_ORE, 6);
        addLevel(5, Material.LAPIS_ORE, 8);
        addLevel(6, Material.GOLD_ORE,10);
        addLevel(7, Material.DIAMOND_ORE, 12);
        addLevel(8, Material.EMERALD_ORE, 14);
        addLevel(9, Material.OBSIDIAN, 16);
        addLevel(10, Material.NETHER_QUARTZ_ORE, 18);
        addLevel(11, Material.END_STONE, 20);
        this.levels.put(12, new MiningLevel(12, Material.ANCIENT_DEBRIS, 22, (int) Math.ceil(getBaseXpPerLevel() * 12 * getXpLevelMultiplier() * xpLevelMultiplier)));
    }
    
    public MiningLevel getLevel(Material type) {
        for (SkillLevel level : levels.values()) {
            if (level instanceof MiningLevel) {
                MiningLevel miningLevel = (MiningLevel) level;
                if (miningLevel.getMaterial().equals(type)) {
                    return miningLevel;
                } else if (miningLevel.getMaterials().contains(type)) {
                    return miningLevel;
                }
            }
        }
        
        return null;
    }
    
    private MiningLevel addLevel(int position, Material type, int blockXp) {
        MiningLevel miningLevel = new MiningLevel(position, type, blockXp, (int) Math.ceil((getBaseXpPerLevel() * position) * getXpLevelMultiplier()));
        this.levels.put(position, miningLevel);
        return miningLevel;
    }
}