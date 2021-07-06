package com.stardevmc.chat.api;

import java.util.UUID;

public class PlayerOwner implements IOwner {
    
    private UUID owner;
    
    public PlayerOwner(UUID player) {
        this.owner = player;
    }
    
    public UUID getOwner() {
        return owner;
    }
    
    public String toString() {
        return owner.toString();
    }
}
