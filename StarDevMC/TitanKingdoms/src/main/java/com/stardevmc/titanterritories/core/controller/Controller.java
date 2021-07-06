package com.stardevmc.titanterritories.core.controller;

import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import org.bukkit.command.Command;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public abstract class Controller<T extends IHolder> implements ConfigurationSerializable {
    
    protected T holder;
    
    public Controller(T holder) {
        this.holder = holder;
    }
    
    protected Controller() {
    }
    
    public T getHolder() {
        return holder;
    }
    
    public void setHolder(T holder) {
        this.holder = holder;
    }
    
    /**
     * Defines the command behavior for the specific controller
     * @param cmd
     * @param holder The holder for the command, each one will have it
     * @param user The user of the command
     * @param args The Objects that were previously generated. Object names will allow easy access to them, and there will always be
     */
    public abstract void handleCommand(Command cmd, T holder, IUser user, String[] args);
}