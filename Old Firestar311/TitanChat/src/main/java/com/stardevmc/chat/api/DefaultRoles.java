package com.stardevmc.chat.api;

import org.bukkit.entity.Player;

import java.util.*;

public enum DefaultRoles implements IRole {
    
    OWNER("&4", new ArrayList<>(Arrays.asList(RoomPermission.values()))),
    MANAGER("&c", new ArrayList<>()), MODERATOR("&e", new ArrayList<>(Collections.singletonList(RoomPermission.BAN))),
    MEMBER("&f", new ArrayList<>(Arrays.asList(RoomPermission.SEND_MESSAGES, RoomPermission.READ_MESSAGES))), INVITED("&7", new ArrayList<>());
    
    private String name, prefix;
    private List<RoomPermission> permissions;
    
    DefaultRoles(String prefix, List<RoomPermission> permissions) {
        this.name = name();
        this.prefix = prefix;
        this.permissions = permissions;
    }
    
    public boolean hasPermission(Player player, RoomPermission permission) {
        if (player.hasPermission("titanchat.admin.edit." + permission.toString().toLowerCase())) {
            return true;
        }
        return permissions.contains(permission);
    }
    
    public String getName() {
        return name;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public List<RoomPermission> getPermissions() {
        return permissions;
    }
    
    public String getFormat() {
        return "{prefix} {name}";
    }
}