package com.stardevmc.titaneconomy;

import com.firestar311.lib.player.PlayerManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class TitanEconomy extends JavaPlugin {
    
    private PlayerManager playerManager;
    private AccountManager accountManager;
    
    public void onEnable() {
        INSTANCE = this;
    
        RegisteredServiceProvider<PlayerManager> playerRSP = getServer().getServicesManager().getRegistration(PlayerManager.class);
        if (playerRSP != null) {
            this.playerManager = playerRSP.getProvider();
        } else {
            getLogger().severe("Could not load the PlayerManager from FireLib. Disabling Plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        this.accountManager = new AccountManager();
        this.accountManager.loadData();
        
        TitanVaultEco titanVaultEco = new TitanVaultEco();
        getServer().getServicesManager().register(Economy.class, titanVaultEco, this, ServicePriority.Highest);
        getServer().getServicesManager().register(AccountManager.class, accountManager, this, ServicePriority.Highest);
    }
    
    public void onDisable() {
        this.accountManager.saveData();
    }
    
    public AccountManager getAccountManager() {
        return accountManager;
    }
    
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    private static TitanEconomy INSTANCE;
    
    public static TitanEconomy getInstance() {
        return INSTANCE;
    }
}
