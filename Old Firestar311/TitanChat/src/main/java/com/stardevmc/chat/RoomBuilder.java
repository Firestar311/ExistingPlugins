package com.stardevmc.chat;

import com.firestar311.lib.util.Utils;
import com.stardevmc.chat.api.IChatroom;
import com.stardevmc.chat.api.IOwner;
import org.bukkit.Material;

public class RoomBuilder {
    private String id;
    private String displayName;
    private String permission;
    private String format;
    private Material icon;
    private String description;
    private IOwner owner;
    private boolean global;
    
    public RoomBuilder(IOwner owner) {
        this.owner = owner;
    }
    
    public IChatroom buildChatRoom() {
        IChatroom room = new Chatroom(id, owner, displayName, permission, format, icon);
        room.setDescription(description);
        room.setGlobal(global);
        return room;
    }
    
    public RoomBuilder setId(String id) {
        this.id = id;
        return this;
    }
    
    public RoomBuilder setDisplayName(String displayName) {
        this.displayName = Utils.color(displayName);
        return this;
    }
    
    public RoomBuilder setPermission(String permission) {
        this.permission = permission;
        return this;
    }
    
    public RoomBuilder setFormat(String format) {
        this.format = format;
        return this;
    }
    
    public RoomBuilder setIcon(Material icon) {
        this.icon = icon;
        return this;
    }
    
    public RoomBuilder setDescription(String description) {
        this.description = Utils.color(description);
        return this;
    }
    
    public RoomBuilder setGlobal(boolean global) {
        this.global = global;
        return this;
    }
}