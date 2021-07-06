package com.kingrealms.realms.economy.shop.types;

import com.kingrealms.realms.economy.shop.Shop;
import com.kingrealms.realms.economy.shop.enums.OwnerType;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Map;

public abstract class ServerShop extends Shop {
    public ServerShop() {
        super("Console");
        this.ownerType = OwnerType.SERVER;
    }
    
    public ServerShop(Map<String, Object> serialized) {
        super(serialized);
    }
    
    @Override
    public ConsoleCommandSender getOwner() {
        return Bukkit.getConsoleSender();
    }
}