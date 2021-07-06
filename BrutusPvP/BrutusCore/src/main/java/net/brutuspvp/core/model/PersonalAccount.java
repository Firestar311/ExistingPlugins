package net.brutuspvp.core.model;

import net.brutuspvp.core.model.abstraction.Account;

import java.util.UUID;

public class PersonalAccount extends Account {

    public PersonalAccount(UUID owner) {
        super(owner);
    }

    public PersonalAccount(UUID owner, double balance) {
        super(owner, balance);
    }

    public boolean deposit(UUID uuid, double amount) {
        return false;
    }

    public boolean withdraw(UUID uuid, double amount) {
        return false;
    }
}
