package com.firestar311.lib.superadmins;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SuperAdminToggleEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private boolean value;
    
    public SuperAdminToggleEvent(boolean value) {
        this.value = value;
    }
    
    public boolean getValue() {
        return value;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public HandlerList getHandlerList() {
        return handlers;
    }
}