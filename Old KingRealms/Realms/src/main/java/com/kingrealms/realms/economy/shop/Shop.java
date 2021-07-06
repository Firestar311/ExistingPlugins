package com.kingrealms.realms.economy.shop;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.economy.account.Account;
import com.kingrealms.realms.economy.shop.enums.OwnerType;
import com.kingrealms.realms.economy.shop.enums.ShopType;
import com.kingrealms.realms.economy.transaction.TransactionHandler;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("Shop")
public abstract class Shop implements ConfigurationSerializable {
    protected Account account; //cache
    protected long accountNumber = -1; //mysql
    protected String name, description; //mysql
    protected String owner; //mysql
    @Deprecated
    protected UUID uniqueId; //Replace with mysql auto-id
    protected boolean fromTemplate; //mysql
    protected ShopType shopType; //mysql
    protected OwnerType ownerType; //mysql
    
    public Shop(Map<String, Object> serialized) {
        this.accountNumber = Long.parseLong((String) serialized.get("account"));
        this.name = (String) serialized.get("name");
        this.description = (String) serialized.get("description");
        this.owner = (String) serialized.get("owner");
        this.uniqueId = UUID.fromString((String) serialized.get("uniqueId"));
        this.fromTemplate = (boolean) serialized.get("fromTemplate");
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("account", accountNumber + "");
            put("name", name);
            put("description", description);
            put("owner", owner);
            put("uniqueId", uniqueId.toString());
            put("fromTemplate", fromTemplate);
        }};
    }
    
    public Shop(String owner) {
        this.owner = owner;
    }
    
    public UUID getUniqueId() {
        return uniqueId;
    }
    
    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    public long getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public abstract <T> T getOwner();
    
    public void linkAccount(Account account) {
        this.account = account;
        this.accountNumber = account.getAccountNumber();
    }
    
    public Account getAccount() {
        if (this.account == null) {
            this.account = Realms.getInstance().getEconomyManager().getAccountHandler().getAccount(accountNumber);
        }
        
        if (accountNumber != this.account.getAccountNumber()) {
            this.account = Realms.getInstance().getEconomyManager().getAccountHandler().getAccount(accountNumber);
        }
        
        return account;
    }
    
    public TransactionHandler getTransactionHandler() {
        return Realms.getInstance().getEconomyManager().getTransactionHandler();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Shop shop = (Shop) o;
        return Objects.equals(uniqueId, shop.uniqueId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }
    
    public boolean isFromTemplate() {
        return fromTemplate;
    }
    
    public void setFromTemplate(boolean fromTemplate) {
        this.fromTemplate = fromTemplate;
    }
    
    public abstract void update();
    
    public OwnerType getOwnerType() {
        return ownerType;
    }
    
    public ShopType getShopType() {
        return shopType;
    }
}