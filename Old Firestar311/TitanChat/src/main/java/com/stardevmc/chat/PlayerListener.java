package com.stardevmc.chat;

import com.stardevmc.chat.api.DefaultRoles;
import com.stardevmc.chat.api.IChatroom;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

class PlayerListener implements Listener {
    
    private TitanChat plugin;
    
    public PlayerListener(TitanChat plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getChatroomManager().getGlobalRoom().addMember(event.getPlayer().getUniqueId(), DefaultRoles.MEMBER);
        plugin.getChatroomManager().changeActiveChatroom(event.getPlayer(), plugin.getChatroomManager().getGlobalRoom());
        
        for (IChatroom chatroom : plugin.getChatroomManager().getChatrooms().values()) {
            if (chatroom.isAutoJoin()) {
                if (chatroom.hasPermission(event.getPlayer())) {
                    chatroom.addMember(event.getPlayer().getUniqueId(), DefaultRoles.MEMBER);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (!e.isCancelled()) {
            Player player = e.getPlayer();
            String message = e.getMessage();
            e.setCancelled(true);
    
            IChatroom room = plugin.getChatroomManager().getActiveChatroom(player);
    
            String format = room.formatMessage(player, message);
            room.sendChatMessage(format);
        }
    }
}