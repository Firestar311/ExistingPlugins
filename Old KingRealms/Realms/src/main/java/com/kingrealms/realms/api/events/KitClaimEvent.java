package com.kingrealms.realms.api.events;

import com.kingrealms.realms.kits.Kit;
import com.kingrealms.realms.profile.RealmProfile;
import org.bukkit.event.HandlerList;

public class KitClaimEvent extends ProfileEvent {
    
    private static final HandlerList handlers = new HandlerList();
    private Kit kit;
    
    public KitClaimEvent(RealmProfile profile, Kit kit) {
        super(profile);
        this.kit = kit;
    }
    
    public Kit getKit() {
        return kit;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}