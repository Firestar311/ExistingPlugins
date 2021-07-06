package com.kingrealms.realms.economy.account;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.territory.base.Territory;
import com.kingrealms.realms.territory.enums.Rank;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class AccountHandler {
    private static final int ACCT_NUMBER_CHAR_COUNT = 12;
    private final SortedMap<Long, Account> accounts = new TreeMap<>();
    private ServerAccount mainServerAccount;
    
    public AccountHandler() {
        createMainServerAccount();
    }
    
    protected void createMainServerAccount() {
        if (mainServerAccount == null) {
            this.mainServerAccount = new ServerAccount(-1);
            this.mainServerAccount.setInfiniteBalance(true);
            this.accounts.put(mainServerAccount.getAccountNumber(), mainServerAccount);
        }
    }
    
    public void saveData(ConfigurationSection section) {
        for (Account account : getAccounts()) {
            section.set("" + account.getAccountNumber(), account);
        }
    }
    
    public Set<Account> getAccounts() {
        return new HashSet<>(this.accounts.values());
    }
    
    public void loadData(ConfigurationSection section) {
        if (section != null) {
            for (String a : section.getKeys(true)) {
                Account account = (Account) section.get(a);
                this.accounts.put(account.getAccountNumber(), account);
                if (account instanceof ServerAccount) {
                    if (account.getAccountNumber() == -1) {
                        this.mainServerAccount = (ServerAccount) account;
                    }
                }
            }
        }
    }
    
    public ServerAccount getMainServerAccount() {
        createMainServerAccount();
        return mainServerAccount;
    }
    
    public Account createAccount(CommandSender sender) {
        Account account = null;
        if (sender instanceof Player) {
            RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(sender);
            account = new PlayerAccount(profile, generateAccountNumber());
        } else if (sender instanceof ConsoleCommandSender) {
            account = new ServerAccount(generateAccountNumber());
        }
        
        if (account != null) {
            this.accounts.put(account.getAccountNumber(), account);
            return account;
        }
        return account;
    }
    
    public long generateAccountNumber() {
        long accountNumber;
        Random random = new Random();
        do {
            String[] rawAcctNumber = new String[ACCT_NUMBER_CHAR_COUNT];
            for (int i = 0; i < rawAcctNumber.length; i++) {
                int a;
                if (i == 0) {
                    a = random.nextInt(9) + 1;
                } else {
                    a = random.nextInt(10);
                }
                rawAcctNumber[i] = a + "";
            }
            
            StringBuilder sb = new StringBuilder();
            for (String r : rawAcctNumber) {
                sb.append(r);
            }
            
            accountNumber = Long.parseLong(sb.toString());
        } while (accounts.containsKey(accountNumber));
        return accountNumber;
    }
    
    public Account createAccount(Territory territory, Rank minDeposit, Rank minWithdraw) {
        Account account = new TerritoryAccount(territory, generateAccountNumber(), minDeposit, minWithdraw);
        this.accounts.put(account.getAccountNumber(), account);
        return account;
    }
    
    public Account createAccount(RealmProfile profile) {
        Account account = new PlayerAccount(profile, generateAccountNumber());
        this.accounts.put(account.getAccountNumber(), account);
        return account;
    }
    
    public Account getAccount(String raw) {
        Account account = null;
        try {
            long an = Long.parseLong(raw);
            account = getAccount(an);
        } catch (NumberFormatException e) {}
        
        if (account == null) {
            RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(raw);
            if (profile != null) {
                account = profile.getAccount();
            }
        }
        
        if (account == null) {
            Territory territory = Realms.getInstance().getTerritoryManager().getTerritory(raw.toLowerCase().replace("_", " "));
            if (territory != null) {
                account = territory.getAccount();
            }
        }
        return account;
    }
    
    public Account getAccount(long accountNumber) {
        return this.accounts.get(accountNumber);
    }
}