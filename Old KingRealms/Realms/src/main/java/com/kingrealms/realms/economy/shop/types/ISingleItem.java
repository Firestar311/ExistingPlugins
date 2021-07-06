package com.kingrealms.realms.economy.shop.types;

import com.kingrealms.realms.economy.shop.item.ShopItem;
import com.kingrealms.realms.profile.RealmProfile;

public interface ISingleItem {
    
    ShopItem getItem();
    void setItem(ShopItem item);
    void buyItem(RealmProfile buyer, int amount);
    void sellItem(RealmProfile seller);
}