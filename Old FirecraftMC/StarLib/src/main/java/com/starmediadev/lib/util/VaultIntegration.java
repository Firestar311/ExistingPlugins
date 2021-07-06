package com.starmediadev.lib.util;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultIntegration {
    
    private Permission permission;
    private Chat chat;
    private Economy economy;
    
    public VaultIntegration() {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            this.permission = permissionProvider.getProvider();
        }
        
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            this.chat = chatProvider.getProvider();
        }
        
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            this.economy = economyProvider.getProvider();
        }
    }
    
    public Permission getPermission() {
        return permission;
    }
    
    public boolean hasPermissionHook() {
        return permission != null;
    }
    
    public Chat getChat() {
        return chat;
    }
    
    public boolean hasChatHook() {
        return chat != null;
    }
    
    public Economy getEconomy() {
        return economy;
    }
    
    public boolean hasEconomyHook() {
        return economy != null;
    }
}