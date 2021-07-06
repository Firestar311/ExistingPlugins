package com.kingrealms.realms;

import com.kingrealms.realms.limits.LimitBoost;
import com.kingrealms.realms.limits.limit.Limit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface IOwner extends ConfigurationSerializable {
    void sendMessage(String message);
    String getName();
    String getIdentifier();
    Number getLimitValue(Limit limit);
    void addLimitBoost(LimitBoost boost);
    LimitBoost getLimitBoost(Limit limit);
}