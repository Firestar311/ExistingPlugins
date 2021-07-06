package com.kingrealms.realms.skills.woodcutting;

import com.kingrealms.realms.skills.SkillType;
import com.kingrealms.realms.skills.base.Skill;
import org.bukkit.Material;

public class WoodcuttingSkill extends Skill {
    public WoodcuttingSkill(int baseXpPerLevel, double xpLevelMultiplier) {
        super(SkillType.WOODCUTTING, Material.DIAMOND_AXE, baseXpPerLevel, xpLevelMultiplier);
        addLevel(1, Material.OAK_LOG, 5);
        addLevel(2, Material.BIRCH_LOG, 10);
        addLevel(3, Material.SPRUCE_LOG, 15);
        addLevel(4, Material.JUNGLE_LOG, 20);
        addLevel(5, Material.ACACIA_LOG, 25);
        addLevel(6, Material.DARK_OAK_LOG, 30);
    }
    
    private void addLevel(int position, Material icon, int blockXp) {
        this.levels.put(position, new WoodcuttingLevel(position, blockXp, (int) Math.ceil((getBaseXpPerLevel() * position) * getXpLevelMultiplier()), icon));
    }
}