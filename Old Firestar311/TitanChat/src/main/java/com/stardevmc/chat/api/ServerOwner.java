package com.stardevmc.chat.api;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ServerOwner implements IOwner {
 
    public ServerOwner() {
    }
    
    public CommandSender getSender() {
        return Bukkit.getServer().getConsoleSender();
    }
    
    public String toString() {
        return "server";
    }
}