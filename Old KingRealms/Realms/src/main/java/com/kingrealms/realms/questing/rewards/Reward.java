package com.kingrealms.realms.questing.rewards;

import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.ID;

public abstract class Reward {
    protected ID id;
    protected String name;
    
    public Reward(ID id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public Reward(String name) {
        this.name = name;
    }
    
    public ID getId() {
        return id;
    }
    
    public void setId(ID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public abstract void applyReward(RealmProfile profile);
}