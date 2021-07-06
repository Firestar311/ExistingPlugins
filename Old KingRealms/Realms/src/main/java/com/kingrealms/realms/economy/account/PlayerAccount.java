package com.kingrealms.realms.economy.account;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.economy.EconomyResponse;
import com.kingrealms.realms.profile.RealmProfile;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("PlayerAccount")
public class PlayerAccount extends Account {
    private RealmProfile profile; //cache
    
    public PlayerAccount(RealmProfile owner, long accountNumber) {
        super(owner.getIdentifier(), accountNumber);
    }
    
    public PlayerAccount(Map<String, Object> serialized) {
        super(serialized);
    }
    
    @Override
    public RealmProfile getOwner() {
        if (this.profile == null) {
            this.profile = Realms.getInstance().getProfileManager().getProfile(this.owner);
        }
        
        return this.profile;
    }
    
    @Override
    protected EconomyResponse canDeposit(String actor, double amount) {
        return EconomyResponse.SUCCESS;
    }
    
    @Override
    protected EconomyResponse canWithdraw(String actor, double amount) {
        double balance = getBalance();
        if (amount > balance) {
            return EconomyResponse.NOT_ENOUGH_FUNDS;
        }
        
        return EconomyResponse.SUCCESS;
    }
}