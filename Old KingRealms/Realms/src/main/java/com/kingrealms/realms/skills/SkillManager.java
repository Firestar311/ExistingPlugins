package com.kingrealms.realms.skills;

import com.kingrealms.realms.skills.base.Skill;
import com.kingrealms.realms.skills.farming.FarmingSkill;
import com.kingrealms.realms.skills.woodcutting.WoodcuttingSkill;
import com.kingrealms.realms.skills.mining.MiningSkill;
import com.kingrealms.realms.skills.slayer.SlayerSkill;

import java.util.*;

public class SkillManager {
    
    private Map<SkillType, Skill> skills = new TreeMap<>();
    
    private FarmingSkill farmingSkill = new FarmingSkill(75, 25.6);
    private MiningSkill miningSkill = new MiningSkill(75, 25.6);
    private SlayerSkill slayerSkill = new SlayerSkill(75, 25.6);
    private WoodcuttingSkill woodcuttingSkill = new WoodcuttingSkill(75, 25.6);
    
    public SkillManager() {
        this.skills.put(farmingSkill.getType(), farmingSkill);
        this.skills.put(miningSkill.getType(), miningSkill);
        this.skills.put(slayerSkill.getType(), slayerSkill);
        this.skills.put(woodcuttingSkill.getType(), woodcuttingSkill);
    }
    
    public FarmingSkill getFarmingSkill() {
        return farmingSkill;
    }
    
    public MiningSkill getMiningSkill() {
        return miningSkill;
    }
    
    public SlayerSkill getSlayerSkill() {
        return slayerSkill;
    }
    
    public WoodcuttingSkill getWoodcuttingSkill() {
        return woodcuttingSkill;
    }
    
    public Skill getSkill(SkillType type) {
        return skills.get(type);
    }
    
    public Collection<Skill> getSkills() {
        return skills.values();
    }
}