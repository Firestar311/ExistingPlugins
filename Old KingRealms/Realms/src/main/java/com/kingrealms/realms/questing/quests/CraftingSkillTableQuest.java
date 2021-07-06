package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.rewards.MoneyReward;
import com.kingrealms.realms.questing.tasks.types.CraftkingSkillTableTask;
import com.starmediadev.lib.util.ID;

public class CraftingSkillTableQuest extends Quest {
    public CraftingSkillTableQuest() {
        super("Craft a Crafting Skill Table", new ID("crafting_skill_table_quest"));
        addTask(new CraftkingSkillTableTask(this.id));
        setDescription("This is crafted with 4 crafting tables in the same pattern as a normal crafting table.");
        addReward(new MoneyReward("1,000 coins", 1000));
    }
    
    @Override
    public boolean onComplete(RealmProfile profile) {
        profile.sendDelayedMessage("&aYou can place scraps, shards and slivers in the table and click the green button to craft them into their full form. The button only becomes green with a valid recipe.", 1L);
        return super.onComplete(profile);
    }
}