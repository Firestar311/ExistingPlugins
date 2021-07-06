package com.stardevmc.titanterritories.core.controller;

import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import org.bukkit.command.Command;

import java.util.Map;

public class TaxController<T extends IHolder> extends Controller<T> {
    
    public TaxController(T holder) {
        super(holder);
    }
    
    private TaxController() {
    
    }
    
    public void handleCommand(Command cmd, T holder, IUser user, String[] args) {
    
    }
    
    public Map<String, Object> serialize() {
        return null;
    }
}