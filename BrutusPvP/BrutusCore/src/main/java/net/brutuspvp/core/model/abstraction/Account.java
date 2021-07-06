package net.brutuspvp.core.model.abstraction;

import java.util.UUID;

public abstract class Account {

    protected UUID owner;
    protected double balance;
    protected String name;

    public Account(UUID owner) {
        this.owner = owner;
    }

    public Account(UUID owner, double balance) {
        this.owner = owner;
        this.balance = balance;
    }

    public abstract boolean deposit(UUID uuid, double amount);

    public abstract boolean withdraw(UUID uuid, double amount);

    public final void setBalance(double newBalance) {
        this.balance = newBalance;
    }

    public final boolean take(double amount) {
        if (this.balance >= amount) {
            balance -= amount;
            return true;
        } else {
            return false;
        }
    }

    public final void give(double amount) {
        this.balance += amount;
    }

    public final boolean transfer(UUID uuid, Account other, double amount) {
        if (this.balance >= amount) {
            if (other.deposit(uuid, amount)) {
                if (this.withdraw(uuid, amount)) {
                    return true;
                } else {
                   this.deposit(uuid, amount);
                }
            }
        }
        return false;
    }

    public String getName() {
        return this.name;
    }

    public final UUID getOwner() {
        return this.owner;
    }

    public final double getBalance() {
        return this.balance;
    }
}