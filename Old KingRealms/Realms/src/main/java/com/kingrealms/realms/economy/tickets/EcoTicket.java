package com.kingrealms.realms.economy.tickets;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.economy.transaction.Transaction;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.util.Constants;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SerializableAs("EcoTicket")
public class EcoTicket implements ConfigurationSerializable {
    
    private double amount;
    private long creationDate, useDate;
    private UUID creator, depositor;
    private String id;
    private long withdrawAccount, depositAccount;
    private long transactionId = -1;
    private Transaction transaction;
    
    public EcoTicket(long withdrawAccount, long creationDate, double amount, UUID creator) {
        this.withdrawAccount = withdrawAccount;
        this.creationDate = creationDate;
        this.amount = amount;
        this.creator = creator;
    }
    
    public EcoTicket(String id, long withdrawAccount, long depositAccount, long creationDate, long useDate, double amount, UUID creator, UUID depositor) {
        this.id = id;
        this.withdrawAccount = withdrawAccount;
        this.depositAccount = depositAccount;
        this.creationDate = creationDate;
        this.useDate = useDate;
        this.amount = amount;
        this.creator = creator;
        this.depositor = depositor;
    }
    
    public EcoTicket(Map<String, Object> serialized) {
        this.amount = Double.parseDouble((String) serialized.get("amount"));
        this.creationDate = Long.parseLong((String) serialized.get("creationDate"));
        if (serialized.containsKey("useDate")) {
            this.useDate = Long.parseLong((String) serialized.get("useDate"));
        }
        this.creator = UUID.fromString((String) serialized.get("creator"));
        if (serialized.containsKey("depositor")) {
            this.depositor = UUID.fromString((String) serialized.get("depositor"));
        }
        this.id = (String) serialized.get("id");
        this.withdrawAccount = Long.parseLong((String) serialized.get("withdrawAccount"));
        if (serialized.containsKey("depositAccount")) {
            this.depositAccount = Long.parseLong((String) serialized.get("depositAccount"));
        }
        if (serialized.containsKey("transactionId")) {
            this.transactionId = Long.parseLong((String) serialized.get("transactionId"));
        }
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("amount", amount + "");
            put("creationDate", creationDate + "");
            put("creator", creator.toString());
            put("id", id);
            put("withdrawAccount", withdrawAccount + "");
            if (useDate != 0) {
                put("useDate", useDate + "");
            }
            
            if (depositor != null) {
                put("depositor", depositor.toString());
            }
            
            if (depositAccount != 0) {
                put("depositAccount", depositAccount + "");
            }
            
            if (transactionId != -1) {
                put("transactionId", transactionId + "");
            }
        }};
    }
    
    public ItemStack getItem() {
        String amountFormat = Constants.NUMBER_FORMAT.format(this.amount) + " coins";
        return ItemBuilder.start(Material.PAPER).withName("&b&lEco Ticket&7: &f" + amountFormat).withLore("", "&6&lClaim &fto receive &e" + amountFormat).addNBTString("ecoticketid", this.id).buildItem();
    }
    
    public boolean hasBeenUsed() {
        return depositor != null || useDate != 0 || depositAccount != 0;
    }
    
    public void useTicket(long date, long depositAccount, UUID depositor) {
        this.useDate = date;
        this.depositAccount = depositAccount;
        this.depositor = depositor;
    }
    
    public void setTransaction(Transaction transaction) {
        this.transactionId = transaction.getId();
        this.transaction = transaction;
    }
    
    public Transaction getTransaction() {
        if (transaction == null) {
            if (transactionId != -1) {
                this.transaction = Realms.getInstance().getEconomyManager().getTransactionHandler().getTransation(transactionId);
            }
        }
        return transaction;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public long getWithdrawAccount() {
        return withdrawAccount;
    }
    
    public long getDepositAccount() {
        return depositAccount;
    }
    
    public void setDepositAccount(long depositAccount) {
        this.depositAccount = depositAccount;
    }
    
    public long getCreationDate() {
        return creationDate;
    }
    
    public long getUseDate() {
        return useDate;
    }
    
    public void setUseDate(long useDate) {
        this.useDate = useDate;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public UUID getCreator() {
        return creator;
    }
    
    public UUID getDepositor() {
        return depositor;
    }
    
    public void setDepositor(UUID depositor) {
        this.depositor = depositor;
    }
}