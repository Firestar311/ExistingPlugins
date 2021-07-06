package net.brutuspvp.core.model;

import net.brutuspvp.core.BrutusCore;
import net.brutuspvp.core.model.abstraction.Account;

import java.util.UUID;

public class ServerAccount extends Account {

    public ServerAccount() {
        super(BrutusCore.getInstance().settings().getServerUUID());
    }

    public ServerAccount(double balance) {
        super(BrutusCore.getInstance().settings().getServerUUID(), balance);
    }

    public boolean deposit(UUID uuid, double amount) {
        return false;
    }

    public boolean withdraw(UUID uuid, double amount) {
        return false;
    }
}
