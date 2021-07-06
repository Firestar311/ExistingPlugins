package com.kingrealms.realms.season;

import com.kingrealms.realms.Realms;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class Season {
    
    boolean active = false;
    private List<Listener> listeners = new ArrayList<>();
    private int number;
    private Type type;
    public Season(int number, Type type) {
        this.number = number;
        this.type = type;
    }
    
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }
    
    public int getNumber() {
        return number;
    }
    
    public Type getType() {
        return type;
    }
    
    public void registerListeners() {
        if (!isActive()) { return; }
        for (Listener listener : this.listeners) {
            Realms.getInstance().getServer().getPluginManager().registerEvents(listener, Realms.getInstance());
        }
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            try {
                registerListeners();
//                for (User user : plugin.getUserManager().getUsers().values()) {
//                    user.clearSessions();
//                }
    
//                for (RealmProfile profile : plugin.getProfileManager().getProfiles()) {
//                    profile.getKilledMobs().clear();
//                    profile.getPlacedBlocks().clear();
//                    profile.getMinedBlocks().clear();
//                    profile.getSkillExperience().clear();
//                    profile.getHomes().clear();
//                    profile.getKitUsage().clear();
//                    profile.getCompletedQuests().clear();
//                    profile.getQuestProgress().clear();
//                    profile.getActiveQuestLines().clear();
//                }
//    
//                plugin.getEconomyManager().getTransactionHandler().clearTransactions();
//                plugin.getTerritoryManager().clearTerritories();
            } catch (Exception e) {}
        } else {
            unregisterListeners();
        }
    }
    
    private void unregisterListeners() {
        for (Listener listener : this.listeners) {
            HandlerList.unregisterAll(listener);
        }
    }
    
    public enum Type {
        BETA, RELEASE
    }
}