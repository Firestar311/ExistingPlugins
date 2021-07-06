package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.rewards.MoneyReward;
import com.kingrealms.realms.questing.tasks.types.*;
import com.starmediadev.lib.util.ID;

public class GettingStartedQuest extends Quest {
    
    private ID claimStarter = new ID("claim_starter_task"), createHamlet = new ID("hamlet_create_task"), joinHamlet = new ID("hamlet_join_task");
    
    public GettingStartedQuest() {
        super("Getting Started", new ID("getting_started"));
        setDescription("Welcome to KingRealms! Start your journey with these starting tasks.");
        addTask(new KitTask(claimStarter, this.id, "Claim Starter Kit", "Use /kit starter", "starter"));
        addTask(new HamletCreateTask(createHamlet, this.id));
        addTask(new HamletJoinTask(joinHamlet, this.id));
        addReward(new MoneyReward("250 Coins", 250));
    }
    
    @Override
    public boolean checkComplete(RealmProfile profile) {
        boolean createComplete = profile.isTaskComplete(this.id, createHamlet);
        boolean joinComplete = profile.isTaskComplete(this.id, joinHamlet);
        boolean claimComplete = profile.isTaskComplete(this.id, claimStarter);
        return (createComplete || joinComplete) && claimComplete;
    }
}