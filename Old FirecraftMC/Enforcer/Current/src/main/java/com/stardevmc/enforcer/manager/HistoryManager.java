package com.stardevmc.enforcer.manager;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.modules.base.Manager;
import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.history.*;
import com.stardevmc.enforcer.objects.target.Target;

import java.util.HashSet;
import java.util.Set;

public class HistoryManager extends Manager {
    
    private Set<IHistory> histories = new HashSet<>();
    
    public HistoryManager(Enforcer plugin) {
        super(plugin, "history", false);
    }
    
    public PlayerHistory getPlayerHistory(Target target) {
        for (IHistory history : histories) {
            if (history instanceof PlayerHistory) {
                if (((PlayerHistory) history).getTarget().equals(target)) {
                    return (PlayerHistory) history;
                }
            }
        }
        
        PlayerHistory playerHistory = new PlayerHistory(target);
        this.histories.add(playerHistory);
        return playerHistory;
    }
    
    public StaffHistory getStaffHistory(Actor actor) {
        for (IHistory history : histories) {
            if (history instanceof StaffHistory) {
                if (((StaffHistory) history).getActor().equals(actor)) {
                    return (StaffHistory) history;
                }
            }
        }
        
        StaffHistory staffHistory = new StaffHistory(actor);
        this.histories.add(staffHistory);
        return staffHistory;
    }
    
    public void saveData() {
    
    }
    
    public void loadData() {
    
    }
}