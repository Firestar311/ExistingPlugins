package com.kingrealms.realms.economy.shop.types;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public interface IPlaceable {
    ItemStack getPlacer();
    Location getLocation();
    void setLocation(Location location);
}