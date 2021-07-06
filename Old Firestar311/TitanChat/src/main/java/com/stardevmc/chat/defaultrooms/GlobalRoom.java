package com.stardevmc.chat.defaultrooms;

import com.stardevmc.chat.Chatroom;
import com.stardevmc.chat.api.IOwner;
import com.stardevmc.chat.api.ServerOwner;
import org.bukkit.Material;

import java.util.UUID;

public class GlobalRoom extends Chatroom {
    
    public GlobalRoom() {
        super("global", new ServerOwner(), "&fGlobal", null, "&8[&fG&8] &r{displayname}&8: &f{message}", Material.STONE);
        this.description = "This is the global channel.";
    }
    
    public void setOwner(IOwner uuid) {}
    
    public void setGlobal(boolean global) {}
    
    public void setDescription(String description) {}
    
    public void removeMember(UUID uuid) {}
    
    public void addBannedUser(UUID uuid) {}
}