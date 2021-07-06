package com.kingrealms.realms.limits.group;

import com.kingrealms.realms.limits.limit.IntegerLimit;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("PlayerLimits")
public class PlayerLimits extends LimitGroup {
    public PlayerLimits() {
        super("player_limits", "Player Limits", "All limits specific to players.");
    }
    
    @Override
    public void createDefaultLimits() {
        addLimit(new IntegerLimit("Player Home Limit", "Maximum amount of homes that a player can have.", "player_home_limit", 10));
    }
}