package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.questing.rewards.MoneyReward;
import com.kingrealms.realms.questing.tasks.types.CoinTask;
import com.starmediadev.lib.util.ID;

public class ObtainOneThousandCoins extends Quest {
    public ObtainOneThousandCoins() {
        super("Sell 1,000 coins worth of items", new ID("one_thousand_coins"));
        
        addTask(new CoinTask(new ID("thousand_coin_task"), getId(), "1,000 coins", "Have 1,000 coins in your account", 1000));
        addReward(new MoneyReward("1,000 Coins", 1000));
    }
}