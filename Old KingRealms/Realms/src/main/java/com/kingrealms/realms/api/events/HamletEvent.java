package com.kingrealms.realms.api.events;

import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.territory.medievil.Hamlet;
import org.bukkit.event.HandlerList;

public abstract class HamletEvent extends ProfileEvent {
    
    private static final HandlerList handlers = new HandlerList();
    
    protected Hamlet hamlet;
    
    public HamletEvent(RealmProfile profile, Hamlet hamlet) {
        super(profile);
        this.hamlet = hamlet;
    }
    
    public Hamlet getHamlet() {
        return hamlet;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}