package com.kingrealms.realms.economy.account;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.economy.EconomyResponse;
import com.kingrealms.realms.profile.ServerProfile;
import com.starmediadev.lib.util.Constants;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("ServerAccount")
public class ServerAccount extends Account {
    
    private boolean infiniteBalance = false; //mysql
    private double maxBalance = 0; //mysql
    
    public ServerAccount(long accountNumber) {
        super("Console", accountNumber);
    }
    
    public ServerAccount(Map<String, Object> serialized) {
        super(serialized);
        this.infiniteBalance = (boolean) serialized.get("infiniteBalance");
        this.maxBalance = Double.parseDouble((String) serialized.get("maxBalance"));
    }
    
    @Override
    public ServerProfile getOwner() {
        return (ServerProfile) Realms.getInstance().getProfileManager().getProfile("Console");
    }
    
    @Override
    protected EconomyResponse canDeposit(String actor, double amount) {
        if (hasInfiniteBalance()) {
            return EconomyResponse.SUCCESS;
        }
        
        double balance = getBalance();
        
        if ((balance + amount) > maxBalance) {
            return EconomyResponse.MAX_BALANCE_REACHED;
        }
        
        return EconomyResponse.SUCCESS;
    }
    
    public boolean hasInfiniteBalance() {
        return infiniteBalance;
    }
    
    @Override
    protected EconomyResponse canWithdraw(String actor, double amount) {
        if (hasInfiniteBalance()) {
            return EconomyResponse.SUCCESS;
        }
        
        double balance = getBalance();
        if (amount > balance) {
            return EconomyResponse.NOT_ENOUGH_FUNDS;
        }
        
        return EconomyResponse.SUCCESS;
    }
    
    @Override
    public void sendMessageToOwner(String message) {
        ServerProfile profile = (ServerProfile) Realms.getInstance().getProfileManager().getProfile("console");
        profile.sendMessage(message);
    }
    
    public double getMaxBalance() {
        return maxBalance;
    }
    
    public void setMaxBalance(double maxBalance) {
        this.maxBalance = maxBalance;
    }
    
    public void setInfiniteBalance(boolean infiniteBalance) {
        this.infiniteBalance = infiniteBalance;
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>(super.serialize()) {{
            put("infiniteBalance", infiniteBalance);
            put("maxBalance", maxBalance + "");
        }};
    }
    
    @Override
    public Map<String, String> getDisplayMap() {
        return new TreeMap<>(super.getDisplayMap()) {{
            put("Infinite Balance", infiniteBalance + "");
            put("Max Balance", maxBalance + "");
        }};
    }
    
    @Override
    public String formatLine(String... args) {
        String owner = "Console";
        String date = Constants.DATE_FORMAT.format(new Date(this.created));
        return " &8- &a" + accountNumber + "&f: &e" + owner + " &fcreated on &d" + date;
    }
}