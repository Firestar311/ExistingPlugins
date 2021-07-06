package com.firestar311.lib.player;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;
import java.util.Map.Entry;

public class ServerUUIDHistory implements ConfigurationSerializable {
    
    private SortedMap<Long, UUID> previousUUIDs = new TreeMap<>();
    
    public ServerUUIDHistory() {
    }
    
    public ServerUUIDHistory(Map<String, Object> serialized) {
        int count = (int) serialized.get("uuidCount");
        for (int i = 0; i < count; i++) {
            UUID uuid = UUID.fromString(((String) serialized.get(i + ".uuid")));
            long date = Long.parseLong((String) serialized.get(i + ".date"));
            this.previousUUIDs.put(date, uuid);
        }
    }
    
    public void addPreviousUUID(long endDate, UUID uuid) {
        this.previousUUIDs.put(endDate, uuid);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("uuidCount", previousUUIDs.size());
        int counter = 0;
        for (Entry<Long, UUID> entry : this.previousUUIDs.entrySet()) {
            serialized.put(counter + ".uuid", entry.getValue().toString());
            serialized.put(counter + ".date", entry.getKey().toString());
            counter++;
        }
        return serialized;
    }
    
    public UUID getPreviousUUID(long time) {
        long previousDate = 0;
        for (Entry<Long, UUID> entry : previousUUIDs.entrySet()) {
            long endDate = entry.getKey();
            UUID uuid = entry.getValue();
            if (previousDate == 0) {
                if (time <= endDate) {
                    return uuid;
                }
            }
            if ((time > previousDate) && (time < endDate)) {
                return uuid;
            } else {
                previousDate = endDate;
            }
        }
        return null;
    }
}