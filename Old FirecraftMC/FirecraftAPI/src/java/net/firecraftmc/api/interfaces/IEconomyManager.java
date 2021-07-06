package net.firecraftmc.api.interfaces;

import net.firecraftmc.api.enums.TransactionType;
import net.firecraftmc.api.model.Transaction;
import net.firecraftmc.api.model.player.FirecraftProfile;
import org.bukkit.event.Listener;

public interface IEconomyManager extends Listener {

    static Transaction deposit(FirecraftProfile profile, double amount) {
        Transaction transaction = new Transaction(profile.getUniqueId(), TransactionType.DEPOSIT, amount, System.currentTimeMillis());
        profile.addTransaction(transaction);
        return transaction;
    }

    static Transaction withdraw(FirecraftProfile profile, double amount) {
        Transaction transaction = new Transaction(profile.getUniqueId(), TransactionType.WITHDRAWAL, amount, System.currentTimeMillis());
        profile.addTransaction(transaction);
        return transaction;
    }
}