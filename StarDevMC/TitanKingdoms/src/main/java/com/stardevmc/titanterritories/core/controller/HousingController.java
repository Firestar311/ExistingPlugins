package com.stardevmc.titanterritories.core.controller;

import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import org.bukkit.command.Command;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.HashMap;
import java.util.Map;

public class HousingController<T extends IHolder> extends Controller<T> {
    
    static { ConfigurationSerialization.registerClass(HousingController.class); }
    
    public HousingController(T holder) {
        super(holder);
    }
    
    public void handleCommand(Command cmd, T holder, IUser user, String[] args) {
    
    }
    
    //TODO
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        return serialized;
    }
    
    public static HousingController deserialize(Map<String, Object> serialized) {
        
        return null;
    }
}