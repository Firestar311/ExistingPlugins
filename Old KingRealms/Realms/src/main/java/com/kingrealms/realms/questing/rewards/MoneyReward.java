package com.kingrealms.realms.questing.rewards;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.ID;

public class MoneyReward extends Reward {
    
    private double amount;
    
    public MoneyReward(String name, double amount) {
        super(new ID("money"), name);
        this.amount = amount;
    }
    
    @Override
    public void applyReward(RealmProfile profile) {
        Realms.getInstance().getEconomyManager().getTransactionHandler().deposit(amount, profile.getAccount(), "Quest Reward");
    }
}