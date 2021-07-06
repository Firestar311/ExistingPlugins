package com.kingrealms.realms.economy.account;

import com.kingrealms.realms.IOwner;
import com.kingrealms.realms.Realms;
import com.kingrealms.realms.economy.EconomyResponse;
import com.kingrealms.realms.economy.transaction.Transaction;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.pagination.IElement;
import com.starmediadev.lib.util.CommandViewable;
import com.starmediadev.lib.util.Constants;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SuppressWarnings("SameReturnValue")
@SerializableAs("Account")
public abstract class Account implements ConfigurationSerializable, CommandViewable, Comparable<Account>, IElement {
    
    protected final long accountNumber; //mysql
    protected final long created; //mysql
    protected String owner; //mysql
    protected boolean active; //mysql
    protected String name, description; //mysql
    
    public Account(String owner, long accountNumber) {
        this.owner = owner;
        this.accountNumber = accountNumber;
        this.created = System.currentTimeMillis();
        this.active = true;
        this.name = owner + "'s Account";
        this.description = "The account of " + owner;
    }
    
    public Account(Map<String, Object> serialized) {
        this.accountNumber = Long.parseLong((String) serialized.get("accountNumber"));
        this.created = Long.parseLong((String) serialized.get("created"));
        this.active = (boolean) serialized.get("active");
        this.name = (String) serialized.get("name");
        this.description = (String) serialized.get("description");
        this.owner = (String) serialized.get("owner");
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public long getAccountNumber() {
        return accountNumber;
    }
    
    public long getCreatedDate() {
        return created;
    }
    
    public abstract <T extends IOwner> T getOwner();
    
    public boolean isActive() {
        return active;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public double getBalance() {
        double balance = 0;
        
        for (Transaction transaction : Realms.getInstance().getEconomyManager().getTransactionHandler().getTransactions()) {
            if (!transaction.isRemoved()) {
                if (transaction.getToAccount() == this.accountNumber) {
                    balance += transaction.getAmount();
                } else if (transaction.getFromAccount() == this.accountNumber) {
                    balance -= transaction.getAmount();
                }
            }
        }
        
        return balance;
    }
    
    protected abstract EconomyResponse canDeposit(String actor, double amount);
    protected abstract EconomyResponse canWithdraw(String actor, double amount);
    public void sendMessageToOwner(String message) {
        getOwner().sendMessage(message);
    }
    
    public final EconomyResponse canDeposit() {
        return EconomyResponse.SUCCESS;
    }
    
    public final EconomyResponse canDeposit(RealmProfile profile, double amount) {
        return canDeposit(profile.getUniqueId().toString(), amount);
    }
    
    public final EconomyResponse canWithdraw(double amount) {
        return canWithdraw("Console", amount);
    }
    
    public final EconomyResponse canWithdraw(RealmProfile profile, double amount) {
        return canWithdraw(profile.getUniqueId().toString(), amount);
    }
    
    @Override
    public Map<String, String> getDisplayMap() {
        return new LinkedHashMap<>(){{
            put("Account Number", accountNumber + "");
            put("Created Date", Constants.DATE_FORMAT.format(new Date(created)));
            put("Owner", getOwner().getName());
            put("Name", name);
            put("Description", description);
            put("Active", active + "");
        }};
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("accountNumber", accountNumber + "");
            put("created", created + "");
            put("owner", owner);
            put("active", active);
            put("name", name);
            put("description", description);
        }};
    }
    
    @Override
    public int compareTo(Account o) {
        return Long.compare(this.accountNumber, o.accountNumber);
    }
    
    @Override
    public String formatLine(String... args) {
        String owner = getOwner().toString();
        String date = Constants.DATE_FORMAT.format(new Date(this.created));
        return " &8- &a" + accountNumber + "&f: &e" + owner + " &fcreated on &d" + date;
    }
}