package com.stardevmc.titaneconomy;

import com.firestar311.lib.player.User;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class Account {

    private Owner owner;
    private Set<Transaction> transactions = new HashSet<>();
    
    public Account(Owner owner) {
        this.owner = owner;
    }
    
    public Account(Player player) {
        this.owner = new PlayerOwner();
    }
    
    public Account(User user) {
        this.owner = new PlayerOwner();
    }
    
    public Owner getOwner() {
        return owner;
    }
    
    public Set<Transaction> getTransactions() {
        return transactions;
    }
    
    public double getBalance() {
        double balance = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType() == Type.DEPOSIT) {
                balance += transaction.getAmount();
            } else {
                balance -= transaction.getAmount();
            }
        }
        return balance;
    }
}