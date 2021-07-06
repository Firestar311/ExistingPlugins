package com.kingrealms.realms.economy.shop.types;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.economy.shop.Shop;
import com.kingrealms.realms.economy.shop.enums.OwnerType;
import com.kingrealms.realms.profile.RealmProfile;

public abstract class PlayerShop extends Shop {
    public PlayerShop(RealmProfile owner) {
        super(owner.getUniqueId().toString());
        this.ownerType = OwnerType.PLAYER;
    }
    
    @Override
    public RealmProfile getOwner() {
        return Realms.getInstance().getProfileManager().getProfile(this.owner);
    }
}