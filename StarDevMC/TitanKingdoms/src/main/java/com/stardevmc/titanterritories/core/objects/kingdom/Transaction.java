package com.stardevmc.titanterritories.core.objects.kingdom;

import com.firestar311.lib.pagination.IElement;
import com.stardevmc.titanterritories.core.TitanTerritories;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.text.SimpleDateFormat;
import java.util.*;

public class Transaction implements ConfigurationSerializable, IElement {
    public enum Type {
        DEPOSIT, WITHDRAWL
    }
    
    private double amount;
    private Type type;
    private UUID uuid;
    private boolean console;
    private long date;
    
    public Transaction(double amount, Type type, UUID uuid) {
        this.amount = amount;
        this.type = type;
        this.uuid = uuid;
        this.date = System.currentTimeMillis();
    }
    
    public Transaction(double amount, Type type, boolean console) {
        this.amount = amount;
        this.type = type;
        this.console = console;
        this.date = System.currentTimeMillis();
    }
    
    public Transaction(Map<String, Object> serialized) {
        if (serialized.containsKey("amount")) {
            this.amount = (double) serialized.get("amount");
        }
        
        if (serialized.containsKey("type")) {
            this.type = Type.valueOf((String) serialized.get("type"));
        }
        
        if (serialized.containsKey("uuid")) {
            this.uuid = UUID.fromString((String) serialized.get("uuid"));
        }
        
        if (serialized.containsKey("console")) {
            this.console = (boolean) serialized.get("console");
        }
        
        if (serialized.containsKey("date")) {
            this.date = Long.parseLong((String) serialized.get("date"));
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("amount", amount);
        serialized.put("type", type.name());
        if (uuid != null) {
            serialized.put("uuid", uuid.toString());
        } else {
            serialized.put("console", console);
        }
        serialized.put("date", date + "");
        return serialized;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public Type getType() {
        return type;
    }
    
    public UUID getUniqueId() {
        return uuid;
    }
    
    public boolean isConsole() {
        return console;
    }
    
    public void setConsole(boolean console) {
        this.console = console;
    }
    
    public long getDate() {
        return date;
    }
    
    public String formatLine(String... args) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a z");
        String format = " &8- &7" + dateFormat.format(new Date(this.date)) + " {actor} {type} &7$" + this.amount;
        if (console) {
            format = format.replace("{actor}", "CONSOLE");
        } else {
            format = format.replace("{actor}", TitanTerritories.getInstance().getMemberManager().getMember(uuid).getName());
        }
        
        switch (type) {
            case DEPOSIT: format = format.replace("{type}", "&adeposited");
                break;
            case WITHDRAWL: format = format.replace("{type}", "&cwithdrew");
                break;
        }
        
        return format;
    }
}