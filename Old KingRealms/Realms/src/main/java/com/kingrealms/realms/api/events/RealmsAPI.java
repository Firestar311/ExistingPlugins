package com.kingrealms.realms.api.events;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.economy.EconomyResponse;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.Pair;
import org.bukkit.command.CommandSender;

public final class RealmsAPI {
    private RealmsAPI() {}
    
    private static Realms getInstance() {
        return Realms.getInstance();
    }
    
    public static RealmProfile getProfile(CommandSender sender) {
        return getInstance().getProfileManager().getProfile(sender);
    }
    
    public static Pair<EconomyResponse, EconomyResponse> withdrawPlayer(RealmProfile profile, double amount, String description) {
        return getInstance().getEconomyManager().getTransactionHandler().withdraw(profile, amount, profile.getAccount(), description);
    }
}