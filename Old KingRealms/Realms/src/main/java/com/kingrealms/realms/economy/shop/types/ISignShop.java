package com.kingrealms.realms.economy.shop.types;

import org.bukkit.block.Sign;

public interface ISignShop extends IPlaceable, ISingleItem {
    
    Sign getSign();
}