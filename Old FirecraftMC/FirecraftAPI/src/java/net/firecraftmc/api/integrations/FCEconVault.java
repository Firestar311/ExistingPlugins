package net.firecraftmc.api.integrations;

import net.firecraftmc.api.interfaces.IEconomyManager;
import net.firecraftmc.api.model.Transaction;
import net.firecraftmc.api.plugin.IFirecraftCore;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;

import java.util.List;

@SuppressWarnings("SameReturnValue")
public class FCEconVault implements Economy {
    final IFirecraftCore plugin;
    public FCEconVault(IFirecraftCore plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return true;
    }

    public String getName() {
        return "Firecraft Economy";
    }

    public boolean hasBankSupport() {
        return false;
    }

    public int fractionalDigits() {
        return 0;
    }

    public String format(double v) {
        return "$" + v;
    }

    public String currencyNamePlural() {
        return "dollars";
    }

    public String currencyNameSingular() {
        return "dollar";
    }

    public boolean hasAccount(String s) {
        return true;
    }

    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return true;
    }

    public boolean hasAccount(String s, String s1) {
        return true;
    }

    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return true;
    }

    public double getBalance(String s) {
        return plugin.getPlayerManager().getPlayer(s).getBalance();
    }

    public double getBalance(OfflinePlayer offlinePlayer) {
        return plugin.getPlayerManager().getPlayer(offlinePlayer.getUniqueId()).getBalance();
    }

    public double getBalance(String s, String s1) {
        return plugin.getPlayerManager().getPlayer(s).getBalance();
    }

    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return plugin.getPlayerManager().getPlayer(offlinePlayer.getUniqueId()).getBalance();
    }

    public boolean has(String s, double v) {
        return plugin.getPlayerManager().getPlayer(s).getBalance() >= v;
    }

    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return plugin.getPlayerManager().getPlayer(offlinePlayer.getUniqueId()).getBalance() >= v;
    }

    public boolean has(String s, String s1, double v) {
        return plugin.getPlayerManager().getPlayer(s).getBalance() >= v;
    }

    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return plugin.getPlayerManager().getPlayer(offlinePlayer.getUniqueId()).getBalance() >= v;
    }

    public EconomyResponse withdrawPlayer(String s, double v) {
        Transaction transaction = IEconomyManager.withdraw(plugin.getPlayerManager().getPlayer(s).getProfile(), v);
        plugin.getFCDatabase().saveTransaction(transaction);
        return new EconomyResponse(v, plugin.getPlayerManager().getPlayer(s).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        Transaction transaction =  IEconomyManager.withdraw(plugin.getPlayerManager().getPlayer(offlinePlayer.getUniqueId()).getProfile(), v);
        plugin.getFCDatabase().saveTransaction(transaction);
        return new EconomyResponse(v, plugin.getPlayerManager().getPlayer(offlinePlayer.getUniqueId()).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        Transaction transaction = IEconomyManager.withdraw(plugin.getPlayerManager().getPlayer(s).getProfile(), v);
        plugin.getFCDatabase().saveTransaction(transaction);
        return new EconomyResponse(v, plugin.getPlayerManager().getPlayer(s).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        Transaction transaction =  IEconomyManager.withdraw(plugin.getPlayerManager().getPlayer(offlinePlayer.getUniqueId()).getProfile(), v);
        plugin.getFCDatabase().saveTransaction(transaction);
        return new EconomyResponse(v, plugin.getPlayerManager().getPlayer(offlinePlayer.getUniqueId()).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse depositPlayer(String s, double v) {
        Transaction transaction = IEconomyManager.deposit(plugin.getPlayerManager().getPlayer(s).getProfile(), v);
        plugin.getFCDatabase().saveTransaction(transaction);
        return new EconomyResponse(v, plugin.getPlayerManager().getPlayer(s).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        Transaction transaction = IEconomyManager.deposit(plugin.getPlayerManager().getPlayer(offlinePlayer.getUniqueId()).getProfile(), v);
        plugin.getFCDatabase().saveTransaction(transaction);
        return new EconomyResponse(v, plugin.getPlayerManager().getPlayer(offlinePlayer.getUniqueId()).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse depositPlayer(String s, String s1, double v) {
        Transaction transaction = IEconomyManager.deposit(plugin.getPlayerManager().getPlayer(s).getProfile(), v);
        plugin.getFCDatabase().saveTransaction(transaction);
        return new EconomyResponse(v, plugin.getPlayerManager().getPlayer(s).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        Transaction transaction = IEconomyManager.deposit(plugin.getPlayerManager().getPlayer(offlinePlayer.getUniqueId()).getProfile(), v);
        plugin.getFCDatabase().saveTransaction(transaction);
        return new EconomyResponse(v, plugin.getPlayerManager().getPlayer(offlinePlayer.getUniqueId()).getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks do not exist");
    }

    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks do not exist");
    }

    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks do not exist");
    }

    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks do not exist");
    }

    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks do not exist");
    }

    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks do not exist");
    }

    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks do not exist");
    }

    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks do not exist");
    }

    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks do not exist");
    }

    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks do not exist");
    }

    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks do not exist");
    }

    public List<String> getBanks() {
        return null;
    }

    public boolean createPlayerAccount(String s) {
        return false;
    }

    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }

    public void registerServices() {
        Vault vault = (Vault) Bukkit.getPluginManager().getPlugin("Vault");
        if (vault != null) {
            Bukkit.getServicesManager().register(Economy.class, this, vault, ServicePriority.Highest);
        }
    }
}