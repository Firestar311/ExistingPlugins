package com.stardevmc.chat.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChatroomCommandEvent extends Event {
    
    private static HandlerList handlerList = new HandlerList();
    
    private Player player;
    private String roomId;
    
    public ChatroomCommandEvent(Player player, String roomId) {
        this.player = player;
        this.roomId = roomId;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public String getRoomId() {
        return roomId;
    }
    
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    
    public HandlerList getHandlers() {
        return getHandlerList();
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}