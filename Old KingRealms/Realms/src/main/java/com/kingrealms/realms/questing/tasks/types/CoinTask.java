package com.kingrealms.realms.questing.tasks.types;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.api.events.TransactionEvent;
import com.kingrealms.realms.economy.account.Account;
import com.kingrealms.realms.economy.account.PlayerAccount;
import com.kingrealms.realms.economy.transaction.Transaction;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.tasks.Task;
import com.starmediadev.lib.util.ID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

public class CoinTask extends Task {
    
    private double coinAmount;
    
    public CoinTask(ID id, ID questId, String name, String description, double coinAmount) {
        this(id, questId, name);
        this.description = description;
        this.coinAmount = coinAmount;
    }
    
    public CoinTask(ID id, ID questId, String name) {
        super(id, questId, name);
        
        new BukkitRunnable() {
            public void run() {
                if (!Realms.getInstance().getSeason().isActive()) { return; }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(p);
                    if (!profile.isTaskComplete(getQuest().getId(), getId())) {
                        if (profile.getAccount().getBalance() >= coinAmount) { onComplete(profile); }
                    }
                }
            }
        }.runTaskTimer(Realms.getInstance(), 20L, 20L);
    }
    
    @EventHandler
    public void onTransaction(TransactionEvent e) {
        Transaction transaction = e.getTransaction();
        Account fromAccount = Realms.getInstance().getEconomyManager().getAccountHandler().getAccount(transaction.getFromAccount());
        Account toAccount = Realms.getInstance().getEconomyManager().getAccountHandler().getAccount(transaction.getFromAccount());
        if (fromAccount instanceof PlayerAccount) {
            PlayerAccount playerAccount = (PlayerAccount) fromAccount;
            if (!playerAccount.getOwner().isTaskComplete(getQuest().getId(), getId())) {
                if (playerAccount.getOwner().getAccount().getBalance() >= coinAmount) { onComplete(playerAccount.getOwner()); }
            }
        }
    
        if (toAccount instanceof PlayerAccount) {
            PlayerAccount playerAccount = (PlayerAccount) toAccount;
            if (!playerAccount.getOwner().isTaskComplete(getQuest().getId(), getId())) {
                if (playerAccount.getOwner().getAccount().getBalance() >= coinAmount) { onComplete(playerAccount.getOwner()); }
            }
        }
    }
}