package com.kingrealms.realms.skills.farming;

import com.kingrealms.realms.skills.SkillType;
import com.kingrealms.realms.skills.base.Skill;
import com.kingrealms.realms.skills.base.SkillLevel;
import org.bukkit.Material;

public final class FarmingSkill extends Skill {
    
    public FarmingSkill(int baseXp, double xpMultiplier) {
        super(SkillType.FARMING, Material.DIAMOND_HOE, baseXp, xpMultiplier);
        addLevel(1, CropType.WHEAT, 5);
        addLevel(2, CropType.CARROT, 10);
        addLevel(3, CropType.POTATO, 15);
        addLevel(4, CropType.CACTUS, 20);
        addLevel(5, CropType.SUGAR_CANE, 25);
        addLevel(6, CropType.BEETROOT, 30);
        addLevel(7, CropType.PUMPKIN, 35);
        addLevel(8, CropType.MELON, 40);
        addLevel(9, CropType.KELP, 45);
        addLevel(10, CropType.BAMBOO, 50);
        addLevel(11, CropType.COCO_BEANS, 55);
        addLevel(12, CropType.RED_MUSHROOM, 60);
        addLevel(13, CropType.BROWN_MUSHROOM, 65);
        addLevel(14, CropType.NETHER_WART, 70);
        addLevel(15, CropType.CHORUS, 75);
    }
    
    private void addLevel(int position, CropType type, int harvestXp) {
        this.levels.put(position, new FarmingLevel(position, type, harvestXp, (int) Math.ceil((getBaseXpPerLevel() * position) * getXpLevelMultiplier())));
    }
    
    public FarmingLevel getLevel(CropType type) {
        for (SkillLevel skillLevel : getLevels().values()) {
            if (skillLevel instanceof FarmingLevel) {
                FarmingLevel level = (FarmingLevel) skillLevel;
                if (level.getType() == type) {
                    return level;
                }
            }
        }
        return null;
    }
}