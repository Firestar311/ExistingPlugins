package com.firestar311.lib.player;

import java.util.UUID;

public class ServerUser extends User {
    
    private ServerUUIDHistory history;
    
    public ServerUser(UUID uuid) {
        super(uuid);
        history = new ServerUUIDHistory();
        setLastName("SERVER");
    }
    
    public ServerUser(UUID uuid, ServerUUIDHistory history) {
        super(uuid);
        this.history = history;
        setLastName("SERVER");
    }
    
    public void setUniqueId(UUID uuid) {
        if (history == null) {
            history = new ServerUUIDHistory();
        }
        history.addPreviousUUID(System.currentTimeMillis(), this.getUniqueId());
        this.uuid = uuid;
    }
    
    public ServerUUIDHistory getHistory() {
        return history;
    }
    
    public String toString() {
        return this.uuid.toString() + ":" + System.currentTimeMillis();
    }
    
    
}