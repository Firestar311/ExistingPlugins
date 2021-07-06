package com.kingrealms.realms.economy.transaction;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.pagination.IElement;
import com.starmediadev.lib.util.CommandViewable;
import com.starmediadev.lib.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("Transaction")
public class Transaction implements ConfigurationSerializable, IElement, CommandViewable, Comparable<Transaction> {
    
    private double amount; //mysql
    private long date; //mysql
    private long fromAccount, toAccount; //mysql
    private String description, removedActor; //mysql
    private long id = -1; //mysql
    private long removedDate = -1; //mysql
    
    public Transaction(Map<String, Object> serialized) {
        this.id = Long.parseLong((String) serialized.get("id"));
        this.date = Long.parseLong((String) serialized.get("date"));
        this.removedDate = Long.parseLong((String) serialized.get("removedDate"));
        this.amount = Double.parseDouble((String) serialized.get("amount"));
        this.fromAccount = Long.parseLong((String) serialized.get("from"));
        this.toAccount = Long.parseLong((String) serialized.get("to"));
        this.description = (String) serialized.get("description");
        this.removedActor = (String) serialized.get("removedActor");
    }
    
    public Transaction(long id, long date, double amount, long fromAccount, long toAccount, String description) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.description = description;
    }
    
    public Transaction(long id, long date, double amount, long fromAccount, long toAccount) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
    }
    
    public Transaction(long date, double amount, long fromAccount, long toAccount, String description) {
        this.date = date;
        this.amount = amount;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.description = description;
    }
    
    public Transaction(long date, double amount, long fromAccount, long toAccount) {
        this.date = date;
        this.amount = amount;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
    }
    
    public long getId() {
        return id;
    }
    
    protected void setId(long id) {
        this.id = id;
    }
    
    public long getDate() {
        return date;
    }
    
    public long getFromAccount() {
        return fromAccount;
    }
    
    public long getToAccount() {
        return toAccount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setRemovedInfo(long date, String actor) {
        this.removedDate = date;
        this.removedActor = actor;
    }
    
    public String getRemovedActor() {
        return removedActor;
    }
    
    public long getRemovedDate() {
        return removedDate;
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("id", id + "");
            put("date", date + "");
            put("removedDate", removedDate + "");
            put("amount", amount + "");
            put("from", fromAccount + "");
            put("to", toAccount + "");
            put("description", description);
            put("removedActor", removedActor);
        }};
    }
    
    @Override
    public String formatLine(String... args) {
        String defaultFormat = " &8- &f" + fromAccount + " -> " + toAccount + ": " + amount;
        if (args.length != 1) {
            return defaultFormat;
        }
        
        long an;
        try {
            an = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            return defaultFormat;
        }
        
        String type;
        if (fromAccount == an) {
            type = "&cWITHDRAWAL";
        } else {
            type = "&aDEPOSIT";
        }
        
        if (!isRemoved()) {
            return " &8- &e" + this.id + "&f: " + type + " &fof &b" + amount + " &fon &d" + Constants.DATE_FORMAT.format(new Date(this.date));
        } else {
            return " &8- &c[REMOVED] " + this.id + "&f: " + type + " &fof &b" + amount + " &fon &d" + Constants.DATE_FORMAT.format(new Date(this.date));
        }
    }
    
    public boolean isRemoved() {
        return removedDate == -1 && !StringUtils.isEmpty(this.removedActor);
    }
    
    @Override
    public Map<String, String> getDisplayMap() {
        return new TreeMap<>() {{
            put("ID", id + "");
            put("Date", Constants.DATE_FORMAT.format(new Date(date)));
            put("Amount", Constants.NUMBER_FORMAT.format(amount));
            put("From Account", fromAccount + "");
            put("To Account", toAccount + "");
            if (!StringUtils.isEmpty(description)) {
                put("Description", description);
            }
            
            if (isRemoved()) {
                ConsoleCommandSender ca = resolveActorToConsole();
                RealmProfile profile = resolveActorToPlayer();
                String actor;
                if (ca != null) {
                    actor = "Console";
                } else {
                    actor = profile.getName();
                }
                
                put("Removed By", actor);
                put("Removed Date", Constants.DATE_FORMAT.format(new Date(removedDate)));
            }
        }};
    }
    
    public ConsoleCommandSender resolveActorToConsole() {
        if (this.removedActor.equalsIgnoreCase("console")) {
            return Bukkit.getConsoleSender();
        }
        return null;
    }
    
    public RealmProfile resolveActorToPlayer() {
        return Realms.getInstance().getProfileManager().getProfile(this.removedActor);
    }
    
    @Override
    public int compareTo(Transaction o) {
        return Long.compare(o.id, this.id);
    }
    
    public void setToAccount(long toAccount) {
        this.toAccount = toAccount;
    }
}