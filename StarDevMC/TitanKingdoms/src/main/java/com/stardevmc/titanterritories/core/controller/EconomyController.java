package com.stardevmc.titanterritories.core.controller;

import com.firestar311.lib.pagination.Paginator;
import com.firestar311.lib.pagination.PaginatorFactory;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.enums.Permission;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.Transaction;
import com.stardevmc.titanterritories.core.objects.kingdom.Transaction.Type;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;

import java.util.*;
import java.util.stream.Collectors;

public class EconomyController<T extends IHolder> extends Controller<T> {
    
    private TitanTerritories plugin = TitanTerritories.getInstance();
    private List<Transaction> transactions = new ArrayList<>();
    
    public EconomyController(T kingdom) {
        super(kingdom);
    }
    
    private EconomyController() {
    }
    
    public static EconomyController deserialize(Map<String, Object> serialized) {
        List<Transaction> transactions = new ArrayList<>();
        if (serialized.containsKey("amount")) {
            int transactionAmount = (int) serialized.get("amount");
            for (int i = 0; i < transactionAmount; i++) {
                transactions.add((Transaction) serialized.get("transaction" + i));
            }
        }
        EconomyController economyController = new EconomyController();
        economyController.transactions = transactions;
        return economyController;
    }
    
    public void handleCommand(Command cmd, IHolder holder, IUser user, String[] args) {
        if (Utils.checkCmdAliases(args, 0, "deposit")) {
            if (!user.hasPermission(Permission.DEPOSIT)) {
                user.sendMessage("&cYou do not have permission to deposit into the " + holder.getClass().getSimpleName() + " bank.");
                return;
            }
            
            if (!(args.length > 1)) {
                user.sendMessage("&cYou must provide an amount.");
                return;
            }
            
            double amount;
            try {
                amount = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                user.sendMessage("The value you provided for the amount is not a valid number");
                return;
            }
            
            EconomyResponse response = plugin.getVaultEconomy().withdrawPlayer(user.getPlayer(), amount);
            if (!response.transactionSuccess()) {
                user.sendMessage("&cThere was an error: " + response.errorMessage);
                return;
            }
            
            addTransaction(new Transaction(amount, Type.DEPOSIT, user.getUniqueId()));
            user.sendMessage("&aYou deposited $" + amount + " into your kingdom's bank.");
        } else if (Utils.checkCmdAliases(args, 0, "withdraw")) {
            if (!user.hasPermission(Permission.WITHDRAW)) {
                user.sendMessage("&cYou do not have permission to withdraw from the " + holder.getClass().getSimpleName() + "'s bank.");
                return;
            }
            
            if (!(args.length > 1)) {
                user.sendMessage("&cYou must provide an amount.");
                return;
            }
            
            double amount;
            try {
                amount = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                user.sendMessage("The value you provided for the amount is not a valid number");
                return;
            }
            
            if (getBalance() < amount) {
                user.sendMessage("&cThe Kingdom Bank does not have enough money.");
                return;
            }
            
            EconomyResponse response = plugin.getVaultEconomy().depositPlayer(user.getPlayer(), amount);
            if (!response.transactionSuccess()) {
                user.sendMessage("&cThere was an error: " + response.errorMessage);
                return;
            }
            
            addTransaction(new Transaction(amount, Type.WITHDRAWL, user.getUniqueId()));
            user.sendMessage("&aYou withdrew $" + amount + " into your kingdom's bank.");
        } else if (Utils.checkCmdAliases(args, 0, "balance", "bal")) {
            user.sendMessage("&aYour " + holder.getClass().getSimpleName() + " has the balance $" + getBalance());
        } else if (Utils.checkCmdAliases(args, 0, "transactions")) {
            Paginator<Transaction> paginator = PaginatorFactory.generatePaginator("&7List of transactions &e({pagenumber}/{totalpages})", "&7Type /" + cmd.getName() + " transactions page {nextpage} for more.", 7, getTransactions());
            int page = 1;
            if (args.length > 2) {
                if (Utils.checkCmdAliases(args, 1, "page", "p")) {
                    try {
                        page = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        user.sendMessage("&cThe value for the page was not a valid number.");
                        return;
                    }
                }
            }
            
            paginator.display(user.getPlayer(), page);
        }
    }
    
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
    
    public double getBalance() {
        double balance = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals(Type.DEPOSIT)) {
                balance += transaction.getAmount();
            } else if (transaction.getType().equals(Type.WITHDRAWL)) {
                balance -= transaction.getAmount();
            }
        }
        return balance;
    }
    
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("amount", getTransactions().size());
        for (int i = 0; i < getTransactions().size(); i++) {
            serialized.put("transaction" + i, getTransactions().get(i));
        }
        return serialized;
    }
    
    public double getContributionTotal(UUID uuid) {
        double contributions = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getUniqueId().equals(uuid)) {
                if (transaction.getType().equals(Type.DEPOSIT)) {
                    contributions += transaction.getAmount();
                } else if (transaction.getType().equals(Type.WITHDRAWL)) {
                    contributions -= transaction.getAmount();
                }
            }
        }
        return contributions;
    }
    
    public List<Transaction> getTransactions(UUID uuid) {
        return transactions.stream().filter(transaction -> transaction.getUniqueId().equals(uuid)).collect(Collectors.toList());
    }
}