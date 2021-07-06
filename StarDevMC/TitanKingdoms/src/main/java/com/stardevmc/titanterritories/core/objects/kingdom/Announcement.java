package com.stardevmc.titanterritories.core.objects.kingdom;

import com.firestar311.lib.pagination.IElement;
import com.stardevmc.titanterritories.core.TitanTerritories;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class Announcement implements ConfigurationSerializable, IElement {
    private UUID creator;
    private int order;
    private String message;
    
    public Announcement(UUID creator, int order, String message) {
        this.creator = creator;
        this.order = order;
        this.message = message;
    }
    
    public Announcement(Map<String, Object> serialized) {
        if (serialized.containsKey("creator")) {
            this.creator = UUID.fromString((String) serialized.get("creator"));
        }
        
        if (serialized.containsKey("order")) {
            this.order = ((int) serialized.get("order"));
        }
        
        if (serialized.containsKey("message")) {
            this.message = (String) serialized.get("message");
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("creator", creator.toString());
        serialized.put("order", order);
        serialized.put("message", message);
        return serialized;
    }
    
    public String formatLine(String... args) {
        return "&a" + this.order + " " + TitanTerritories.getInstance().getMemberManager().getMember(creator).getName() + ": " + this.message;
    }
    
    public UUID getCreator() {
        return creator;
    }
    
    public int getOrder() {
        return order;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Announcement that = (Announcement) o;
        return order == that.order && Objects.equals(creator, that.creator) && Objects.equals(message, that.message);
    }
    
    public int hashCode() {
        return Objects.hash(creator, order, message);
    }
}