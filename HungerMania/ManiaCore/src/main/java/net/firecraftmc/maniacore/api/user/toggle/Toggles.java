package net.firecraftmc.maniacore.api.user.toggle;

import net.firecraftmc.maniacore.api.ranks.Rank;

import java.util.UUID;

public enum Toggles {
    
    VANISHED("false"), PRIVATE_MESSAGES("true", "messages"), INCOGNITO("false"), STAFF_NOTIFICATIONS("true", "staff", net.firecraftmc.maniacore.api.ranks.Rank.HELPER), ADMIN_NOTIFICATIONS("true", "admin", net.firecraftmc.maniacore.api.ranks.Rank.ADMIN), SPARTAN_NOTIFICATIONS("true", "spartan", net.firecraftmc.maniacore.api.ranks.Rank.HELPER), FRIEND_REQUESTS("true", "requests");
    
    private String defaultValue, cmdName;
    private net.firecraftmc.maniacore.api.ranks.Rank rank = net.firecraftmc.maniacore.api.ranks.Rank.DEFAULT;
    
    Toggles(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    Toggles(String defaultValue, net.firecraftmc.maniacore.api.ranks.Rank rank) {
        this.defaultValue = defaultValue;
        this.rank = rank;
    }
    
    Toggles(String defaultValue, String cmdName) {
        this.defaultValue = defaultValue;
        this.cmdName = cmdName;
    }
    
    Toggles(String defaultValue, String cmdName, net.firecraftmc.maniacore.api.ranks.Rank rank) {
        this.defaultValue = defaultValue;
        this.cmdName = cmdName;
        this.rank = rank;
    }
    
    public String getCmdName() {
        return cmdName;
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public net.firecraftmc.maniacore.api.user.toggle.Toggle create(UUID uuid) {
        return new Toggle(uuid, name().toLowerCase(), defaultValue, defaultValue);
    }
}
