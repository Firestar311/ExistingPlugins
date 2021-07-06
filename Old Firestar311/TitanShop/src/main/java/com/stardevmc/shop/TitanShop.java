package com.stardevmc.shop;

import com.firestar311.lib.gui.PaginatedGUI;
import com.firestar311.lib.player.PlayerManager;
import com.stardevmc.shop.cmds.*;
import com.stardevmc.shop.manager.ItemManager;
import com.stardevmc.shop.manager.ShopManager;
import com.stardevmc.shop.worth.WorthManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class TitanShop extends JavaPlugin {
    
    private static TitanShop instance;
    
    private ShopManager shopManager;
    private ItemManager itemManager;
    private WorthManager worthManager;
    
    private PlayerManager playerManager;
    
    private Economy economy;
    
    private UUID serverUUID;
    
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        PaginatedGUI.prepare(this);
        this.itemManager = new ItemManager(this);
        this.itemManager.loadItems();
        this.shopManager = new ShopManager(this);
        this.shopManager.loadShops();
        this.worthManager = new WorthManager(this);
        this.worthManager.loadData();
        
        RegisteredServiceProvider<PlayerManager> playerRsp = Bukkit.getServer().getServicesManager().getRegistration(PlayerManager.class);
        if (playerRsp != null) {
            playerManager = playerRsp.getProvider();
        }
        
        if (!this.getConfig().contains("serveruuid")) {
            do {
                serverUUID = UUID.randomUUID();
            } while (playerManager.getUser(serverUUID) != null);
            getConfig().set("serveruuid", serverUUID.toString());
            saveConfig();
        } else {
            this.serverUUID = UUID.fromString(getConfig().getString("serveruuid"));
        }
        
        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("shopadmin").setExecutor(new ShopAdminCommand(this));
        getCommand("sell").setExecutor(new SellCommand());
        getCommand("signshop").setExecutor(new SignShopCommand(this));
        getCommand("shopitem").setExecutor(new ShopItemCommand());
        getCommand("worth").setExecutor(new WorthCommand(this));
        
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        } else {
            getLogger().info("Could not find a Vault Economy Provider, disabling plugin");
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    public void onDisable() {
        this.itemManager.saveItems();
        this.shopManager.saveShops();
    }
    
    public ShopManager getShopManager() {
        return shopManager;
    }
    
    public static TitanShop getInstance() {
        return instance;
    }
    
    public Economy getEconomy() {
        return economy;
    }
    
    public ItemManager getItemManager() {
        return itemManager;
    }
    
    public UUID getServerUUID() {
        return serverUUID;
    }
    
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    public WorthManager getWorthManager() {
        return worthManager;
    }
}