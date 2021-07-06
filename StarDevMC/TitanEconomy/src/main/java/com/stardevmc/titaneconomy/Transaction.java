package com.stardevmc.titaneconomy;

public class Transaction {
    private long date;
    private Actor actor;
    private Type type;
    private double amount;
    
    public Transaction(long date, Actor actor, Type type, double amount) {
        this.date = date;
        this.actor = actor;
        this.type = type;
        this.amount = amount;
    }
    
    public long getDate() {
        return date;
    }
    
    public Actor getActor() {
        return actor;
    }
    
    public Type getType() {
        return type;
    }
    
    public double getAmount() {
        return amount;
    }
}