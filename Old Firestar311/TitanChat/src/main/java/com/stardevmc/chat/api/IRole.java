package com.stardevmc.chat.api;

import org.bukkit.entity.Player;

import java.util.List;

public interface IRole {
    boolean hasPermission(Player player, RoomPermission permission);
    
    String getName();
    String getPrefix();
    List<RoomPermission> getPermissions();
    String getFormat();
}