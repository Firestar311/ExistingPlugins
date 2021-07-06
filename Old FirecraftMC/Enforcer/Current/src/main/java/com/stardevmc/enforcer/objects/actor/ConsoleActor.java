package com.stardevmc.enforcer.objects.actor;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ConsoleActor extends Actor {
    public ConsoleActor() {}
    
    public String getName() {
        return "Console";
    }
    
    public Player getPlayer() {
        return null;
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>();
    }
    
    public static ConsoleActor deserialize(Map<String, Object> serialized) {
        return new ConsoleActor();
    }
}