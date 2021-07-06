package com.stardevmc.titanterritories.core.controller;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.Shop;
import org.bukkit.command.Command;

import java.util.*;

public class ShopController<T extends IHolder> extends Controller<T> {
    
    private Set<Shop> shops = new HashSet<>();
    
    public ShopController(T holder) {
        super(holder);
    }
    
    public void handleCommand(Command cmd, T holder, IUser user, String[] args) {
        if (!(args.length > 1)) {
            user.sendMessage("&cYou must provide a sub command.");
            return;
        }
        
        if (Utils.checkCmdAliases(args, 1, "create", "c", "add", "a")) {
        
        } else if (Utils.checkCmdAliases(args, 1, "delete", "d", "remove", "r")) {
        
        } else if (Utils.checkCmdAliases(args, 1, "modify", "m")) {
        
        } else if (Utils.checkCmdAliases(args, 1, "addchest", "ac")) {
        
        } else if (Utils.checkCmdAliases(args, 1, "addsign", "as")) {
        
        }
    }
    
    //TODO
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        return serialized;
    }
    
    public static ShopController deserialize(Map<String, Object> serialized) {
        
        return null;
    }
}