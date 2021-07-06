package com.kingrealms.realms.api.events;

import com.kingrealms.realms.profile.RealmProfile;
import org.bukkit.event.HandlerList;

public abstract class ProfileEvent extends RealmEvent {
    
    private static final HandlerList handlers = new HandlerList();
    
    protected RealmProfile profile;
    
    public ProfileEvent(RealmProfile profile) {
        this.profile = profile;
    }
    
    public RealmProfile getProfile() {
        return profile;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}