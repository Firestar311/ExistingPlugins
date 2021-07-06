package com.stardevmc.titaneconomy;

import com.firestar311.lib.config.ConfigManager;

import java.util.*;

public class AccountManager {

    private TitanEconomy plugin = TitanEconomy.getInstance();
    private Map<UUID, Account> accounts = new HashMap<>();
    private ConfigManager configManager;
    
    public AccountManager() {
        this.configManager = new ConfigManager(plugin, "accounts");
        this.configManager.setup();
    }
    
    public void loadData() {
    
    }
    
    public void saveData() {
    
    }
    
    public boolean hasAccount(UUID uniqueId) {
        return this.accounts.containsKey(uniqueId);
    }
    
    public Account getAccount(UUID uniqueId) {
        return accounts.get(uniqueId);
    }
    
    public void withdraw(UUID uniqueId, double amount) {
    
    }
    
    public void deposit(UUID uniqueId, double amount) {
    
    }
}