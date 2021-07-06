package net.firecraftmc.api.database;

import net.firecraftmc.api.enums.TransactionType;
import net.firecraftmc.api.model.Transaction;

import java.sql.*;
import java.util.*;

public class EconomyDatabase extends Database {
    
    public EconomyDatabase(String user, String database, String password, int port, String hostname) {
        super(user, database, password, port, hostname);
    }
    
    public void modifyDatabase() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `{database}.transactions` (`id` INT NOT NULL AUTO_INCREMENT, `date` VARCHAR(20) NOT NULL, `player` VARCHAR(36) NOT NULL, `amount` VARCHAR(30) NOT NULL, `admin` VARCHAR(36), `target` VARCHAR(64),  `ecoticketid` VARCHAR(64))");
            statement.executeUpdate("ALTER TABLE `transactions` ADD PRIMARY KEY(`id`);");
            statement.close();
        } catch (Exception e) {
        }
    }
    
    public List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery("SELECT * FROM `transactions`;");
            while (set.next()) {
                long date = set.getLong("date");
                UUID player = UUID.fromString(set.getString("player"));
                TransactionType type = TransactionType.valueOf(set.getString("type"));
                double amount = set.getDouble("amount");
                UUID admin = null, target = null;
                
                try {
                    admin = UUID.fromString(set.getString("admin"));
                } catch (Exception e) {
                }
                
                try {
                    target = UUID.fromString(set.getString("target"));
                } catch (Exception e) {
                }
                
                String ticketid = set.getString("ecoticketid");
                
                Transaction transaction = new Transaction(player, type, amount, date);
                if (admin != null) {
                    transaction.setAdmin(admin);
                    if (target != null) transaction.setTarget(target);
                }
                if (ticketid != null) transaction.setEcoTicketId(ticketid);
                
                transactions.add(transaction);
            }
            statement.close();
        } catch (Exception e) {
        }
        
        return transactions;
    }
    
    public List<Transaction> getTransactions(UUID player) {
        List<Transaction> transactions = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `transactions` WHERE `player` = ?");
            statement.setString(1, player.toString());
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                long date = set.getLong("date");
                TransactionType type = TransactionType.valueOf(set.getString("type"));
                double amount = set.getDouble("amount");
                UUID admin = null, target = null;
                
                try {
                    admin = UUID.fromString(set.getString("admin"));
                } catch (Exception e) {
                }
                
                try {
                    target = UUID.fromString(set.getString("target"));
                } catch (Exception e) {
                }
                
                String ticketid = set.getString("ecoticketid");
                
                Transaction transaction = new Transaction(player, type, amount, date);
                if (admin != null) transaction.setAdmin(admin);
                if (target != null) transaction.setTarget(target);
                if (ticketid != null) transaction.setEcoTicketId(ticketid);
                
                transactions.add(transaction);
            }
            statement.close();
        } catch (Exception e) {
        }
        
        return transactions;
    }
    
    public void saveTransaction(Transaction transaction) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `transactions`(`date`, `player`, `type`, `amount`, `admin`, `ecoticketid`) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, transaction.getDate() + "");
            statement.setString(2, transaction.getPlayer().toString());
            statement.setString(3, transaction.getType().toString());
            statement.setString(4, transaction.getAmount() + "");
            statement.setString(5, (transaction.getAdmin() == null) ? "" : transaction.getAdmin().toString());
            statement.setString(6, (transaction.getTarget() == null) ? "" : transaction.getTarget().toString());
            statement.setString(7, (transaction.getEcoTicketId() == null) ? "" : transaction.getEcoTicketId());
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {}
    }
}
