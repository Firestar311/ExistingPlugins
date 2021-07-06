package net.firecraftmc.api.friends;

import java.util.UUID;

public class Friend {
    
    private UUID uuid;
    
    public Friend(UUID uuid) {
        this.uuid = uuid;
    }
    
    public UUID getUniqueId() {
        return uuid;
    }
}