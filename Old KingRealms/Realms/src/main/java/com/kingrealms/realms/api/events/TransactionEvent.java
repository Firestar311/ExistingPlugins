package com.kingrealms.realms.api.events;

import com.kingrealms.realms.economy.transaction.Transaction;
import org.bukkit.event.HandlerList;

public class TransactionEvent extends RealmEvent {
    
    private static final HandlerList handlers = new HandlerList();
    private Transaction transaction;
    
    public TransactionEvent(Transaction transaction) {
        this.transaction = transaction;
    }
    
    public Transaction getTransaction() {
        return transaction;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}