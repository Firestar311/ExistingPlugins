package com.kingrealms.realms.economy.account;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.economy.EconomyResponse;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.territory.base.Territory;
import com.kingrealms.realms.territory.base.member.Member;
import com.kingrealms.realms.territory.enums.Rank;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("TerritoryAccount")
public class TerritoryAccount extends Account {
    
    private Rank minDeposit, minWithdraw; //mysql
    
    public TerritoryAccount(Territory owner, long accountNumber, Rank minDeposit, Rank minWithdraw) {
        super(owner.getUniqueId(), accountNumber);
        this.minDeposit = minDeposit;
        this.minWithdraw = minWithdraw;
    }
    
    public TerritoryAccount(Map<String, Object> serialized) {
        super(serialized);
        this.minDeposit = Rank.valueOf((String) serialized.get("minDeposit"));
        this.minWithdraw = Rank.valueOf((String) serialized.get("minWithdraw"));
    }
    
    @Override
    public Territory getOwner() {
        return Realms.getInstance().getTerritoryManager().getTerritory(this.owner);
    }
    
    @Override
    protected EconomyResponse canDeposit(String actor, double amount) {
        RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(actor);
        Territory owner = getOwner();
        Member member = owner.getMember(profile.getUniqueId());
        if (member != null) {
            if (member.getRank().getOrder() > minDeposit.getOrder()) {
                return EconomyResponse.NOT_ENOUGH_PERMISSION;
            }
        } else {
            if (!profile.hasPermission("realms.admin.forcedeposit")) {
                return EconomyResponse.NOT_A_MEMBER;
            }
        }
        
        return EconomyResponse.SUCCESS;
    }
    
    @Override
    protected EconomyResponse canWithdraw(String actor, double amount) {
        RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(actor);
        Territory owner = getOwner();
    
        Member member = owner.getMember(profile.getUniqueId());
        if (member != null) {
            if (member.getRank().getOrder() > minWithdraw.getOrder()) {
                return EconomyResponse.NOT_ENOUGH_PERMISSION;
            }
        } else {
            if (!profile.hasPermission("realms.admin.forcewithdraw")) {
                return EconomyResponse.NOT_ENOUGH_PERMISSION;
            }
        }
    
        double balance = getBalance();
        if (amount > balance) {
            return EconomyResponse.NOT_ENOUGH_FUNDS;
        }
    
        return EconomyResponse.SUCCESS;
    }
    
    @Override
    public void sendMessageToOwner(String message) {
        getOwner().sendMemberMessage(message);
    }
    
    public Rank getMinWithdraw() {
        return minWithdraw;
    }
    
    public void setMinWithdraw(Rank minWithdraw) {
        this.minWithdraw = minWithdraw;
    }
    
    public Rank getMinDeposit() {
        return minDeposit;
    }
    
    public void setMinDeposit(Rank minDeposit) {
        this.minDeposit = minDeposit;
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>(super.serialize()) {{
            put("minWithdraw", minWithdraw.name());
            put("minDeposit", minDeposit.name());
        }};
    }
    
    @Override
    public Map<String, String> getDisplayMap() {
        return new TreeMap<>(super.getDisplayMap()) {{
            put("Minimum Deposit", minDeposit.name());
            put("Minimum Withdraw", minWithdraw.name());
        }};
    }
}