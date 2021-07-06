package com.stardevmc.chat.api;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface IMember {
    UUID getUniqueId();
    IRole getRole();
    Player getPlayer();
    void sendMessage(String message);
}