package com.kingrealms.realms.economy.transaction;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.api.events.TransactionEvent;
import com.kingrealms.realms.economy.EconomyResponse;
import com.kingrealms.realms.economy.account.Account;
import com.kingrealms.realms.economy.account.ServerAccount;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.collection.IncrementalLongMap;
import com.starmediadev.lib.util.Constants;
import com.starmediadev.lib.util.Pair;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("DuplicatedCode")
public class TransactionHandler {
    
    private final IncrementalLongMap<Transaction> transactions = new IncrementalLongMap<>();
    
    public void saveData(ConfigurationSection section) {
        for (Transaction transaction : getTransactions()) {
            section.set(transaction.getId() + "", transaction);
        }
    }
    
    public Set<Transaction> getTransactions() {
        return new HashSet<>(transactions.values());
    }
    
    public void clearTransactions() {
        this.transactions.clear();
    }
    
    public void loadData(ConfigurationSection section) {
        if (section != null) {
            for (String t : section.getKeys(false)) {
                Transaction transaction = (Transaction) section.get(t);
                this.transactions.put(transaction.getId(), transaction);
            }
        }
    }
    
    public boolean removeTransaction(long transId, CommandSender actor) {
        Transaction transaction = this.transactions.get(transId);
        if (transaction == null) {
            return false;
        }
        
        transaction.setRemovedInfo(System.currentTimeMillis(), actor.getName());
        return true;
    }
    
    public Pair<EconomyResponse, EconomyResponse> transfer(double amount, Account from, Account to, String... description) {
        EconomyResponse fromResponse = from.canWithdraw(amount);
        EconomyResponse toResponse = to.canDeposit();
        Pair<EconomyResponse, EconomyResponse> responses = new Pair<>(fromResponse, toResponse);
        
        if (!(fromResponse == EconomyResponse.SUCCESS) || !(toResponse == EconomyResponse.SUCCESS)) {
            return responses;
        }
        
        Transaction transaction = new Transaction(System.currentTimeMillis(), amount, from.getAccountNumber(), to.getAccountNumber());
        if (description != null) {
            transaction.setDescription(StringUtils.join(description, " ", 0, description.length));
        }
        addTransactionWithMsg(transaction);
        return responses;
    }
    
    public void addTransactionWithMsg(Transaction transaction) {
        addTransaction(transaction);
        new BukkitRunnable() {
            public void run() {
                String amount = Constants.NUMBER_FORMAT.format(Math.abs(transaction.getAmount()));
                Account fromAccount = Realms.getInstance().getEconomyManager().getAccountHandler().getAccount(transaction.getFromAccount());
                Account toAccount = Realms.getInstance().getEconomyManager().getAccountHandler().getAccount(transaction.getToAccount());
                if (!(fromAccount instanceof ServerAccount)) {
                    fromAccount.sendMessageToOwner("&8[&a■&8] &c- &7" + amount + " &bcoins");
                }
                if (toAccount != null) {
                    if (!(toAccount instanceof ServerAccount)) {
                        toAccount.sendMessageToOwner("&8[&a■&8] &a+ &7" + amount + " &bcoins");
                    }
                }
                TransactionEvent event = new TransactionEvent(transaction);
                Bukkit.getPluginManager().callEvent(event);
            }
        }.runTaskLater(Realms.getInstance(), 1L);
    }
    
    public void addTransaction(Transaction transaction) {
        if (transaction.getId() != -1) {
            this.transactions.put(transaction.getId(), transaction);
        } else {
            long id = this.transactions.add(transaction);
            transaction.setId(id);
        }
        TransactionEvent event = new TransactionEvent(transaction);
        Bukkit.getPluginManager().callEvent(event);
    }
    
    public Pair<EconomyResponse, EconomyResponse> transfer(RealmProfile profile, double amount, Account from, Account to, String... description) {
        EconomyResponse fromResponse = from.canWithdraw(profile, amount);
        EconomyResponse toResponse = to.canDeposit(profile, amount);
        Pair<EconomyResponse, EconomyResponse> responses = new Pair<>(fromResponse, toResponse);
        
        if (!(fromResponse == EconomyResponse.SUCCESS) || !(toResponse == EconomyResponse.SUCCESS)) {
            return responses;
        }
        
        Transaction transaction = new Transaction(System.currentTimeMillis(), amount, from.getAccountNumber(), to.getAccountNumber());
        if (description != null) {
            transaction.setDescription(StringUtils.join(description, " ", 0, description.length));
        }
        addTransactionWithMsg(transaction);
        return responses;
    }
    
    public Pair<EconomyResponse, EconomyResponse> deposit(double amount, Account account, String... description) {
        ServerAccount serverAccount = Realms.getInstance().getEconomyManager().getAccountHandler().getMainServerAccount();
        EconomyResponse fromResponse = serverAccount.canWithdraw(amount);
        EconomyResponse toResponse = account.canDeposit();
        Pair<EconomyResponse, EconomyResponse> responses = new Pair<>(fromResponse, toResponse);
        
        if (!(fromResponse == EconomyResponse.SUCCESS) || !(toResponse == EconomyResponse.SUCCESS)) {
            return responses;
        }
        
        Transaction transaction = new Transaction(System.currentTimeMillis(), amount, serverAccount.getAccountNumber(), account.getAccountNumber());
        if (description != null) {
            transaction.setDescription(StringUtils.join(description, " ", 0, description.length));
        }
        addTransactionWithMsg(transaction);
        return responses;
    }
    
    public Set<Transaction> getTransactions(long accountNumber) {
        Set<Transaction> transactions = new HashSet<>();
        for (Transaction transaction : this.getTransactions()) {
            if (transaction.getToAccount() == accountNumber || transaction.getFromAccount() == accountNumber) {
                transactions.add(transaction);
            }
        }
        
        return transactions;
    }
    
    public Pair<EconomyResponse, EconomyResponse> deposit(RealmProfile profile, double amount, Account account, String... description) {
        ServerAccount serverAccount = Realms.getInstance().getEconomyManager().getAccountHandler().getMainServerAccount();
        EconomyResponse fromResponse = serverAccount.canWithdraw(profile, amount);
        EconomyResponse toResponse = account.canDeposit(profile, amount);
        Pair<EconomyResponse, EconomyResponse> responses = new Pair<>(fromResponse, toResponse);
        
        if (!(fromResponse == EconomyResponse.SUCCESS) || !(toResponse == EconomyResponse.SUCCESS)) {
            return responses;
        }
        
        Transaction transaction = new Transaction(System.currentTimeMillis(), amount, serverAccount.getAccountNumber(), account.getAccountNumber());
        if (description != null) {
            transaction.setDescription(StringUtils.join(description, " ", 0, description.length));
        }
        addTransactionWithMsg(transaction);
        return responses;
    }
    
    public Pair<EconomyResponse, EconomyResponse> withdraw(double amount, Account account, String... description) {
        ServerAccount serverAccount = Realms.getInstance().getEconomyManager().getAccountHandler().getMainServerAccount();
        EconomyResponse fromResponse = serverAccount.canDeposit();
        EconomyResponse toResponse = account.canWithdraw(amount);
        Pair<EconomyResponse, EconomyResponse> responses = new Pair<>(fromResponse, toResponse);
        
        if (!(fromResponse == EconomyResponse.SUCCESS) || !(toResponse == EconomyResponse.SUCCESS)) {
            return responses;
        }
        
        Transaction transaction = new Transaction(System.currentTimeMillis(), amount, account.getAccountNumber(), serverAccount.getAccountNumber());
        if (description != null) {
            transaction.setDescription(StringUtils.join(description, " ", 0, description.length));
        }
        addTransactionWithMsg(transaction);
        return responses;
    }
    
    public Pair<EconomyResponse, EconomyResponse> withdraw(RealmProfile profile, double amount, Account account, String... description) {
        ServerAccount serverAccount = Realms.getInstance().getEconomyManager().getAccountHandler().getMainServerAccount();
        EconomyResponse fromResponse = serverAccount.canDeposit(profile, amount);
        EconomyResponse toResponse = account.canWithdraw(profile, amount);
        Pair<EconomyResponse, EconomyResponse> responses = new Pair<>(fromResponse, toResponse);
        
        if (!(fromResponse == EconomyResponse.SUCCESS) || !(toResponse == EconomyResponse.SUCCESS)) {
            return responses;
        }
        
        Transaction transaction = new Transaction(System.currentTimeMillis(), amount, account.getAccountNumber(), serverAccount.getAccountNumber());
        if (description != null) {
            transaction.setDescription(StringUtils.join(description, " ", 0, description.length));
        }
        addTransactionWithMsg(transaction);
        return responses;
    }
    
    public void initialDeposit(Account account) {
        ServerAccount serverAccount = Realms.getInstance().getEconomyManager().getAccountHandler().getMainServerAccount();
        Transaction transaction = new Transaction(System.currentTimeMillis(), 100, serverAccount.getAccountNumber(), account.getAccountNumber(), "Initial deposit");
        addTransaction(transaction);
    }
    
    public Transaction getTransation(long id) {
        return this.transactions.get(id);
    }
}