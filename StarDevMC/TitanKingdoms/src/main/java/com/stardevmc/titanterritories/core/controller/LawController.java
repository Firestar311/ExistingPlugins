package com.stardevmc.titanterritories.core.controller;

import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import org.bukkit.command.Command;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.HashMap;
import java.util.Map;

public class LawController<T extends IHolder> extends Controller<T> {
    
    static {ConfigurationSerialization.registerClass(LawController.class); }
    
    public LawController(T holder) {
        super(holder);
    }
    
    public static UserController deserialize(Map<String, Object> serialized) {
        
        return null;
    }
    
    public void handleCommand(Command cmd, T holder, IUser user, String[] args) {
    
    }
    
    //TODO
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        return serialized;
    }
}