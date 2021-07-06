package com.kingrealms.realms.api.events;

import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.territory.medievil.Hamlet;
import org.bukkit.event.HandlerList;

public class HamletJoinEvent extends HamletEvent {
    
    private static final HandlerList handlers = new HandlerList();
    
    public HamletJoinEvent(RealmProfile profile, Hamlet hamlet) {
        super(profile, hamlet);
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}