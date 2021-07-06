package com.starmediadev.lib.user;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("ChatMessage")
public class ChatMessage implements ConfigurationSerializable, Comparable<ChatMessage> {
    
    private String sender;
    private String message;
    private long time;
    
    public ChatMessage(String sender, String message, long time) {
        this.sender = sender;
        this.message = message;
        this.time = time;
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("sender", this.sender);
        serialized.put("message", this.message);
        serialized.put("time", this.time + "");
        return serialized;
    }
    
    public static ChatMessage deserialize(Map<String, Object> serialized) {
        String sender = (String) serialized.get("sender");
        String message = (String) serialized.get("message");
        long time = Long.parseLong((String) serialized.get("time"));
        return new ChatMessage(sender, message, time);
    }
    
    @Override
    public int compareTo(ChatMessage o) {
        return Long.compare(this.time, o.time);
    }
    
    public String getSender() {
        return sender;
    }
    
    public String getMessage() {
        return message;
    }
    
    public long getTime() {
        return time;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ChatMessage that = (ChatMessage) o;
        return time == that.time && Objects.equals(sender, that.sender) && Objects.equals(message, that.message);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sender, message, time);
    }
}