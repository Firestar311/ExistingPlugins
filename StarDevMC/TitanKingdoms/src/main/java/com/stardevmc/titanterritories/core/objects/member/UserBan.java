package com.stardevmc.titanterritories.core.objects.member;

import java.util.UUID;

public class UserBan {
    private UUID uuid;
    private UUID actor;
    private String reason;
    
    public UserBan(UUID uuid, UUID actor, String reason) {
        this.uuid = uuid;
        this.actor = actor;
        this.reason = reason;
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public UUID getActor() {
        return actor;
    }
    
    public String getReason() {
        return reason;
    }
}