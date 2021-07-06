package com.kingrealms.realms.economy.shop.types;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.economy.shop.Shop;
import com.kingrealms.realms.economy.shop.enums.OwnerType;
import com.kingrealms.realms.territory.base.Territory;

public abstract class TerritoryShop extends Shop {
    public TerritoryShop(Territory owner) {
        super(owner.getUniqueId());
        this.ownerType = OwnerType.TERRITORY;
    }
    
    @Override
    public Territory getOwner() {
        return Realms.getInstance().getTerritoryManager().getTerritory(this.owner);
    }
}