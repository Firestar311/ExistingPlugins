package com.stardevmc.titaneconomy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class TitanVaultEco implements Economy {
    
    private TitanEconomy plugin = TitanEconomy.getInstance();
    
    public boolean isEnabled() {
        return true;
    }
    
    public String getName() {
        return "TitanEconomy";
    }
    
    public boolean hasBankSupport() {
        return false;
    }
    
    public int fractionalDigits() {
        return 2;
    }
    
    public String format(double v) {
        return "$" + v;
    }
    
    public String currencyNamePlural() {
        return "Dollars";
    }
    
    public String currencyNameSingular() {
        return "Dollar";
    }
    
    public boolean hasAccount(String s) {
        throw new UnsupportedOperationException("This method is not supported, please update.");
    }
    
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return plugin.getAccountManager().hasAccount(offlinePlayer.getUniqueId());
    }
    
    public boolean hasAccount(String s, String s1) {
        throw new UnsupportedOperationException("This method is not supported, please update.");
    }
    
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return hasAccount(offlinePlayer);
    }
    
    public double getBalance(String s) {
        throw new UnsupportedOperationException("This method is not supported, please update.");
    }
    
    public double getBalance(OfflinePlayer offlinePlayer) {
        Account account = plugin.getAccountManager().getAccount(offlinePlayer.getUniqueId());
        return account.getBalance();
    }
    
    public double getBalance(String s, String s1) {
        throw new UnsupportedOperationException("This method is not supported, please update.");
    }
    
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(offlinePlayer);
    }
    
    public boolean has(String s, double v) {
        throw new UnsupportedOperationException("This method is not supported, please update.");
    }
    
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        double balance = getBalance(offlinePlayer);
        return balance >= v;
    }
    
    public boolean has(String s, String s1, double v) {
        throw new UnsupportedOperationException("This method is not supported, please update.");
    }
    
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return has(offlinePlayer, v);
    }
    
    public EconomyResponse withdrawPlayer(String s, double v) {
        throw new UnsupportedOperationException("This method is not supported, please update.");
    }
    
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        plugin.getAccountManager().withdraw(offlinePlayer.getUniqueId(), v);
        return new EconomyResponse(v, getBalance(offlinePlayer), ResponseType.SUCCESS, "");
    }
    
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        throw new UnsupportedOperationException("This method is not supported, please update.");
    }
    
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return withdrawPlayer(offlinePlayer, v);
    }
    
    public EconomyResponse depositPlayer(String s, double v) {
        throw new UnsupportedOperationException("This method is not supported, please update.");
    }
    
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        plugin.getAccountManager().deposit(offlinePlayer.getUniqueId(), v);
        return new EconomyResponse(v, getBalance(offlinePlayer), ResponseType.SUCCESS, "");
    }
    
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        throw new UnsupportedOperationException("This method is not supported, please update.");
    }
    
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return depositPlayer(offlinePlayer, v);
    }
    
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Unused Method");
    }
    
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Unused Method");
    }
    
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Unused Method");
    }
    
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Unused Method");
    }
    
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Unused Method");
    }
    
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Unused Method");
    }
    
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Unused Method");
    }
    
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Unused Method");
    }
    
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Unused Method");
    }
    
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Unused Method");
    }
    
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Unused Method");
    }
    
    public List<String> getBanks() {
        throw new UnsupportedOperationException("This method is not supported, please update.");
    }
    
    public boolean createPlayerAccount(String s) {
        throw new UnsupportedOperationException("This method is not supported, please update.");
    }
    
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        throw new UnsupportedOperationException("This method is not supported, please update.");
    }
    
    public boolean createPlayerAccount(String s, String s1) {
        throw new UnsupportedOperationException("This method is not supported, please update.");
    }
    
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        throw new UnsupportedOperationException("This method is not supported, please update.");
    }
}