package com.stardevmc.titanterritories.core.objects.kingdom;

import com.firestar311.lib.pagination.IElement;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class ExperienceAction implements ConfigurationSerializable, IElement {
    private double amount;
    private boolean console;
    private IUser member;
    private Type type;
    public ExperienceAction(double amount, Type type, IUser member) {
        this.amount = amount;
        this.type = type;
        this.member = member;
    }
    
    public ExperienceAction(double amount, Type type, boolean console) {
        this.amount = amount;
        this.type = type;
        this.console = console;
    }
    
    public ExperienceAction(Map<String, Object> serialized) {
        if (serialized.containsKey("amount")) {
            this.amount = (double) serialized.get("amount");
        }
        
        if (serialized.containsKey("type")) {
            this.type = Type.valueOf((String) serialized.get("type"));
        }
        
        if (serialized.containsKey("member")) {
            this.member = TitanTerritories.getInstance().getMemberManager().getMember(UUID.fromString((String) serialized.get("member")));
        }
        
        if (serialized.containsKey("console")) {
            this.console = (boolean) serialized.get("console");
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("amount", amount);
        serialized.put("type", type.name());
        serialized.put("member", member.getUniqueId().toString());
        serialized.put("console", console);
        return serialized;
    }
    
    public String formatLine(String... args) {
        String actor = isConsole() ? "Console" : member.getName();
        String typeString = "";
        switch (type) {
            case GAIN: typeString = "&agained"; break;
            case LOSS: typeString = "&clost"; break;
        }
        
        return " &8- &7" + actor + " " + typeString + " &7" + amount;
    }
    
    public boolean isConsole() {
        return console;
    }
    
    public void setConsole(boolean console) {
        this.console = console;
    }
    
    public enum Type {
        GAIN, LOSS
    }
    
    public double getAmount() {
        return amount;
    }
    
    public Type getType() {
        return type;
    }
    
    public IUser getMember() {
        return member;
    }
}