package com.kingrealms.realms.questing.tasks.types;

import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.economy.EconomyResponse;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.util.Pair;

public class EntityCoinSubmitTask extends EntityInteractTask {
    
    private double amount;
    
    public EntityCoinSubmitTask(ID id, ID questId, String name, double amount) {
        super(id, questId, name);
        this.amount = amount;
    }
    
    @Override
    public String onComplete(RealmProfile profile) {
        if (profile.isTaskComplete(questId, id)) {
            return "";
        }
        Pair<EconomyResponse, EconomyResponse> responses = RealmsAPI.withdrawPlayer(profile, amount, "Task Coin Submit");
        if (responses.getValue2() == EconomyResponse.NOT_ENOUGH_FUNDS) {
            profile.sendMessage("&4&lPortal Keeper &cYou do not have enough coins for me.");
            return "";
        } else if (responses.getValue1() != EconomyResponse.SUCCESS && responses.getValue2() != EconomyResponse.SUCCESS) {
            profile.sendMessage("&cThere was an error processing your payment to the Portal Keeper");
            return "";
        }
    
        return super.onComplete(profile);
    }
    
    @Override
    public String getProgressLine(RealmProfile profile) {
        return getName();
    }
}