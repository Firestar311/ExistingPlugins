package com.stardevmc.chat.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public interface IChatroomManager {
    
    void changeActiveChatroom(Player player, IChatroom newActiveRoom);
    Map<String, IChatroom> getChatrooms();
    boolean registerChatroom(IChatroom channel);
    boolean unregisterChatroom(IChatroom channel);
    void openGUI(Player player);
    String extractName(ItemStack stack);
    IChatroom getGlobalRoom();
    IChatroom getStaffRoom();
    
    IChatroom getActiveChatroom(Player player);
    List<IChatroom> getChatrooms(Player player);
    IChatroom getChatroom(String name);
    
    void saveData();
    void loadData();
    
    static String getInventoryTitle() {
        return "Chat Rooms";
    }
}