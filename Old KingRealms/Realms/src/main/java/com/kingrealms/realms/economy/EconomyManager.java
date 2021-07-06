package com.kingrealms.realms.economy;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.economy.account.AccountHandler;
import com.kingrealms.realms.economy.shop.ShopHandler;
import com.kingrealms.realms.economy.tickets.TicketHandler;
import com.kingrealms.realms.economy.transaction.TransactionHandler;
import com.kingrealms.realms.storage.StorageManager;
import com.starmediadev.lib.config.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

public class EconomyManager {
    private final AccountHandler accountHandler;
    private final ConfigManager accountsConfig = StorageManager.accountsConfig, transactionsConfig = StorageManager.transactionsConfig, 
            shopsConfig = StorageManager.shopsConfig, ticketsConfig = StorageManager.ticketsConfig;
    private final File economyFolder = new File(Realms.getInstance().getDataFolder() + File.separator + "economy");
    private final ShopHandler shopHandler;
    private final TicketHandler ticketHandler;
    private final TransactionHandler transactionHandler;
    
    public EconomyManager() {
        if (!economyFolder.exists()) {
            economyFolder.mkdirs();
        }
        
        this.accountsConfig.setup();
        this.transactionsConfig.setup();
        this.shopsConfig.setup();
        this.ticketsConfig.setup();
        
        this.accountHandler = new AccountHandler();
        this.transactionHandler = new TransactionHandler();
        this.shopHandler = new ShopHandler();
        this.ticketHandler = new TicketHandler();
    }
    
    public void saveData() {
        ConfigurationSection transactionsSection = this.transactionsConfig.getConfig().getConfigurationSection("transactions");
        if (transactionsSection == null) {
            transactionsSection = this.transactionsConfig.getConfig().createSection("transactions");
        }
        
        transactionHandler.saveData(transactionsSection);
        
        ConfigurationSection accountSections = this.accountsConfig.getConfig().getConfigurationSection("accounts");
        if (accountSections == null) {
            accountSections = this.accountsConfig.getConfig().createSection("accounts");
        }
        
        accountHandler.saveData(accountSections);
        
        ConfigurationSection shopsSection = this.shopsConfig.getConfig().getConfigurationSection("shops");
        if (shopsSection == null) {
            shopsSection = this.shopsConfig.getConfig().createSection("shops");
        }
        
        shopHandler.saveData(shopsSection);
    
        ConfigurationSection ticketsSection = this.ticketsConfig.getConfig().getConfigurationSection("tickets");
        if (ticketsSection == null) {
            ticketsSection = this.ticketsConfig.getConfig().createSection("tickets");
        }
    
        ticketHandler.saveData(ticketsSection);
        
        this.accountsConfig.saveConfig();
        this.transactionsConfig.saveConfig();
        this.shopsConfig.saveConfig();
        this.ticketsConfig.saveConfig();
    }
    
    public void loadData() {
        this.accountHandler.loadData(this.accountsConfig.getConfig().getConfigurationSection("accounts"));
        this.transactionHandler.loadData(this.transactionsConfig.getConfig().getConfigurationSection("transactions"));
        this.shopHandler.loadData(this.shopsConfig.getConfig().getConfigurationSection("shops"));
        this.ticketHandler.loadData(this.ticketsConfig.getConfig().getConfigurationSection("tickets"));
    }
    
    public AccountHandler getAccountHandler() {
        return accountHandler;
    }
    
    public TransactionHandler getTransactionHandler() {
        return transactionHandler;
    }
    
    public File getEconomyFolder() {
        return economyFolder;
    }
    
    public ShopHandler getShopHandler() {
        return shopHandler;
    }
    
    public TicketHandler getTicketHandler() {
        return ticketHandler;
    }
}