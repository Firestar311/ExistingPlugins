package com.kingrealms.realms.limits.group;

import com.kingrealms.realms.limits.limit.IntegerLimit;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("TerritoryLimits")
public class TerritoryLimits extends LimitGroup {
    public TerritoryLimits() {
        super("territory_limits", "Territory Limits", "Limits for the territories");
    }
    
    @Override
    public void createDefaultLimits() {
        addLimit(new IntegerLimit("Territory Claim Limit", "Maxiumum number of claims allowed per territory.", "territory_claim_limit", 50));
        addLimit(new IntegerLimit("Territory Member Limit", "Maxiumum number of members allowed per territory.", "territory_member_limit", 10));
        addLimit(new IntegerLimit("Territory Warp Limit", "Maxiumum number of warps allowed per territory.", "territory_warp_limit", 5));
    }
}