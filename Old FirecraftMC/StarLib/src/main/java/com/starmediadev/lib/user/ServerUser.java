package com.starmediadev.lib.user;

import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("ServerUser")
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
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("uuid", this.getUniqueId().toString());
        serialized.put("history", this.history);
        return serialized;
    }
    
    public static ServerUser deserialize(Map<String, Object> serialized) {
        UUID uuid = UUID.fromString((String) serialized.get("uuid"));
        ServerUUIDHistory history = (ServerUUIDHistory) serialized.get("history");
        return new ServerUser(uuid, history);
    }
}